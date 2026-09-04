/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.service.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import tu.sofia.value_stream_mapper_server.models.LeadTime;
import tu.sofia.value_stream_mapper_server.models.ProcessTable;
import tu.sofia.value_stream_mapper_server.models.RowType;
import tu.sofia.value_stream_mapper_server.models.TotalLeadTime;
import tu.sofia.value_stream_mapper_server.models.VSMObject;

public class LeadTimeUtils {
    public static List<LeadTime> createLeadTimes(
            HashMap<Double, Double> leadTimeMap,
            List<ProcessTable> processTables,
            TotalLeadTime totalLeadTime,
            VSMObject vsm) {
        List<ProcessTable> ordered =
                processTables.stream()
                        .sorted(Comparator.comparing(ProcessTable::getPosition))
                        .toList();
        double actualIndex = 0.5;
        double taktPosition = (double) leadTimeMap.size() / 2;
        List<LeadTime> result = new ArrayList<>();
        for (int i = 0; i < leadTimeMap.size(); i++) {
            Double value = leadTimeMap.get(actualIndex);
            int casted = (int) actualIndex;
            if (casted != actualIndex) {
                if (actualIndex == 0.5) {
                    BigDecimal leadTimeValue =
                            BigDecimal.valueOf(value)
                                    .multiply(
                                            BigDecimal.valueOf(leadTimeMap.get(actualIndex + 0.5)));
                    LeadTime leadTime =
                            new LeadTime(
                                    UUID.randomUUID(),
                                    vsm.getSupply().getId(),
                                    leadTimeValue.setScale(0, RoundingMode.UP).doubleValue(),
                                    i + 1,
                                    totalLeadTime);
                    totalLeadTime.getLeadTimes().add(leadTime);
                    result.add(leadTime);
                } else {
                    UUID inventoryId = ordered.get(casted - 1).getInventory().getId();
                    BigDecimal leadTimeValue =
                            BigDecimal.valueOf(value)
                                    .multiply(
                                            BigDecimal.valueOf(leadTimeMap.get(actualIndex + 0.5)));
                    LeadTime leadTime =
                            new LeadTime(
                                    UUID.randomUUID(),
                                    inventoryId,
                                    leadTimeValue.setScale(0, RoundingMode.UP).doubleValue(),
                                    i + 1,
                                    totalLeadTime);
                    totalLeadTime.getLeadTimes().add(leadTime);
                    result.add(leadTime);
                }
            } else {
                if (actualIndex == taktPosition) {
                    UUID customerOutputId = vsm.getCustomerOutput().getId();
                    LeadTime leadTime =
                            new LeadTime(
                                    UUID.randomUUID(),
                                    customerOutputId,
                                    vsm.getCustomerOutput().getTakt(),
                                    i + 1,
                                    totalLeadTime);
                    totalLeadTime.getLeadTimes().add(leadTime);
                    result.add(leadTime);
                } else {
                    UUID processTableId = ordered.get(casted - 1).getId();
                    LeadTime leadTime =
                            new LeadTime(
                                    UUID.randomUUID(), processTableId, value, i + 1, totalLeadTime);
                    totalLeadTime.getLeadTimes().add(leadTime);
                    result.add(leadTime);
                }
            }
            actualIndex += 0.5;
        }
        return result;
    }

    public static HashMap<Double, Double> createLeadTimesMap(
            List<ProcessTable> processTables, VSMObject vsm) {
        List<ProcessTable> ordered =
                processTables.stream()
                        .sorted(Comparator.comparing(ProcessTable::getPosition))
                        .toList();
        var supplyInventoryAmount = vsm.getSupply().getInventoryAmount();
        // Due to the strange nature of the calculation we will store the values by position. Ex:
        // table 3 will be stored as KV pair(3,value) it's inventory will be stored as KV pair((3,5)
        // , value)
        HashMap<Double, Double> valuesByPositionMap = new HashMap<>();
        valuesByPositionMap.put(0.5, supplyInventoryAmount);
        Double lastPosition = null;
        for (ProcessTable processTable : ordered) {
            double processTablePosition = processTable.getPosition();
            valuesByPositionMap.put(
                    Double.valueOf(processTablePosition),
                    getCycleTimeInSecondsDouble(processTable));
            valuesByPositionMap.put(
                    Double.valueOf(processTablePosition + 0.5),
                    processTable.getInventory().getAmount());
            lastPosition = Double.valueOf(processTablePosition + 0.5);
        }
        if (lastPosition != null) {
            valuesByPositionMap.put(lastPosition + 0.5, vsm.getCustomerOutput().getTakt());
        }
        return valuesByPositionMap;
    }

    public static Double getCycleTimeInSecondsDouble(ProcessTable processTable) {
        return Double.valueOf(getCycleTimeInSeconds(processTable).doubleValue());
    }

    public static BigDecimal getCycleTimeInSeconds(ProcessTable processTable) {
        return BigDecimal.valueOf(
                        processTable.getRows().stream()
                                .filter(x -> x.getRowType().equals(RowType.CYCLE_TIME))
                                .findAny()
                                .orElseThrow(
                                        () ->
                                                new IllegalStateException(
                                                        "Lead time calculation exception. Table"
                                                            + " does not contain cycle time row"))
                                .getValue())
                .setScale(0, RoundingMode.UP);
    }
}
