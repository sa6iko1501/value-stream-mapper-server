/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
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
    @Id private UUID id;

    private String title;
    private int position;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "inventory_id", referencedColumnName = "id")
    private TableInventory inventory;

    @OneToMany(mappedBy = "processTable", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TableRow> rows;

    @ManyToOne
    @JoinColumn(name = "vsm_id")
    private VSMObject vsm;
}
