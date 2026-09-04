/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.service;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tu.sofia.value_stream_mapper_server.dao.VSMObjectDAO;
import tu.sofia.value_stream_mapper_server.dto.request.SupplyInventoryDTO;
import tu.sofia.value_stream_mapper_server.dto.response.vsm.SupplyDTO;
import tu.sofia.value_stream_mapper_server.models.Supplier;
import tu.sofia.value_stream_mapper_server.models.Supply;
import tu.sofia.value_stream_mapper_server.models.VSMObject;

@Service
public class SupplyService {
    @Autowired private VSMObjectDAO vsmRepo;

    public SupplyInventoryDTO updateSupplyInventory(UUID vsmId, SupplyInventoryDTO dto) {
        var vsm = getVsm(vsmId);

        vsm.getSupply().setInventoryAmount(dto.getSupplyInventory());
        vsmRepo.save(vsm);
        return dto;
    }

    public SupplyDTO createSupplier(UUID vsmId, SupplyDTO dto) {
        final var vsm = getVsm(vsmId);

        final var supply = vsm.getSupply();
        dto.getSuppliers().stream()
                .forEach(
                        x -> {
                            if (x.getId() == null) {
                                var supplier = new Supplier();
                                supplier.setSupply(supply);
                                supplier.setName(x.getName());
                                supplier.setUnitName(supply.getUnit().getName());
                                supplier.setId(UUID.randomUUID());
                                supplier.setDailySupply(x.getSupply());
                                supply.getSuppliers().add(supplier);
                            }
                        });

        supply.setAmount(calculateTotalSupply(supply));

        vsmRepo.save(vsm);
        return dto;
    }

    public SupplyDTO updateSupply(UUID vsmId, SupplyDTO dto) {
        final var vsm = getVsm(vsmId);

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
                                                                    "Mismatch between supplier"
                                                                            + " request and actual"
                                                                            + " supply state"));
                            matchingSupplier.setDailySupply(x.getSupply());
                            matchingSupplier.setName(x.getName());
                            matchingSupplier.setUnitName(supply.getUnit().getName());
                        });

        supply.setAmount(calculateTotalSupply(supply));
        vsmRepo.save(vsm);

        return dto;
    }

    public boolean deleteSupplier(UUID vsmId, UUID supplierId) {
        final var vsm = getVsm(vsmId);

        final var supply = vsm.getSupply();
        var toBeDeleted =
                supply.getSuppliers().stream()
                        .filter(x -> x.getId().equals(supplierId))
                        .findAny()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "Mismatch between supplier delete request"
                                                        + " request and actual"
                                                        + " supply state"));

        boolean removed = supply.getSuppliers().remove(toBeDeleted);
        supply.setAmount(calculateTotalSupply(supply));
        vsmRepo.save(vsm);
        return removed;
    }

    private VSMObject getVsm(UUID vsmId) {
        return vsmRepo.findById(vsmId)
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        String.format(
                                                "No value-stream map with the following id"
                                                        + " found: %s",
                                                vsmId)));
    }

    public double calculateTotalSupply(Supply supply) {
        double totalSupply = 0;
        for (Supplier supplier : supply.getSuppliers()) {
            totalSupply += supplier.getDailySupply();
        }

        return totalSupply;
    }
}
