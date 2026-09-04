/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "vsm")
@AllArgsConstructor
@NoArgsConstructor
public class VSMObject {
    @Id private UUID id;

    private String title;
    private LocalDateTime date;

    private int shifts;

    @Column(name = "length_of_shifts")
    private double lengthOfShifts;

    private int breaktime;

    @ManyToOne
    @JoinColumn(name = "unit_id")
    private Unit unit;

    @OneToMany(mappedBy = "vsm", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProcessTable> processTables;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "supply_id", referencedColumnName = "id")
    private Supply supply;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "customer_output_id", referencedColumnName = "id")
    private CustomerOutput customerOutput;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "total_lead_time_id", referencedColumnName = "id")
    private TotalLeadTime totalLeadTime;
}
