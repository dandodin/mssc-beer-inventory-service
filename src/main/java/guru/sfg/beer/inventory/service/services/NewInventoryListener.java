package guru.sfg.beer.inventory.service.services;

import guru.sfg.beer.inventory.service.config.JmsConfig;
import guru.sfg.beer.inventory.service.domain.BeerInventory;
import guru.sfg.beer.inventory.service.repositories.BeerInventoryRepository;
import guru.sfg.brewery.model.events.BeerDto;
import guru.sfg.brewery.model.events.NewInventoryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
public class NewInventoryListener {

    private final BeerInventoryRepository beerInventoryRepository;

    @Transactional
    @JmsListener(destination = JmsConfig.NEW_INVENTORY_QUEUE)
    public void listen(NewInventoryEvent event) {
        BeerDto beerDto = event.getBeerDto();
        log.debug("Got inventory event " + beerDto.getBeerName());
        beerInventoryRepository.save(BeerInventory.builder()
            .beerId(beerDto.getId())
            .upc(beerDto.getUpc())
            .quantityOnHand(beerDto.getQuantityOnHand())
            .build());
    }
}
