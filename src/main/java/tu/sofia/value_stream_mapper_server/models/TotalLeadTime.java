/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Set;
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
@Table(name = "total_lead_time_table")
public class TotalLeadTime {
    @Id private UUID id;

    @Column(name = "value_added")
    private double valueAdded;

    @Column(name = "lead_time")
    private double leadTime;

    @Column(name = "value_added_ratio")
    private double valueAddedRatio;

    @OneToOne(mappedBy = "totalLeadTime")
    private VSMObject vsm;

    @OneToMany(mappedBy = "totalLeadTime", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LeadTime> leadTimes;
}
