package org.project.controller;

import org.project.model.RawMaterial;
import org.project.model.Supplier;
import org.project.model.SupplyOffer;
import org.project.service.RawMaterialService;
import org.project.service.SupplyOfferService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SupplyOfferController {

    private SupplyOfferService supplyOfferService;
    private RawMaterialService rawMaterialService;

    public SupplyOfferController() {
        supplyOfferService = new SupplyOfferService();
        rawMaterialService = new RawMaterialService();
    }

    public SupplyOfferController(SupplyOfferService supplyOfferService, RawMaterialService rawMaterialService) {
        this.supplyOfferService = supplyOfferService;
        this.rawMaterialService = rawMaterialService;
    }

    public SupplyOffer getSupplyOfferByID (int id) {
        return supplyOfferService.getSupplyOfferByID (id);
    }

    public SupplyOffer registerSupplyOffer(Supplier supplier, int supplyOfferID, String deliveryStreet, String deliveryZipCode, String deliveryTown, String deliveryCountry, Date startDate, Date endDate, Map<String, Map<Integer, Double>> rawMaterialIDsQuantityCost) {
        Map<RawMaterial, Map<Integer, Double>> rawMaterialsQuantityCost = new HashMap<>();
        for (Map.Entry<String, Map<Integer, Double>> rawMaterialIdQuantityCost : rawMaterialIDsQuantityCost.entrySet()) {
            RawMaterial rawMaterial = rawMaterialService.getRawMaterialByID(rawMaterialIdQuantityCost.getKey());
            for (Map.Entry<Integer, Double> entry : rawMaterialIdQuantityCost.getValue().entrySet()) {
                rawMaterialsQuantityCost
                        .computeIfAbsent(rawMaterial, k -> new HashMap<>())
                        .put(entry.getKey(), entry.getValue());
            }
        }

        return supplyOfferService.registerSupplyOffer(supplier, supplyOfferID, deliveryStreet, deliveryZipCode, deliveryTown, deliveryCountry, startDate, endDate, rawMaterialsQuantityCost);
    }

    public SupplyOffer deleteSupplyOffer (int id) {
        return supplyOfferService.deleteSupplyOffer(id);
    }

    public SupplyOffer updateSupplyOffer (SupplyOffer supplyOffer, String deliveryStreet, String deliveryZipCode, String deliveryTown, String deliveryCountry, Date startDate, Date endDate) {
        return supplyOfferService.updateSupplyOffer(supplyOffer, deliveryStreet, deliveryZipCode, deliveryTown, deliveryCountry, startDate, endDate);
    }

    public List<SupplyOffer> activeSupplyOffers () {
        return supplyOfferService.activeSupplyOffers();
    }

    public SupplyOffer completeSupplyOffer(int supplyOfferID) {
        return supplyOfferService.completeSupplyOffer(supplyOfferID);
    }

}
