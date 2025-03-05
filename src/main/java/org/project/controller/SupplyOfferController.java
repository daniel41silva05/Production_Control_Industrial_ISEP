package org.project.controller;

import org.project.exceptions.DatabaseException;
import org.project.exceptions.ProductException;
import org.project.exceptions.SupplierException;
import org.project.exceptions.SupplyOfferException;
import org.project.model.ProcessState;
import org.project.model.SupplyOffer;
import org.project.service.SupplyOfferService;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class SupplyOfferController {

    private SupplyOfferService supplyOfferService;

    public SupplyOfferController() {
        supplyOfferService = new SupplyOfferService();
    }

    public SupplyOffer getSupplyOfferByID (int id) throws SupplyOfferException {
        return supplyOfferService.getSupplyOfferByID (id);
    }

    public SupplyOffer registerSupplyOffer(int supplierID, int supplyOfferID, String deliveryStreet, String deliveryZipCode, String deliveryTown, String deliveryCountry, Date startDate, Date endDate, ProcessState state, Map<String, Map<Integer, Double>> rawMaterialIDsQuantityCost) throws SupplierException, SupplyOfferException, ProductException, DatabaseException {
        return supplyOfferService.registerSupplyOffer(supplierID, supplyOfferID, deliveryStreet, deliveryZipCode, deliveryTown, deliveryCountry, startDate, endDate, state, rawMaterialIDsQuantityCost);
    }

    public SupplyOffer deleteSupplyOffer (int id) throws SupplyOfferException {
        return supplyOfferService.deleteSupplyOffer(id);
    }

    public SupplyOffer updateSupplyOffer (SupplyOffer supplyOffer, String deliveryStreet, String deliveryZipCode, String deliveryTown, String deliveryCountry, Date startDate, Date endDate) throws SupplyOfferException, DatabaseException {
        return supplyOfferService.updateSupplyOffer(supplyOffer, deliveryStreet, deliveryZipCode, deliveryTown, deliveryCountry, startDate, endDate);
    }

    public List<SupplyOffer> activeSupplyOffers () {
        return supplyOfferService.activeSupplyOffers();
    }

    public SupplyOffer completeSupplyOffer(int supplyOfferID) throws SupplyOfferException, ProductException {
        return supplyOfferService.completeSupplyOffer(supplyOfferID);
    }
}
