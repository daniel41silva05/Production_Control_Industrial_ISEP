package org.project.ui;

import org.project.controller.SupplierController;
import org.project.controller.SupplyOfferController;
import org.project.exceptions.*;
import org.project.model.*;
import org.project.ui.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class UpdateSupplyOfferUI implements Runnable {

    private final SupplyOfferController supplyOfferController;
    private final SupplierController supplierController;

    public UpdateSupplyOfferUI() {
        this.supplyOfferController = new SupplyOfferController();
        this.supplierController = new SupplierController();
    }

    public void run() {
        try {
            showSuppliers(supplierController.getSuppliers());

            int supplierID = Utils.readIntegerFromConsole("Enter Supplier ID: ");
            Supplier supplier = supplierController.getSupplierByID(supplierID);

            showSupplyOffersSupplier(supplier);

            boolean update = Utils.confirm("Do you want to update a supply offer?");
            if (!update) {
                return;
            }

            int supplyOfferID = Utils.readIntegerFromConsole("Enter Supply Offer ID: ");
            SupplyOffer supplyOffer = supplyOfferController.getSupplyOfferByID(supplyOfferID);

            String street = supplyOffer.getDeliveryAddress().getStreet();
            String zipCode = supplyOffer.getDeliveryAddress().getZipCode();
            String town = supplyOffer.getDeliveryAddress().getTown();
            String country = supplyOffer.getDeliveryAddress().getCountry();
            Date startDate = supplyOffer.getStartDate();
            Date endDate = supplyOffer.getEndDate();

            if (Utils.confirm("Do you want to update the Supply Offer Date?")) {
                startDate = Utils.readDateFromConsole("Enter new Supply Offer  Date: ");
            }

            if (Utils.confirm("Do you want to update the Start Date?")) {
                endDate = Utils.readDateFromConsole("Enter new Start Date: ");
            }

            if (Utils.confirm("Do you want to update the Address?")) {

                if (Utils.confirm("Do you want to update the Street?")) {
                    street = Utils.readLineFromConsole("Enter new Street: ");
                }
                if (Utils.confirm("Do you want to update the Zip Code?")) {
                    zipCode = Utils.readLineFromConsole("Enter new Zip Code: ");
                }
                if (Utils.confirm("Do you want to update the Town?")) {
                    town = Utils.readLineFromConsole("Enter new Town: ");
                }
                if (Utils.confirm("Do you want to update the Country?")) {
                    country = Utils.readLineFromConsole("Enter new Country: ");
                }
            }

            SupplyOffer newSupplyOffer = supplyOfferController.updateSupplyOffer(supplyOffer, street, zipCode, town, country, startDate, endDate);
            if (newSupplyOffer != null) {
                System.out.println("\nSupply Offer updated successfully.");
                showSupplyOffer(newSupplyOffer);
            } else {
                System.out.println("\nSupply Offer update failed.");
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

    private void showSupplyOffer(SupplyOffer supplyOffer) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println(" - Supply Offer ID: " + supplyOffer.getId());
        System.out.println(" - Start Date: " + dateFormat.format(supplyOffer.getStartDate()));
        System.out.println(" - End Date: " + dateFormat.format(supplyOffer.getEndDate()));
        System.out.println(" - Delivery Address ID: " + supplyOffer.getDeliveryAddress().getId());
        System.out.println(" - Delivery Street: " + supplyOffer.getDeliveryAddress().getStreet());
        System.out.println(" - Delivery Zip Code: " + supplyOffer.getDeliveryAddress().getZipCode());
        System.out.println(" - Delivery Town: " + supplyOffer.getDeliveryAddress().getTown());
        System.out.println(" - Delivery Country: " + supplyOffer.getDeliveryAddress().getCountry());
        System.out.println(" - Raw Materials: ");
        for (Map.Entry<RawMaterial, Map<Integer, Double>> entry : supplyOffer.getRawMaterialsQuantityCost().entrySet()) {
            for (Map.Entry<Integer, Double> quantityCost : entry.getValue().entrySet()) {
                System.out.println(" --- Raw Material ID: " + entry.getKey().getId()
                        + " | Name: " + entry.getKey().getName()
                        + " | Quantity: " + quantityCost.getKey()
                        + " | Cost: " + quantityCost.getValue() + "$");
            }
        }
    }

}
