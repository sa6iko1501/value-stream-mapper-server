/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

    @JsonIgnore
    @OneToMany(mappedBy = "vsm", cascade = CascadeType.REMOVE)
    private Set<ProcessTable> processTables;

    public VSMObject() {}
}
