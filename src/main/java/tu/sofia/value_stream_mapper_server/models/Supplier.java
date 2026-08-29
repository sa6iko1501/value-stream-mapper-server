/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.models;

import jakarta.persistence.Column;
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
@Table(name = "supplier_table")
public class Supplier {
    @Id private UUID id;

    private String name;

    @Column(name = "daily_supply")
    private double dailySupply;

    @Column(name = "unit_name")
    private String unitName;

    @ManyToOne
    @JoinColumn(name = "supply_id", nullable = false)
    private Supply supply;
}
