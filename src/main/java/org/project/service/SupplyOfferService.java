package org.project.service;

import org.project.data.ConnectionFactory;
import org.project.data.DatabaseConnection;
import org.project.exceptions.*;
import org.project.model.*;
import org.project.repository.*;

import java.util.*;

public class SupplyOfferService {

    private DatabaseConnection connection;
    private SupplyOfferRepository supplyOfferRepository;
    private AddressRepository addressRepository;
    private SupplierRepository supplierRepository;
    private RawMaterialRepository rawMaterialRepository;

    public SupplyOfferService() {
        connection = ConnectionFactory.getInstance().getDatabaseConnection();
        Repositories repositories = Repositories.getInstance();
        supplyOfferRepository = repositories.getSupplyOfferRepository();
        addressRepository = repositories.getAddressRepository();
        supplierRepository = repositories.getSupplierRepository();
        rawMaterialRepository = repositories.getRawMaterialRepository();
    }

    public SupplyOffer getSupplyOfferByID (int id) throws SupplyOfferException {
        if (!supplyOfferRepository.getSupplyOfferExists(connection, id)) {
            throw new SupplyOfferException("Supply Offer with ID " + id + " not exists.");
        }

        return supplyOfferRepository.getByID(connection, id);
    }

    public SupplyOffer registerSupplyOffer(int supplierID, int supplyOfferID, String deliveryStreet, String deliveryZipCode, String deliveryTown, String deliveryCountry, Date startDate, Date endDate, ProcessState state, Map<String, Map<Integer, Double>> rawMaterialIDsQuantityCost) throws SupplierException, SupplyOfferException, ProductException {
        if (endDate.before(startDate)) {
            throw new SupplyOfferException("End date cannot be before Start date.");
        }

        if (supplyOfferRepository.getSupplyOfferExists(connection, supplyOfferID)) {
            throw new SupplyOfferException("Supply Offer with ID " + supplyOfferID + " already exists.");
        }

        if (!supplierRepository.getSupplierExists(connection, supplierID)) {
            throw new SupplierException("Supplier with ID " + supplierID + " not exists.");
        }
        Supplier supplier = supplierRepository.getById(connection, supplierID);
        if (supplier == null) {
            return null;
        }

        Address address = addressRepository.findAddress(connection, deliveryStreet, deliveryZipCode, deliveryTown, deliveryCountry);
        if (address == null) {
            int id = addressRepository.getAddressCount(connection);
            address = new Address(id, deliveryStreet, deliveryZipCode, deliveryTown, deliveryCountry);
            addressRepository.save(connection, address);
        }

        Map<RawMaterial, Map<Integer, Double>> rawMaterialsQuantityCost = new HashMap<>();
        for (Map.Entry<String, Map<Integer, Double>> rawMaterialIdQuantityCost : rawMaterialIDsQuantityCost.entrySet()) {
            String rawMaterialID = rawMaterialIdQuantityCost.getKey();

            if (!rawMaterialRepository.getRawMaterialExists(connection, rawMaterialID)) {
                throw new ProductException("Raw Material with ID " + rawMaterialID + " not exists.");
            }
            RawMaterial rawMaterial = rawMaterialRepository.getRawMaterialByID(connection, rawMaterialID);
            if (rawMaterial == null) {
                return null;
            }

            for (Map.Entry<Integer, Double> entry : rawMaterialIdQuantityCost.getValue().entrySet()) {
                rawMaterialsQuantityCost
                        .computeIfAbsent(rawMaterial, k -> new HashMap<>())
                        .put(entry.getKey(), entry.getValue());
            }

        }

        SupplyOffer supplyOffer = new SupplyOffer(supplyOfferID, address, startDate, endDate, state, rawMaterialsQuantityCost);

        boolean success = supplyOfferRepository.save(connection, supplyOffer, supplier);
        if (!success) {
            return null;
        }

        supplier.getSupplyOffers().add(supplyOffer);
        return supplyOffer;
    }

    public SupplyOffer deleteSupplyOffer (int id) throws SupplyOfferException {
        SupplyOffer supplyOffer = getSupplyOfferByID(id);

        boolean success = supplyOfferRepository.delete(connection, supplyOffer);
        if (!success) {
            return null;
        }

        return supplyOffer;
    }

    public SupplyOffer updateSupplyOffer (SupplyOffer supplyOffer, String deliveryStreet, String deliveryZipCode, String deliveryTown, String deliveryCountry, Date startDate, Date endDate) throws SupplyOfferException {
        if (endDate.before(startDate)) {
            throw new SupplyOfferException("End date cannot be before Start date.");
        }

        Address address = supplyOffer.getDeliveryAddress();
        if (!address.getStreet().equals(deliveryStreet) || !address.getZipCode().equals(deliveryZipCode) || !address.getTown().equals(deliveryTown) || !address.getCountry().equals(deliveryCountry)) {
            address = addressRepository.findAddress(connection, deliveryStreet, deliveryZipCode, deliveryTown, deliveryCountry);
            if (address == null) {
                int id = addressRepository.getAddressCount(connection);
                address = new Address(id, deliveryStreet, deliveryZipCode, deliveryTown, deliveryCountry);
                addressRepository.save(connection, address);
            }
        }

        supplyOffer.setDeliveryAddress(address);
        supplyOffer.setStartDate(startDate);
        supplyOffer.setEndDate(endDate);

        boolean success = supplyOfferRepository.update(connection, supplyOffer);
        if (!success) {
            return null;
        }

        return supplyOffer;
    }

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

    public SupplyOffer completeSupplyOffer(int supplyOfferID) throws SupplyOfferException, ProductException {

        SupplyOffer supplyOffer = getSupplyOfferByID(supplyOfferID);

        for (Map.Entry<RawMaterial, Map<Integer, Double>> getRawMaterialQuantityCost : supplyOffer.getRawMaterialsQuantityCost().entrySet()) {
            RawMaterial rawMaterial = getRawMaterialQuantityCost.getKey();

            for (Map.Entry<Integer, Double> entry : getRawMaterialQuantityCost.getValue().entrySet()) {
                Integer quantity = entry.getKey();
                int newCurrentStock = rawMaterial.getCurrentStock() - quantity;

                if (newCurrentStock < 0) {
                    throw new ProductException("There is not enough stock of the raw material id: " + rawMaterial.getId());
                }

                rawMaterial.setCurrentStock(newCurrentStock);

                boolean success = rawMaterialRepository.updateRawMaterial(connection, rawMaterial);
                if (!success) {
                    throw new ProductException("Unable to update stock.");
                }
            }
        }

        supplyOffer.setState(ProcessState.CONFIRMED);

        boolean success = supplyOfferRepository.updateState(connection, supplyOffer);
        if (!success) {
            return null;
        }

        return supplyOffer;
    }
}
