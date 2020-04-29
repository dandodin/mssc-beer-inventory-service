package guru.sfg.beer.inventory.service.services;

import guru.sfg.beer.inventory.service.config.JmsConfig;
import guru.sfg.brewery.model.events.DeallocateOrderRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeallocationListener {

    private final AllocationService allocationService;

    @Transactional
    @JmsListener(destination = JmsConfig.DEALLOCATE_ORDER_QUEUE)
    public void listen(DeallocateOrderRequest deallocateOrderRequest) {
        log.info(String.format("Received deallocate request for id[%s]",
            deallocateOrderRequest.getBeerOrderDto().getId()));
        allocationService.deallocateOrder(deallocateOrderRequest.getBeerOrderDto());
    }
}
