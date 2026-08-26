/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.service;

import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tu.sofia.value_stream_mapper_server.dao.ProcessTableDAO;
import tu.sofia.value_stream_mapper_server.dao.TableRowDAO;
import tu.sofia.value_stream_mapper_server.dao.UnitDAO;
import tu.sofia.value_stream_mapper_server.dto.response.vsm.ProcessTableDTO;
import tu.sofia.value_stream_mapper_server.models.ProcessTable;
import tu.sofia.value_stream_mapper_server.models.TableRow;
import tu.sofia.value_stream_mapper_server.models.Unit;

@Service
public class ProcessTableService {
    @Autowired private ProcessTableDAO processTableRepository;
    @Autowired private TableRowDAO tableRowRepo;
    @Autowired private UnitDAO unitRepo;

    @Transactional
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

    private void applyChanges(ProcessTable obj, ProcessTableDTO dto) {

        Set<TableRow> rows = obj.getRows();
        List<Unit> allUnits = unitRepo.findAll();

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
    }
}
