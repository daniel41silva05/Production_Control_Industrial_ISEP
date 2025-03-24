package org.project.ui;

import org.project.controller.SupplierController;
import org.project.controller.SupplyOfferController;
import org.project.exceptions.*;
import org.project.model.*;
import org.project.ui.utils.Utils;

import java.util.List;
import java.util.Map;

public class CancelSupplyOfferUI implements Runnable {

    private final SupplyOfferController supplyOfferController;
    private final SupplierController supplierController;

    public CancelSupplyOfferUI() {
        this.supplyOfferController = new SupplyOfferController();
        this.supplierController = new SupplierController();
    }

    public void run() {
        try {
            showSuppliers(supplierController.getSuppliers());

            int supplierID = Utils.readIntegerFromConsole("Enter Supplier ID: ");
            Supplier supplier = supplierController.getSupplierByID(supplierID);

            showSupplyOffersSupplier(supplier);
            boolean delete = Utils.confirm("Do you want to cancel a supply offer?");
            if (!delete) {
                return;
            }

            int supplyOfferID = Utils.readIntegerFromConsole("Enter Supply Offer ID: ");

            SupplyOffer supplyOffer = supplyOfferController.deleteSupplyOffer(supplyOfferID);
            if (supplyOffer == null) {
                System.out.println("\nFailed to cancel supply offer.");
            } else {
                System.out.println("\nSupply Offer canceled successfully.");
            }

        } catch (SupplierException | SupplyOfferException | DatabaseException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private void showSuppliers(List<Supplier> suppliers) {
        System.out.println("\nSuppliers:");
        if (suppliers.isEmpty()) {
            System.out.println("No suppliers registered.");
        } else {
            for (Supplier supplier : suppliers) {
                System.out.println(" - Supplier ID: " + supplier.getId() + " | Name: " + supplier.getName());
            }
        }
    }

    private void showSupplyOffersSupplier (Supplier supplier) {
        System.out.println("\nSupplier ID: " + supplier.getId() + " | Name: " + supplier.getName());
        List<SupplyOffer> supplyOffers = supplier.getSupplyOffers();
        if (!supplyOffers.isEmpty()) {
            System.out.println("Supply Offers: ");
            for (SupplyOffer supplyOffer : supplyOffers) {
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
    }

}
