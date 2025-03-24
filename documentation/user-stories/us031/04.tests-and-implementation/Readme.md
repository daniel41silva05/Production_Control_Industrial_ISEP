# US031 - Consult Active Supply Offers

## 4. Tests 

**Test 1:** Check if only include supply offers with a end date after the current one - AC02.

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

**Test 2:** Check if there are no supply offers returns empty.

    @Test
    public void testActiveSupplyOffers_ReturnsEmptyListWhenNoOrders() {
        when(supplyOfferRepository.getAll(connection)).thenReturn(Collections.emptyList());

        List<SupplyOffer> activeSupplyOffers = supplyOfferService.activeSupplyOffers();

        assertTrue(activeSupplyOffers.isEmpty());
    }

**Test 3:** Check if there are only inactive supply offers returns empty.

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

## 5. Construction (Implementation)

### Class SupplyOfferService 

```java
public List<SupplyOffer> activeSupplyOffers () {
    List<SupplyOffer> activeSupplyOffers = new ArrayList<>();

    List<SupplyOffer> supplyOffers = supplyOfferRepository.getAll(connection);

    for (SupplyOffer supplyOffer : supplyOffers) {
        if (supplyOffer.getEndDate().after(new Date())) {
            activeSupplyOffers.add(supplyOffer);
        }
    }

    return activeSupplyOffers;
}
```

## 6. Observations

n/a