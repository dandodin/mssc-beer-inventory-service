package guru.sfg.beer.inventory.service.services;

import guru.sfg.beer.inventory.service.config.JmsConfig;
import guru.sfg.brewery.model.events.AllocateOrderRequest;
import guru.sfg.brewery.model.events.AllocateOrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AllocationListener {

    private final AllocationService allocationService;
    private final JmsTemplate jmsTemplate;

    @Transactional
    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
    public void listen(AllocateOrderRequest allocateOrderRequest) {
        log.info(String.format("Received allocate request for id[%s]",
            allocateOrderRequest.getBeerOrder().getId()));

        AllocateOrderResponse allocateOrderResponse = new AllocateOrderResponse();
        allocateOrderResponse.setBeerOrder(allocateOrderRequest.getBeerOrder());
        try {
            Boolean allocated = allocationService.allocateOrder(allocateOrderRequest.getBeerOrder());
            if (allocated) {
                allocateOrderResponse.setPendingInventory(false);
            } else {
                allocateOrderResponse.setPendingInventory(true);
            }
        } catch (Exception e) {
            log.error("problem allocating beer order", e);
            allocateOrderResponse.setAllocationError(true);
        }
        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESULT_QUEUE, allocateOrderResponse);
    }
}
