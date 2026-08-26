/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.dao;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import tu.sofia.value_stream_mapper_server.models.TableRow;

public interface TableRowDAO extends JpaRepository<TableRow, UUID> {}
