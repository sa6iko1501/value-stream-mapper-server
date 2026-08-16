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
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@jakarta.persistence.Table(name = "process_table")
public class ProcessTable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "inventory_id", referencedColumnName = "id")
    private TableInventory inventory;

    @JsonIgnore
    @OneToMany(mappedBy = "processTable", cascade = CascadeType.REMOVE)
    private Set<TableRow> rows;

    @ManyToOne
    @JoinColumn(name = "vsm_id")
    private VSMObject vsm;

    public ProcessTable(
            UUID id, String title, TableInventory inventory, Set<TableRow> rows, VSMObject vsm) {
        this.id = id;
        this.title = title;
        this.inventory = inventory;
        this.rows = rows;
        this.vsm = vsm;
    }
}
