/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lead_time_table")
public class LeadTime {
    @Id private UUID id;

    private UUID matchingElement;

    private double value;
    int position;

    @ManyToOne
    @JoinColumn(name = "total_lead_time_id")
    private TotalLeadTime totalLeadTime;
}
