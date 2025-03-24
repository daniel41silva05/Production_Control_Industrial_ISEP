package org.project.ui;

import org.project.controller.*;
import org.project.exceptions.*;
import org.project.model.*;
import org.project.ui.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterSupplyOfferUI implements Runnable {

    private final SupplyOfferController supplyOfferController;
    private final SupplierController supplierController;
    private final RawMaterialController rawMaterialController;

    public RegisterSupplyOfferUI() {
        this.supplyOfferController = new SupplyOfferController();
        this.supplierController = new SupplierController();
        this.rawMaterialController = new RawMaterialController();
    }

    public void run() {
        try {
            showSuppliers(supplierController.getSuppliers());

            int supplierID = Utils.readIntegerFromConsole("Enter Supplier ID: ");
            Supplier supplier = supplierController.getSupplierByID(supplierID);

            showSupplyOffersSupplier(supplier);
            boolean register = Utils.confirm("Do you want to register a new supply offer?");
            if (!register) {
                return;
            }

            int supplyOfferID = Utils.readIntegerFromConsole("Enter Supply Offer ID: ");
            Date startDate = Utils.readDateFromConsole("Enter Start Date: ");
            Date endDate = Utils.readDateFromConsole("Enter End Date: ");
            String street = Utils.readLineFromConsole("Enter Delivery Street: ");
            String zipCode = Utils.readZipCodeFromConsole("Enter Delivery Zip Code: ");
            String town = Utils.readLineFromConsole("Enter Delivery Town: ");
            String country = Utils.readLineFromConsole("Enter Delivery Country: ");

            showRawMaterials(rawMaterialController.getRawMaterials());

            Map<String, Map<Integer, Double>> rawMaterialsQuantityCost = new HashMap<>();
            boolean addMoreMaterials = true;

            while (addMoreMaterials) {
                String rawMaterialID = Utils.readLineFromConsole("Enter Raw Material ID: ");
                int quantity = Utils.readIntegerFromConsole("Enter Quantity: ");
                double cost = Utils.readDoubleFromConsole("Enter Cost: ");

                rawMaterialsQuantityCost
                        .computeIfAbsent(rawMaterialID, k -> new HashMap<>())
                        .put(quantity, cost);

                addMoreMaterials = Utils.confirm("Do you want to add another raw material?");
            }

            SupplyOffer supplyOffer = supplyOfferController.registerSupplyOffer(supplier, supplyOfferID, street, zipCode, town, country, startDate, endDate, rawMaterialsQuantityCost);
            if (supplyOffer == null) {
                System.out.println("\nSupply Offer registration failed.");
            } else {
                System.out.println("\nSupply Offer registered successfully.");
                showSupplyOffer(supplyOffer);
            }

        } catch (SupplierException | SupplyOfferException | ProductException | DatabaseException e) {
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

    private void showRawMaterials(List<RawMaterial> rawMaterials) {
        System.out.println("\nRaw Materials:");
        if (rawMaterials.isEmpty()) {
            System.out.println("No raw materials registered.");
        } else {
            for (RawMaterial rawMaterial : rawMaterials) {
                System.out.println(" - Raw Material ID: " + rawMaterial.getId() + " | Name: " + rawMaterial.getName());
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
