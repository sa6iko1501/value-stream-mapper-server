/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tu.sofia.value_stream_mapper_server.dao.ProcessTableDAO;
import tu.sofia.value_stream_mapper_server.dao.TableRowDAO;
import tu.sofia.value_stream_mapper_server.dao.UnitDAO;
import tu.sofia.value_stream_mapper_server.dao.VSMObjectDAO;
import tu.sofia.value_stream_mapper_server.dto.response.vsm.ProcessTableDTO;
import tu.sofia.value_stream_mapper_server.models.ProcessTable;
import tu.sofia.value_stream_mapper_server.models.TableInventory;
import tu.sofia.value_stream_mapper_server.models.TableRow;
import tu.sofia.value_stream_mapper_server.models.Unit;
import tu.sofia.value_stream_mapper_server.models.VSMObject;

@Service
public class ProcessTableService {
    @Autowired private ProcessTableDAO processTableRepository;
    @Autowired private VSMObjectDAO vsmRepo;
    @Autowired private TableRowDAO tableRowRepo;
    @Autowired private UnitDAO unitRepo;

    public List<ProcessTableDTO> updatePositioning(UUID vsmId, List<ProcessTableDTO> dtos) {
        final var vsm = getVsm(vsmId);
        var vsmTables = vsm.getProcessTables();
        List<ProcessTable> vsmTablesOrderedByNewPositioning = new ArrayList<>();
        dtos.stream()
                .sorted(Comparator.comparing(ProcessTableDTO::getPosition))
                .forEach(
                        x -> {
                            var correspondingTable =
                                    vsmTables.stream()
                                            .filter(tbl -> tbl.getId().equals(x.getId()))
                                            .findAny()
                                            .orElseThrow(
                                                    () ->
                                                            new IllegalStateException(
                                                                    "Mismatch between positioning"
                                                                            + " request and actual"
                                                                            + " tables state"));
                            vsmTablesOrderedByNewPositioning.add(correspondingTable);
                        });
        if (vsmTablesOrderedByNewPositioning.size() > 1) {
            for (int i = 0; i < vsmTables.size(); i++) {
                var table = vsmTablesOrderedByNewPositioning.get(i);
                table.setPosition(i + 1);
                if (i == vsmTables.size() - 1) {
                    table.getInventory().setNextInPosition(null);
                } else {
                    table.getInventory()
                            .setNextInPosition(vsmTablesOrderedByNewPositioning.get(i + 1).getId());
                }
            }
        } else {
            var table = vsmTablesOrderedByNewPositioning.getFirst();
            table.setPosition(1);
            table.getInventory().setNextInPosition(null);
        }

        vsmRepo.save(vsm);

        return dtos;
    }

    public ProcessTableDTO updateProcessTable(ProcessTableDTO processTableDTO) {
        var processTableOptional = processTableRepository.findById(processTableDTO.getId());
        if (processTableOptional.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format("No process table with id: %s found", processTableDTO.getId()));
        }

