/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tu.sofia.value_stream_mapper_server.dao.VSMObjectDAO;
import tu.sofia.value_stream_mapper_server.dto.request.VSMSettingsRequestDTO;
import tu.sofia.value_stream_mapper_server.dto.response.vsm.VSMDashboardResultDTO;
import tu.sofia.value_stream_mapper_server.dto.response.vsm.VSMObjectDTO;
import tu.sofia.value_stream_mapper_server.dto.response.vsm.VSMObjectResultDTO;
import tu.sofia.value_stream_mapper_server.models.Customer;
import tu.sofia.value_stream_mapper_server.models.CustomerOutput;
import tu.sofia.value_stream_mapper_server.models.LeadTime;
import tu.sofia.value_stream_mapper_server.models.ProcessTable;
import tu.sofia.value_stream_mapper_server.models.RowType;
import tu.sofia.value_stream_mapper_server.models.Supplier;
import tu.sofia.value_stream_mapper_server.models.Supply;
import tu.sofia.value_stream_mapper_server.models.TableInventory;
import tu.sofia.value_stream_mapper_server.models.TableRow;
import tu.sofia.value_stream_mapper_server.models.TotalLeadTime;
import tu.sofia.value_stream_mapper_server.models.Unit;
import tu.sofia.value_stream_mapper_server.models.UnitType;
import tu.sofia.value_stream_mapper_server.models.VSMObject;
import tu.sofia.value_stream_mapper_server.service.utils.LeadTimeUtils;
import tu.sofia.value_stream_mapper_server.service.utils.VSMToDTOMapper;
import tu.sofia.value_stream_mapper_server.service.utils.VSMToIDAndDateMapper;

@Service
@AllArgsConstructor
public class VSMService {
    @Autowired private final VSMObjectDAO vsmRepo;
    @Autowired private final VSMToIDAndDateMapper vsmToIDAndDateMapper;
    @Autowired private final VSMToDTOMapper vsmToDTOMapper;
    @Autowired private final SupplyService supplyService;
    @Autowired private final UnitService unitService;
    @Autowired private final CustomerService customerService;
    @Autowired private final ProcessTableService processTableService;
    @Autowired private final LeadTimeService leadTimeService;

    public VSMDashboardResultDTO findAllSavedVSMIds() {
        List<VSMObject> allVSM = vsmRepo.findAll(Sort.by(Sort.Order.desc("date")));

        return vsmToIDAndDateMapper.toDTO(allVSM);
    }

    public VSMObjectResultDTO findById(UUID id) {
        return vsmToDTOMapper.toDTO(getVsm(id));
    }

    public VSMSettingsRequestDTO updateSettings(UUID vsmId, VSMSettingsRequestDTO dto) {
        var vsm = getVsm(vsmId);
        vsm.setShifts(dto.getShifts());
        vsm.setBreaktime(dto.getBreaktime());
        vsm.setLengthOfShifts(dto.getLengthOfShifts());
        applyUpdatesToProcessTables(vsm);
        vsm.getTotalLeadTime().getLeadTimes().clear();
        HashMap<Double, Double> leadTimesMap =
                LeadTimeUtils.createLeadTimesMap(vsm.getProcessTables().stream().toList(), vsm);
        LeadTimeUtils.createLeadTimes(
                leadTimesMap,
                vsm.getProcessTables().stream().toList(),
                vsm.getTotalLeadTime(),
                vsm);
        vsmRepo.save(vsm);
        return dto;
    }

    private void applyUpdatesToProcessTables(VSMObject vsm) {
        var customerOutput = vsm.getCustomerOutput();
        customerOutput.setTakt(customerService.calculateTakt(customerOutput, vsm));
        List<ProcessTable> processTables = vsm.getProcessTables().stream().toList();
        processTables.forEach(
                processTable -> {
                    processTable.getRows().stream()
                            .filter(x -> x.getRowType().equals(RowType.TOTAL_AVAILABLE_TIME))
                            .findAny()
                            .ifPresent(x -> processTable.getRows().remove(x));
                    processTable
                            .getRows()
                            .add(
                                    processTableService
                                            .createTotalAvailableTimeRow(processTable, vsm)
                                            .orElseThrow(
                                                    () ->
                                                            new RuntimeException(
                                                                    "Something went wrong")));
                });
    }

