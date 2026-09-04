/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.service.utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import tu.sofia.value_stream_mapper_server.dto.response.unit.UnitDTO;
import tu.sofia.value_stream_mapper_server.dto.response.vsm.CustomerDTO;
import tu.sofia.value_stream_mapper_server.dto.response.vsm.CustomerOutputDTO;
import tu.sofia.value_stream_mapper_server.dto.response.vsm.InventoryDTO;
import tu.sofia.value_stream_mapper_server.dto.response.vsm.LeadTimeDTO;
import tu.sofia.value_stream_mapper_server.dto.response.vsm.ProcessTableDTO;
import tu.sofia.value_stream_mapper_server.dto.response.vsm.SupplierDTO;
import tu.sofia.value_stream_mapper_server.dto.response.vsm.SupplyDTO;
import tu.sofia.value_stream_mapper_server.dto.response.vsm.TableRowDTO;
import tu.sofia.value_stream_mapper_server.dto.response.vsm.TotalLeadTimeDTO;
import tu.sofia.value_stream_mapper_server.dto.response.vsm.VSMObjectDTO;
import tu.sofia.value_stream_mapper_server.dto.response.vsm.VSMObjectResultDTO;
import tu.sofia.value_stream_mapper_server.models.LeadTime;
import tu.sofia.value_stream_mapper_server.models.ProcessTable;
import tu.sofia.value_stream_mapper_server.models.TableInventory;
import tu.sofia.value_stream_mapper_server.models.TableRow;
import tu.sofia.value_stream_mapper_server.models.TotalLeadTime;
import tu.sofia.value_stream_mapper_server.models.Unit;
import tu.sofia.value_stream_mapper_server.models.VSMObject;

@Service
@NoArgsConstructor
public class VSMToDTOMapper {
    public VSMObjectResultDTO toDTO(VSMObject vsm) {
        UUID id = vsm.getId();
        List<ProcessTableDTO> tables = extractTables(vsm);
        String title = vsm.getTitle();
        LocalDateTime date = vsm.getDate();
        int shifts = vsm.getShifts();
        double lengthOfShifts = vsm.getLengthOfShifts();
        int breakTime = vsm.getBreaktime();
        SupplyDTO supply = extractSupply(vsm);
        CustomerOutputDTO customerOutput = extractCustomerOutput(vsm);
        Unit unit = vsm.getUnit();
        TotalLeadTimeDTO totalLeadTimeDTO = extractTotalLeadTime(vsm);
        return new VSMObjectResultDTO(
                new VSMObjectDTO(
                        id,
                        title,
                        date,
                        shifts,
                        lengthOfShifts,
                        breakTime,
                        tables,
                        supply,
                        new UnitDTO(unit.getId(), unit.getName(), unit.getUnitType()),
                        customerOutput,
                        totalLeadTimeDTO));
    }

    private List<ProcessTableDTO> extractTables(VSMObject vsm) {
        List<ProcessTableDTO> result = new ArrayList<>();
        List<ProcessTable> pts =
                vsm.getProcessTables().stream()
                        .sorted(Comparator.comparing(ProcessTable::getPosition))
                        .toList();
        if (pts.isEmpty()) {
            return result;
        }
        for (ProcessTable pt : pts) {
            ProcessTableDTO dtoToAdd = new ProcessTableDTO();
            dtoToAdd.setId(pt.getId());
            dtoToAdd.setTitle(pt.getTitle());
            dtoToAdd.setPosition(pt.getPosition());
            dtoToAdd.setVsmId(vsm.getId());
            List<TableRow> rows = pt.getRows().stream().toList();
            if (!rows.isEmpty()) {
                List<TableRowDTO> rowsForDTO = extractTableRows(rows);
                dtoToAdd.setRows(rowsForDTO);
            }
            TableInventory ti = pt.getInventory();
            if (ti != null) {
                dtoToAdd.setInventory(
                        new InventoryDTO(
                                ti.getId(),
                                ti.getAmount(),
                                vsm.getUnit().getName(),
                                ti.getNextInPosition()));
            }
            result.add(dtoToAdd);
        }
        return result;
    }

    private List<TableRowDTO> extractTableRows(List<TableRow> rows) {
        List<TableRowDTO> result = new ArrayList<>();
        for (TableRow rowFromObject : rows) {
            TableRowDTO rowToAdd = new TableRowDTO();
            rowToAdd.setId(rowFromObject.getId());
            rowToAdd.setRowName(rowFromObject.getRow_name());
            rowToAdd.setRowType(rowFromObject.getRowType());
            var unit = rowFromObject.getUnit();
            if (unit != null) {
                rowToAdd.setUnit(new UnitDTO(unit.getId(), unit.getName(), unit.getUnitType()));
            }
            var value = rowFromObject.getValue();
            if (value != null) {
                rowToAdd.setValue(value);
            }
            result.add(rowToAdd);
        }
        return result.stream().sorted(Comparator.comparing(TableRowDTO::getRowType)).toList();
    }

    private SupplyDTO extractSupply(VSMObject vsm) {
        var supply = vsm.getSupply();
        List<SupplierDTO> suppliers =
                supply.getSuppliers().stream()
                        .map(
                                x ->
                                        new SupplierDTO(
                                                x.getId(),
                                                x.getName(),
                                                x.getUnitName(),
                                                x.getDailySupply()))
                        .toList();
        return new SupplyDTO(
                supply.getId(),
                suppliers,
                supply.getAmount(),
                supply.getInventoryAmount(),
                supply.getUnit().getName());
    }

    private CustomerOutputDTO extractCustomerOutput(VSMObject vsm) {
        var customerOutput = vsm.getCustomerOutput();
        List<CustomerDTO> customers =
                customerOutput.getCustomers().stream()
                        .map(
                                x ->
                                        new CustomerDTO(
                                                x.getId(),
                                                x.getName(),
                                                x.getUnitName(),
                                                x.getDailyRequiredOutput()))
                        .toList();
        return new CustomerOutputDTO(
                customerOutput.getId(),
                customers,
                customerOutput.getAmount(),
                customerOutput.getUnit().getName(),
                customerOutput.getTakt());
    }

    public TotalLeadTimeDTO extractTotalLeadTime(VSMObject vsm){
        var totalLeadTime = vsm.getTotalLeadTime();
        var totalLeadTimeDTO = new TotalLeadTimeDTO();
        totalLeadTimeDTO.setId(totalLeadTime.getId());
        totalLeadTimeDTO.setValueAdded(totalLeadTime.getValueAdded());
        totalLeadTimeDTO.setLeadTime(totalLeadTime.getLeadTime());
        totalLeadTimeDTO.setLeadTimes(extractLeadTimes(totalLeadTime));
        return totalLeadTimeDTO;
    }

    public List<LeadTimeDTO> extractLeadTimes(TotalLeadTime totalLeadTime){
        return totalLeadTime.getLeadTimes().stream().sorted(Comparator.comparing(LeadTime::getPosition)).map(x -> {
            var leadTimeDTO = new LeadTimeDTO();
            leadTimeDTO.setId(x.getId());
            leadTimeDTO.setPosition(x.getPosition());
            leadTimeDTO.setMatchingElement(x.getMatchingElement());
            leadTimeDTO.setValue(x.getValue());
            return leadTimeDTO;
        }).collect(Collectors.toList());
    }
}