        ProcessTable processTableToUpdate = processTableOptional.get();
        applyChanges(processTableToUpdate, processTableDTO);
        processTableRepository.save(processTableToUpdate);
        return processTableDTO;
    }

    public ProcessTableDTO createProcessTable(ProcessTableDTO dto) {
        final var vsmOptional = vsmRepo.findById(dto.getVsmId());
        final var allUnits = unitRepo.findAll();
        if (vsmOptional.isEmpty()) {
            throw new IllegalArgumentException("No value stream map attached to new processTable");
        }
        final var vsm = vsmOptional.get();
        var processTableToSave = new ProcessTable();
        List<TableRow> newPTableRows =
                dto.getRows().stream()
                        .map(
                                x -> {
                                    return new TableRow(
                                            x.getRowName(),
                                            x.getValue(),
                                            allUnits.stream()
                                                    .filter(
                                                            unit ->
                                                                    unit.getUnitType()
                                                                            .equals(
                                                                                    x.getUnit()
                                                                                            .getUnitType()))
                                                    .findAny()
                                                    .orElseThrow(
                                                            () ->
                                                                    new IllegalArgumentException(
                                                                            "Unit not found: "
                                                                                    + x.getUnit()
                                                                                            .getUnitType())),
                                            x.getRowType(),
                                            processTableToSave);
                                })
                        .toList();
        processTableToSave.setId(UUID.randomUUID());
        processTableToSave.setTitle(dto.getTitle());
        processTableToSave.setVsm(vsm);
        processTableToSave.setPosition(dto.getPosition());
        processTableToSave.setInventory(
                new TableInventory(
                        UUID.randomUUID(),
                        dto.getInventory().getAmount(),
                        vsm.getUnit(),
                        processTableToSave,
                        null));

        processTableToSave.setRows(new HashSet<>(newPTableRows));
        if (!vsm.getProcessTables().isEmpty()) {
            var lastPT =
                    vsm.getProcessTables().stream()
                            .sorted(Comparator.comparing(ProcessTable::getPosition))
                            .toList()
                            .getLast();
            lastPT.getInventory().setNextInPosition(processTableToSave.getId());
        }
        vsm.getProcessTables().add(processTableToSave);
        vsmRepo.save(vsm);
        return dto;
    }

    public boolean deleteTable(UUID tableId) {
        var processTableOptional = processTableRepository.findById(tableId);
        if (processTableOptional.isEmpty()) {
            throw new IllegalArgumentException("No process table found with id: " + tableId);
        }
        var toBeDeleted = processTableOptional.get();
        var vsm = toBeDeleted.getVsm();
        int size = vsm.getProcessTables().size();

        if (toBeDeleted.getPosition() == size) {
            var secondToLastTable =
                    vsm.getProcessTables().stream()
                            .sorted(Comparator.comparing(ProcessTable::getPosition))
                            .toList()
                            .get(size - 2);
            secondToLastTable.getInventory().setNextInPosition(null);
            vsm.getProcessTables().remove(toBeDeleted);
            processTableRepository.delete(toBeDeleted);
        }
        if (toBeDeleted.getPosition() == 1) {
            vsm.getProcessTables().remove(toBeDeleted);
            var tables =
                    vsm.getProcessTables().stream()
                            .sorted(Comparator.comparing(ProcessTable::getPosition))
                            .toList();
            tables.forEach(x -> x.setPosition(x.getPosition() - 1));
            processTableRepository.delete(toBeDeleted);
        }
        if (toBeDeleted.getPosition() > 1 && toBeDeleted.getPosition() < size) {
            var tables =
                    vsm.getProcessTables().stream()
                            .sorted(Comparator.comparing(ProcessTable::getPosition))
                            .toList();
            int indexOfToBeDeleted = tables.indexOf(toBeDeleted);
            var previous = tables.get(indexOfToBeDeleted - 1);
            var next = tables.get(indexOfToBeDeleted + 1);
            previous.getInventory().setNextInPosition(next.getId());
            for (int i = indexOfToBeDeleted + 1; i < tables.size(); i++) {
                var table = tables.get(i);
                table.setPosition(table.getPosition() - 1);
            }
            vsm.getProcessTables().remove(toBeDeleted);
            processTableRepository.delete(toBeDeleted);
        }
        vsmRepo.save(vsm);

        return true;
    }

    private void applyChanges(ProcessTable obj, ProcessTableDTO dto) {

        Set<TableRow> rows = obj.getRows();
        List<Unit> allUnits = unitRepo.findAll();

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            obj.setTitle(dto.getTitle());
        }

        dto.getRows()
                .forEach(
                        rowDto -> {

                            // Existing row
                            if (rowDto.getId() != null) {

                                TableRow row =
                                        rows.stream()
                                                .filter(
                                                        existingRow ->
                                                                existingRow
                                                                        .getId()
                                                                        .equals(rowDto.getId()))
                                                .findFirst()
                                                .orElseThrow(
                                                        () ->
                                                                new IllegalArgumentException(
                                                                        "Table row not found: "
                                                                                + rowDto.getId()));

                                row.setRow_name(rowDto.getRowName());
                                row.setRowType(rowDto.getRowType());
                                row.setValue(rowDto.getValue());

                                Unit unit =
                                        allUnits.stream()
                                                .filter(
                                                        u ->
                                                                u.getUnitType()
                                                                        .equals(
                                                                                rowDto.getUnit()
                                                                                        .getUnitType()))
                                                .findFirst()
                                                .orElseThrow(
                                                        () ->
                                                                new IllegalArgumentException(
                                                                        "Unit not found: "
                                                                                + rowDto.getUnit()
                                                                                        .getUnitType()));

                                row.setUnit(unit);
                            }

                            // New row
                            else {

                                Unit unit =
                                        allUnits.stream()
                                                .filter(
                                                        u ->
                                                                u.getUnitType()
                                                                        .equals(
                                                                                rowDto.getUnit()
                                                                                        .getUnitType()))
                                                .findFirst()
                                                .orElseThrow(
                                                        () ->
                                                                new IllegalArgumentException(
                                                                        "Unit not found: "
                                                                                + rowDto.getUnit()
                                                                                        .getUnitType()));

                                TableRow newRow =
                                        new TableRow(
                                                rowDto.getRowName(),
                                                rowDto.getValue(),
                                                unit,
                                                rowDto.getRowType(),
                                                obj);

                                rows.add(newRow);
                            }
                        });

        List<TableRow> toBeDeleted =
                obj.getRows().stream()
                        .filter(
                                x ->
                                        dto.getRows().stream()
                                                .noneMatch(
                                                        dtoRow ->
                                                                dtoRow.getId() != null
                                                                        && dtoRow.getId()
                                                                                .equals(x.getId())))
                        .filter(x -> x.getId() != null)
                        .toList();
        rows.removeAll(toBeDeleted);
        obj.getInventory().setAmount(dto.getInventory().getAmount());
    }

    private VSMObject getVsm(UUID vsmId) {
        return vsmRepo.findById(vsmId)
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        String.format(
                                                "No value-stream map with the following id"
                                                        + " found: %s",
                                                vsmId)));
    }
}
