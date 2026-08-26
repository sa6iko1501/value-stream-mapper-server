/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "table_row")
@NoArgsConstructor
@AllArgsConstructor
public class TableRow {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String row_name;
    private Double value;

    @ManyToOne
    @JoinColumn(name = "unit_id")
    private Unit unit;

    private RowType rowType;

    @ManyToOne
    @JoinColumn(name = "process_table_id", nullable = false)
    private ProcessTable processTable;

    public TableRow(
            String row_name, Double value, Unit unit, RowType rowType, ProcessTable processTable) {
        this.row_name = row_name;
        this.value = value;
        this.unit = unit;
        this.rowType = rowType;
        this.processTable = processTable;
    }
}
