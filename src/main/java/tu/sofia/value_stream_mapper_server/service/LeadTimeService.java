/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import tu.sofia.value_stream_mapper_server.models.LeadTime;
import tu.sofia.value_stream_mapper_server.models.ProcessTable;
import tu.sofia.value_stream_mapper_server.models.RowType;
import tu.sofia.value_stream_mapper_server.models.TotalLeadTime;
import tu.sofia.value_stream_mapper_server.models.VSMObject;

@Service
@AllArgsConstructor
@Getter
@Setter
public class LeadTimeService {

    public void calculateTotalLeadTime(TotalLeadTime totalLeadTime, VSMObject vsm) {
        List<LeadTime> leadTimes =
                totalLeadTime.getLeadTimes().stream()
                        .sorted(Comparator.comparing(LeadTime::getPosition))
                        .toList();
        BigDecimal totalLeadTimeInSeconds = new BigDecimal(0);
        BigDecimal totalValueAddedTime = new BigDecimal(0);

        for (int i = 1; i <= leadTimes.size(); i++) {
            if (i % 2 != 0) {
                totalLeadTimeInSeconds =
                        totalLeadTimeInSeconds.add(
                                BigDecimal.valueOf(getByPosition(i, leadTimes).getValue()));
            } else {
                totalValueAddedTime =
                        totalValueAddedTime.add(
                                BigDecimal.valueOf(getByPosition(i, leadTimes).getValue()));
            }
        }
        totalLeadTime.setLeadTime(
                totalLeadTimeInSeconds.setScale(0, RoundingMode.UP).doubleValue());
        totalLeadTime.setValueAdded(totalValueAddedTime.setScale(0, RoundingMode.UP).doubleValue());
        totalLeadTime.setValueAddedRatio(
                totalValueAddedTime
                        .divide(totalLeadTimeInSeconds, 8, RoundingMode.UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(4, RoundingMode.UP)
                        .doubleValue());
    }

    public BigDecimal getTotalAvailableTimeInSeconds(ProcessTable processTable) {
        return BigDecimal.valueOf(
                        processTable.getRows().stream()
                                .filter(x -> x.getRowType().equals(RowType.TOTAL_AVAILABLE_TIME))
                                .findAny()
                                .orElseThrow(
                                        () ->
                                                new IllegalStateException(
                                                        "Lead time calculation exception. Table"
                                                            + " does not contain total available"
                                                            + " time row"))
                                .getValue())
                .setScale(0, RoundingMode.UP);
    }

    public Double getTotalAvailableTimeInSecondsDouble(ProcessTable processTable) {
        return Double.valueOf(getTotalAvailableTimeInSeconds(processTable).doubleValue());
    }

    public BigDecimal getCycleTimeInSeconds(ProcessTable processTable) {
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

    public Double getCycleTimeInSecondsDouble(ProcessTable processTable) {
        return Double.valueOf(getCycleTimeInSeconds(processTable).doubleValue());
    }

    private LeadTime getByPosition(int position, List<LeadTime> leadTimes) {
        return leadTimes.stream()
                .filter(x -> x.getPosition() == position)
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Position doesn't exist"));
    }
}
