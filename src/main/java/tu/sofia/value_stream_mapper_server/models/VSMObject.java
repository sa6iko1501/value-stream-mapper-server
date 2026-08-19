/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "vsm")
@AllArgsConstructor
public class VSMObject {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "unit_id")
    private Unit unit;

    @JsonIgnore
    @OneToMany(mappedBy = "vsm", cascade = CascadeType.REMOVE)
    private Set<ProcessTable> processTables;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "supply_id", referencedColumnName = "id")
    private Supply supply;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "customer_output_id", referencedColumnName = "id")
    private CustomerOutput customerOutput;

    public VSMObject() {}
}
