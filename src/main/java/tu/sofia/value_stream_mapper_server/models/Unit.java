/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "unit_table")
public class Unit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private UnitType unitType;

    @OneToMany(mappedBy = "unit")
    @JsonIgnore
    private Set<VSMObject> vsm;

    @OneToMany(mappedBy = "unit")
    @JsonIgnore
    private Set<TableInventory> inventories;

    @OneToMany(mappedBy = "unit")
    @JsonIgnore
    private Set<Supply> supplies;

    @OneToMany(mappedBy = "unit")
    private Set<TableRow> rows;
}
