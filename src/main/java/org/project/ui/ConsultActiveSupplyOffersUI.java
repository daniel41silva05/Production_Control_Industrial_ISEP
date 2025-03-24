package org.project.ui;

import org.project.controller.SupplyOfferController;
import org.project.exceptions.DatabaseException;
import org.project.exceptions.SupplierException;
import org.project.exceptions.SupplyOfferException;
import org.project.model.*;

import java.util.List;
import java.util.Map;

public class ConsultActiveSupplyOffersUI implements Runnable {

    private final SupplyOfferController controller;

    public ConsultActiveSupplyOffersUI() {
        this.controller = new SupplyOfferController();
    }

    public void run() {
        try {

            List<SupplyOffer> supplyOffers = controller.activeSupplyOffers();

            if (supplyOffers.isEmpty()) {
                System.out.println("\nNo active supply offers.");
            } else {
                System.out.println("\nActive Supply Offers:\n");
                for (SupplyOffer offer : supplyOffers) {
                    showSupplyOffersSupplier(offer);
                }
            }

        } catch (SupplierException | SupplyOfferException | DatabaseException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private void showSupplyOffersSupplier (SupplyOffer supplyOffer) {
        System.out.println(" - Supply Offer ID: " + supplyOffer.getId());
        for (Map.Entry<RawMaterial, Map<Integer, Double>> entry : supplyOffer.getRawMaterialsQuantityCost().entrySet()) {
            for (Map.Entry<Integer, Double> quantityCost : entry.getValue().entrySet()) {
                System.out.println(" -- Raw Material ID: " + entry.getKey().getId()
                + " | Raw Material Name: " + entry.getKey().getName()
                + " | Quantity: " + quantityCost.getKey()
                + " | Cost: " + quantityCost.getValue() + "$");
            }
        }
    }

}
