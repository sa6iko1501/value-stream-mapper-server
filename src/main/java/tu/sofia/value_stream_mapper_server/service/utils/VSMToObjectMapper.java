/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.service.utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import tu.sofia.value_stream_mapper_server.dto.CustomerDTO;
import tu.sofia.value_stream_mapper_server.dto.CustomerOutputDTO;
import tu.sofia.value_stream_mapper_server.dto.InventoryDTO;
import tu.sofia.value_stream_mapper_server.dto.ProcessTableDTO;
import tu.sofia.value_stream_mapper_server.dto.SupplierDTO;
import tu.sofia.value_stream_mapper_server.dto.SupplyDTO;
import tu.sofia.value_stream_mapper_server.dto.TableRowDTO;
import tu.sofia.value_stream_mapper_server.dto.VSMObjectDTO;
import tu.sofia.value_stream_mapper_server.dto.VSMObjectResultDTO;
import tu.sofia.value_stream_mapper_server.models.ProcessTable;
import tu.sofia.value_stream_mapper_server.models.TableInventory;
import tu.sofia.value_stream_mapper_server.models.TableRow;
import tu.sofia.value_stream_mapper_server.models.VSMObject;

@Service
@NoArgsConstructor
public class VSMToObjectMapper {
    public VSMObjectResultDTO toDTO(VSMObject vsm) {
        UUID id = vsm.getId();
        List<ProcessTableDTO> tables = extractTables(vsm);
        String title = vsm.getTitle();
        LocalDateTime date = vsm.getDate();
        SupplyDTO supply = extractSupply(vsm);
        CustomerOutputDTO customerOutput = extractCustomerOutput(vsm);
        return new VSMObjectResultDTO(
                new VSMObjectDTO(id, title, date, tables, supply, customerOutput));
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
            List<TableRow> rows = pt.getRows().stream().toList();
            if (!rows.isEmpty()) {
                List<TableRowDTO> rowsForDTO = extractTableRows(rows);
                dtoToAdd.setRows(rowsForDTO);
            }
            TableInventory ti = pt.getInventory();
            if (ti != null) {
                dtoToAdd.setInventory(
                        new InventoryDTO(ti.getId(), ti.getAmount(), vsm.getUnit().getName()));
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
            var unit = rowFromObject.getUnit();
            if (unit != null) {
                rowToAdd.setUnit(unit);
            }
            var value = rowFromObject.getValue();
            if (value != null) {
                rowToAdd.setValue(value);
            }
            result.add(rowToAdd);
        }
        return result.stream().sorted(Comparator.comparing(TableRowDTO::getRowName)).toList();
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
                supply.getId(), suppliers, supply.getAmount(), supply.getUnit().getName());
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
                customerOutput.getUnit().getName());
    }
}