    public VSMObjectResultDTO createVsmObj(VSMObjectDTO dto) {
        final var units = unitService.getAllUnits();
        final var unit =
                units.stream()
                        .filter(x -> x.getId().equals(dto.getUnit().getId()))
                        .findAny()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                String.format(
                                                        "Mismatch between request and actual state"
                                                                + " for unit %s",
                                                        dto.getUnit().getUnitType())));
        VSMObject objToCreate = new VSMObject();
        objToCreate.setTitle(dto.getTitle());
        objToCreate.setId(UUID.randomUUID());
        objToCreate.setUnit(unit);
        objToCreate.setShifts(dto.getShifts());
        objToCreate.setLengthOfShifts(dto.getLengthOfShifts());
        objToCreate.setBreaktime(dto.getBreaktime());
        objToCreate.setDate(LocalDateTime.now());

        List<ProcessTable> placeHolderTables = initPlaceHolderTables(objToCreate, units);
        Supply placeHolderSupply = initPlaceHolderSupply(objToCreate);
        objToCreate.setCustomerOutput(initPlaceHolderCustomerOutput(objToCreate));
        objToCreate.setProcessTables(new HashSet<>(placeHolderTables));
        objToCreate.setSupply(placeHolderSupply);

        TotalLeadTime totalLeadTime =
                new TotalLeadTime(UUID.randomUUID(), 0, 0, 0, objToCreate, new HashSet<>());
        objToCreate.setTotalLeadTime(totalLeadTime);
        applyUpdatesToProcessTables(objToCreate);
        HashMap<Double, Double> leadTimesMap =
                LeadTimeUtils.createLeadTimesMap(
                        objToCreate.getProcessTables().stream().toList(), objToCreate);
        List<LeadTime> calculateLeadTimes =
                LeadTimeUtils.createLeadTimes(
                        leadTimesMap,
                        objToCreate.getProcessTables().stream().toList(),
                        totalLeadTime,
                        objToCreate);
        leadTimeService.calculateTotalLeadTime(objToCreate.getTotalLeadTime(), objToCreate);
        vsmRepo.save(objToCreate);
        return vsmToDTOMapper.toDTO(objToCreate);
    }

    public VSMObjectResultDTO updateVSMTitle(UUID vsmId, String title) {
        var vsm = getVsm(vsmId);
        vsm.setTitle(title);
        vsmRepo.save(vsm);
        return vsmToDTOMapper.toDTO(vsm);
    }

    public boolean deleteVSM(UUID vsmId) {
        var toBeDeleted = getVsm(vsmId);
        vsmRepo.delete(toBeDeleted);
        return true;
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

    private List<ProcessTable> initPlaceHolderTables(VSMObject vsm, List<Unit> units) {
        ProcessTable pt1 = new ProcessTable();
        pt1.setVsm(vsm);
        pt1.setPosition(1);
        pt1.setId(UUID.randomUUID());
        pt1.setTitle("My first process");
        pt1.setRows(new HashSet<>(initPlaceHolderRows(pt1, units)));
        processTableService
                .createTotalAvailableTimeRow(pt1, vsm)
                .ifPresent(totalTime -> pt1.getRows().add(totalTime));
        ProcessTable pt2 = new ProcessTable();
        pt2.setVsm(vsm);
        pt2.setPosition(2);
        pt2.setId(UUID.randomUUID());
        pt2.setTitle("My second process");
        pt2.setRows(new HashSet<>(initPlaceHolderRows(pt2, units)));
        processTableService
                .createTotalAvailableTimeRow(pt2, vsm)
                .ifPresent(totalTime -> pt2.getRows().add(totalTime));
        ProcessTable pt3 = new ProcessTable();
        pt3.setVsm(vsm);
        pt3.setPosition(3);
        pt3.setId(UUID.randomUUID());
        pt3.setTitle("My third process");
        pt3.setRows(new HashSet<>(initPlaceHolderRows(pt3, units)));
        processTableService
                .createTotalAvailableTimeRow(pt3, vsm)
                .ifPresent(totalTime -> pt3.getRows().add(totalTime));
        ProcessTable pt4 = new ProcessTable();
        pt4.setVsm(vsm);
        pt4.setPosition(4);
        pt4.setId(UUID.randomUUID());
        pt4.setTitle("My fourth process");
        pt4.setRows(new HashSet<>(initPlaceHolderRows(pt4, units)));
        processTableService
                .createTotalAvailableTimeRow(pt4, vsm)
                .ifPresent(totalTime -> pt4.getRows().add(totalTime));

        pt1.setInventory(
                new TableInventory(
                        UUID.randomUUID(),
                        BigDecimal.valueOf(160.0 + Math.random() * 320)
                                .setScale(0, RoundingMode.UP)
                                .doubleValue(),
                        vsm.getUnit(),
                        pt1,
                        pt2.getId()));
        pt2.setInventory(
                new TableInventory(
                        UUID.randomUUID(),
                        BigDecimal.valueOf(160.0 + Math.random() * 320)
                                .setScale(0, RoundingMode.UP)
                                .doubleValue(),
                        vsm.getUnit(),
                        pt2,
                        pt3.getId()));
        pt3.setInventory(
                new TableInventory(
                        UUID.randomUUID(),
                        BigDecimal.valueOf(160.0 + Math.random() * 320)
                                .setScale(0, RoundingMode.UP)
                                .doubleValue(),
                        vsm.getUnit(),
                        pt3,
                        pt4.getId()));
        pt4.setInventory(
                new TableInventory(
                        UUID.randomUUID(),
                        BigDecimal.valueOf(160.0 + Math.random() * 320)
                                .setScale(0, RoundingMode.UP)
                                .doubleValue(),
                        vsm.getUnit(),
                        pt4,
                        null));
        return List.of(pt1, pt2, pt3, pt4);
    }

    private List<TableRow> initPlaceHolderRows(ProcessTable processTable, List<Unit> units) {
        TableRow processTime = new TableRow();
        processTime.setId(UUID.randomUUID());
        processTime.setUnit(
                units.stream()
                        .filter(x -> x.getUnitType().equals(UnitType.SECOND))
                        .findAny()
                        .get());
        processTime.setRowType(RowType.PROCESS_TIME);
        processTime.setProcessTable(processTable);
        processTime.setRow_name("Process Time");
        processTime.setValue(
                BigDecimal.valueOf(80 + Math.random() * 265.0)
                        .setScale(0, RoundingMode.UP)
                        .doubleValue());
        TableRow cycleTime = new TableRow();
        cycleTime.setId(UUID.randomUUID());
        cycleTime.setUnit(
                units.stream()
                        .filter(x -> x.getUnitType().equals(UnitType.SECOND))
                        .findAny()
                        .get());
        cycleTime.setRowType(RowType.CYCLE_TIME);
        cycleTime.setProcessTable(processTable);
        cycleTime.setRow_name("Cycle Time");
        cycleTime.setValue(
                BigDecimal.valueOf(80 + Math.random() * 520)
                        .setScale(0, RoundingMode.UP)
                        .doubleValue());
        TableRow uptime = new TableRow();
        uptime.setId(UUID.randomUUID());
        uptime.setUnit(
                units.stream()
                        .filter(x -> x.getUnitType().equals(UnitType.PERCENT))
                        .findAny()
                        .get());
        uptime.setRowType(RowType.UPTIME);
        uptime.setProcessTable(processTable);
        uptime.setRow_name("Uptime");
        uptime.setValue(
                BigDecimal.valueOf(65.0 + Math.random() * 35)
                        .setScale(0, RoundingMode.UP)
                        .doubleValue());
        TableRow workers = new TableRow();
        workers.setId(UUID.randomUUID());
        workers.setUnit(
                units.stream()
                        .filter(x -> x.getUnitType().equals(UnitType.NUMBER))
                        .findAny()
                        .get());
        workers.setRowType(RowType.WORKERS);
        workers.setProcessTable(processTable);
        workers.setRow_name("Workers");
        workers.setValue((double) (1 + (int) (Math.random() * 12)));
        TableRow changeOverTime = new TableRow();
        changeOverTime.setId(UUID.randomUUID());
        changeOverTime.setUnit(
                units.stream().filter(x -> x.getUnitType().equals(UnitType.SECOND)).findAny().get());
        changeOverTime.setRowType(RowType.CO);
        changeOverTime.setProcessTable(processTable);
        changeOverTime.setRow_name("Changeover Time");
        changeOverTime.setValue(
                BigDecimal.valueOf(300 + Math.random() * 3600).setScale(1, RoundingMode.UP).doubleValue());

        return List.of(cycleTime, uptime, changeOverTime, workers, processTime);
    }

    private Supply initPlaceHolderSupply(VSMObject vsm) {
        Supply supply = new Supply();
        supply.setId(UUID.randomUUID());
        supply.setUnit(vsm.getUnit());
        supply.setVsm(vsm);
        supply.setSuppliers(new HashSet<>(initPlaceHolderSuppliers(supply)));
        supply.setAmount(supplyService.calculateTotalSupply(supply));
        supply.setInventoryAmount(
                BigDecimal.valueOf(1800 + Math.random() * 3800)
                        .setScale(0, RoundingMode.UP)
                        .doubleValue());
        return supply;
    }

    private List<Supplier> initPlaceHolderSuppliers(Supply supply) {
        Supplier vratsaInc = new Supplier();
        vratsaInc.setId(UUID.randomUUID());
        vratsaInc.setSupply(supply);
        vratsaInc.setName("Vratsa Enterprises inc.");
        vratsaInc.setUnitName(supply.getUnit().getName());
        vratsaInc.setDailySupply(
                BigDecimal.valueOf(600.0 + Math.random() * 3800)
                        .setScale(0, RoundingMode.UP)
                        .doubleValue());

        Supplier technicalSupplier = new Supplier();
        technicalSupplier.setId(UUID.randomUUID());
        technicalSupplier.setSupply(supply);
        technicalSupplier.setName("MBIS Student's Alliance");
        technicalSupplier.setUnitName(supply.getUnit().getName());
        technicalSupplier.setDailySupply(
                BigDecimal.valueOf(200 + Math.random() * 1800)
                        .setScale(0, RoundingMode.UP)
                        .doubleValue());
        return List.of(vratsaInc, technicalSupplier);
    }

    private CustomerOutput initPlaceHolderCustomerOutput(VSMObject vsm) {
        CustomerOutput customerOutput = new CustomerOutput();
        customerOutput.setVsm(vsm);
        customerOutput.setId(UUID.randomUUID());
        customerOutput.setUnit(vsm.getUnit());
        customerOutput.setCustomers(new HashSet<>(initPlaceHolderCustomers(customerOutput)));
        customerOutput.setAmount(customerService.calculateTotalRequirement(customerOutput));
        customerOutput.setTakt(customerService.calculateTakt(customerOutput, vsm));
        return customerOutput;
    }

    private List<Customer> initPlaceHolderCustomers(CustomerOutput customerOutput) {
        Customer technical = new Customer();
        technical.setId(UUID.randomUUID());
        technical.setUnitName(customerOutput.getUnit().getName());
        technical.setCustomerOutput(customerOutput);
        technical.setDailyRequiredOutput(
                BigDecimal.valueOf(200 + Math.random() * 3800)
                        .setScale(0, RoundingMode.UP)
                        .doubleValue());
        technical.setName("Technical University of Sofia");
        return List.of(technical);
    }
}
