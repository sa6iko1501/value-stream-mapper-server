/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.service;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tu.sofia.value_stream_mapper_server.dao.VSMObjectDAO;
import tu.sofia.value_stream_mapper_server.dto.response.vsm.SupplyDTO;
import tu.sofia.value_stream_mapper_server.models.Supplier;
import tu.sofia.value_stream_mapper_server.models.Supply;

@Service
public class SupplyService {
    @Autowired private VSMObjectDAO vsmRepo;

    public SupplyDTO updateSupply(UUID vsmId, SupplyDTO dto) {
        final var vsm =
                vsmRepo.findById(vsmId)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                String.format(
                                                        "No value-stream map with the following id"
                                                                + " found: %s",
                                                        vsmId)));

        Supply supply = vsm.getSupply();

        dto.getSuppliers()
                .forEach(
                        x -> {
                            var matchingSupplier =
                                    supply.getSuppliers().stream()
                                            .filter(supplier -> supplier.getId().equals(x.getId()))
                                            .findAny()
                                            .orElseThrow(
                                                    () ->
                                                            new IllegalStateException(
                                                                    "Mismatch between positioning"
                                                                            + " request and actual"
                                                                            + " tables state"));
                            matchingSupplier.setDailySupply(x.getSupply());
                            matchingSupplier.setName(x.getName());
                            matchingSupplier.setUnitName(supply.getUnit().getName());
                        });

        double supplyTotal = 0;
        for (Supplier supplier : supply.getSuppliers()) {
            supplyTotal += supplier.getDailySupply();
        }
        supply.setAmount(supplyTotal);
        vsmRepo.save(vsm);

        return dto;
    }
}
