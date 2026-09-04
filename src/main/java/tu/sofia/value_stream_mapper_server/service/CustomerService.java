/* 2026 Aleksandar Stoyanov */
package tu.sofia.value_stream_mapper_server.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tu.sofia.value_stream_mapper_server.dao.VSMObjectDAO;
import tu.sofia.value_stream_mapper_server.dto.response.vsm.CustomerOutputDTO;
import tu.sofia.value_stream_mapper_server.models.Customer;
import tu.sofia.value_stream_mapper_server.models.CustomerOutput;
import tu.sofia.value_stream_mapper_server.models.VSMObject;

@Service
@NoArgsConstructor
public class CustomerService {
    @Autowired private VSMObjectDAO vsmRepo;

    public CustomerOutputDTO createCustomer(UUID vsmId, CustomerOutputDTO dto) {
        final var vsm = getVsm(vsmId);

        final var customerOutput = vsm.getCustomerOutput();

        dto.getCustomers().stream()
                .forEach(
                        x -> {
                            if (x.getId() == null) {
                                var customer = new Customer();
                                customer.setName(x.getName());
                                customer.setUnitName(vsm.getUnit().getName());
                                customer.setDailyRequiredOutput(x.getDailyRequiredOutput());
                                customer.setId(UUID.randomUUID());
                                customer.setCustomerOutput(customerOutput);
                                customerOutput.getCustomers().add(customer);
                            }
                        });

        customerOutput.setAmount(calculateTotalRequirement(customerOutput));
        customerOutput.setTakt(calculateTakt(customerOutput, vsm));
        vsmRepo.save(vsm);

        return dto;
    }

    public CustomerOutputDTO updateCustomerOutput(UUID vsmId, CustomerOutputDTO dto) {
        final var vsm = getVsm(vsmId);

        final var customerOutput = vsm.getCustomerOutput();

        dto.getCustomers().stream()
                .forEach(
                        x -> {
                            var matchingCustomer =
                                    customerOutput.getCustomers().stream()
                                            .filter(customer -> customer.getId().equals(x.getId()))
                                            .findAny()
                                            .orElseThrow(
                                                    () ->
                                                            new IllegalStateException(
                                                                    "Mismatch between customer"
                                                                            + " request and actual"
                                                                            + " customer output"
                                                                            + " state"));

                            matchingCustomer.setDailyRequiredOutput(x.getDailyRequiredOutput());
                            matchingCustomer.setUnitName(vsm.getUnit().getName());
                            matchingCustomer.setName(x.getName());
                        });

        customerOutput.setAmount(calculateTotalRequirement(customerOutput));
        customerOutput.setTakt(calculateTakt(customerOutput, vsm));
        customerOutput.setUnit(vsm.getUnit());
        vsmRepo.save(vsm);
        return dto;
    }

    public boolean deleteCustomer(UUID vsmId, UUID customerId) {
        final var vsm = getVsm(vsmId);

        var customers = vsm.getCustomerOutput().getCustomers();

        var toBeDeleted =
                customers.stream()
                        .filter(x -> x.getId().equals(customerId))
                        .findAny()
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "Mismatch between customer"
                                                        + " request and actual"
                                                        + " customer output"
                                                        + " state"));

        boolean deleted = customers.remove(toBeDeleted);

        vsm.getCustomerOutput().setAmount(calculateTotalRequirement(vsm.getCustomerOutput()));
        vsm.getCustomerOutput().setTakt(calculateTakt(vsm.getCustomerOutput(), vsm));

        vsmRepo.save(vsm);
        return deleted;
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

    public double calculateTotalRequirement(CustomerOutput customerOutput) {
        double totalRequirement = 0;
        for (Customer customer : customerOutput.getCustomers()) {
            totalRequirement += customer.getDailyRequiredOutput();
        }

        return totalRequirement;
    }

    public double calculateTakt(CustomerOutput customerOutput, VSMObject vsm) {
        var shifts = vsm.getShifts();
        var breakTime = vsm.getBreaktime();
        var lengthOfShifts = vsm.getLengthOfShifts();

        BigDecimal availableTimeInSeconds =
                BigDecimal.valueOf(shifts)
                        .multiply(BigDecimal.valueOf(lengthOfShifts))
                        .multiply(BigDecimal.valueOf(60))
                        .multiply(BigDecimal.valueOf(60));
        BigDecimal totalBreakTimeInSeconds =
                breakTime != 0
                        ? BigDecimal.valueOf(shifts)
                                .multiply(BigDecimal.valueOf(breakTime))
                                .multiply(BigDecimal.valueOf(60))
                        : BigDecimal.ZERO;
        availableTimeInSeconds = availableTimeInSeconds.subtract(totalBreakTimeInSeconds);
        BigDecimal dailyReq = BigDecimal.valueOf(calculateTotalRequirement(customerOutput));
        BigDecimal perSecondReq = dailyReq.divide(availableTimeInSeconds, 6, RoundingMode.UP);
        BigDecimal secPerUnit = BigDecimal.ONE.divide(perSecondReq, 2, RoundingMode.UP);
        return secPerUnit.doubleValue();
    }
}
