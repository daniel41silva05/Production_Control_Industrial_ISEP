package org.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.project.data.DatabaseConnection;
import org.project.model.SupplyOffer;
import org.project.repository.SupplyOfferRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ActiveSupplyOffersTest {

    private SupplyOfferService supplyOfferService;

    @Mock
    private DatabaseConnection connection;

    @Mock
    private SupplyOfferRepository supplyOfferRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        supplyOfferService = new SupplyOfferService(connection, supplyOfferRepository, null, null);
    }

    @Test
    public void testActiveSupplyOffers_ReturnsOnlyFutureOrders() {
        Date futureDate = new Date(System.currentTimeMillis() + 86400000);
        Date pastDate = new Date(System.currentTimeMillis() - 86400000);

        SupplyOffer offer1 = mock(SupplyOffer.class);
        SupplyOffer offer2 = mock(SupplyOffer.class);

        when(offer1.getEndDate()).thenReturn(futureDate);
        when(offer2.getEndDate()).thenReturn(pastDate);

        when(supplyOfferRepository.getAll(connection)).thenReturn(Arrays.asList(offer1, offer2));

        List<SupplyOffer> activeSupplyOffers = supplyOfferService.activeSupplyOffers();

        assertEquals(1, activeSupplyOffers.size());
        assertTrue(activeSupplyOffers.contains(offer1));
        assertFalse(activeSupplyOffers.contains(offer2));
    }

    @Test
    public void testActiveSupplyOffers_ReturnsEmptyListWhenNoOrders() {
        when(supplyOfferRepository.getAll(connection)).thenReturn(Collections.emptyList());

        List<SupplyOffer> activeSupplyOffers = supplyOfferService.activeSupplyOffers();

        assertTrue(activeSupplyOffers.isEmpty());
    }

    @Test
    public void testActiveSupplyOffers_ReturnsEmptyListWhenAllOrdersArePast() {
        Date pastDate1 = new Date(System.currentTimeMillis() - 86400000);
        Date pastDate2 = new Date(System.currentTimeMillis() - 172800000);

        SupplyOffer offer1 = mock(SupplyOffer.class);
        SupplyOffer offer2 = mock(SupplyOffer.class);

        when(offer1.getEndDate()).thenReturn(pastDate1);
        when(offer2.getEndDate()).thenReturn(pastDate2);

        when(supplyOfferRepository.getAll(connection)).thenReturn(Arrays.asList(offer1, offer2));

        List<SupplyOffer> activeSupplyOffers = supplyOfferService.activeSupplyOffers();

        assertTrue(activeSupplyOffers.isEmpty());
    }

}
