package org.project.service;

import org.project.common.Validator;
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
    private RawMaterialRepository rawMaterialRepository;

    public SupplyOfferService() {
        connection = ConnectionFactory.getInstance().getDatabaseConnection();
        Repositories repositories = Repositories.getInstance();
        supplyOfferRepository = repositories.getSupplyOfferRepository();
        addressRepository = repositories.getAddressRepository();
        rawMaterialRepository = repositories.getRawMaterialRepository();
    }

    public SupplyOfferService (DatabaseConnection connection, SupplyOfferRepository supplyOfferRepository, AddressRepository addressRepository, RawMaterialRepository rawMaterialRepository) {
        this.connection = connection;
        this.supplyOfferRepository = supplyOfferRepository;
        this.addressRepository = addressRepository;
        this.rawMaterialRepository = rawMaterialRepository;
    }

    public SupplyOffer getSupplyOfferByID (int id)  {
        SupplyOffer supplyOffer = supplyOfferRepository.getByID(connection, id);

        if (supplyOffer == null) {
            throw SupplyOfferException.supplyOfferNotFound(id);
        }

        return supplyOffer;
    }

    public SupplyOffer registerSupplyOffer(Supplier supplier, int supplyOfferID, String deliveryStreet, String deliveryZipCode, String deliveryTown, String deliveryCountry, Date startDate, Date endDate, Map<RawMaterial, Map<Integer, Double>> rawMaterialsQuantityCost) {
        if (endDate.before(startDate)) {
            throw SupplyOfferException.invalidDateRange();
        }

        if (supplier == null) {
            return null;
        }

        if (supplyOfferRepository.getSupplyOfferExists(connection, supplyOfferID)) {
            throw SupplyOfferException.supplyOfferAlreadyExists(supplyOfferID);
        }

        if (!Validator.isValidZipCode(deliveryZipCode)) {
            throw SupplyOfferException.invalidZipCode();
        }

        Address address = addressRepository.findAddress(connection, deliveryStreet, deliveryZipCode, deliveryTown, deliveryCountry);
        if (address == null) {
            int id = addressRepository.getAddressCount(connection);
            address = new Address(id, deliveryStreet, deliveryZipCode, deliveryTown, deliveryCountry);
            addressRepository.save(connection, address);
        }

        SupplyOffer supplyOffer = new SupplyOffer(supplyOfferID, address, startDate, endDate, rawMaterialsQuantityCost);

        supplyOfferRepository.save(connection, supplyOffer, supplier);

        supplier.getSupplyOffers().add(supplyOffer);
        return supplyOffer;
    }

    public SupplyOffer deleteSupplyOffer (int id) {
        SupplyOffer supplyOffer = getSupplyOfferByID(id);

        supplyOfferRepository.delete(connection, supplyOffer);

        return supplyOffer;
    }

    public SupplyOffer updateSupplyOffer (SupplyOffer supplyOffer, String deliveryStreet, String deliveryZipCode, String deliveryTown, String deliveryCountry, Date startDate, Date endDate) {
        if (endDate.before(startDate)) {
            throw SupplyOfferException.invalidDateRange();
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

        supplyOfferRepository.update(connection, supplyOffer);

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

    public SupplyOffer completeSupplyOffer(int supplyOfferID) {
        SupplyOffer supplyOffer = getSupplyOfferByID(supplyOfferID);

        for (Map.Entry<RawMaterial, Map<Integer, Double>> getRawMaterialQuantityCost : supplyOffer.getRawMaterialsQuantityCost().entrySet()) {
            RawMaterial rawMaterial = getRawMaterialQuantityCost.getKey();

            for (Map.Entry<Integer, Double> entry : getRawMaterialQuantityCost.getValue().entrySet()) {
                Integer quantity = entry.getKey();
                int newCurrentStock = rawMaterial.getCurrentStock() + quantity;

                rawMaterial.setCurrentStock(newCurrentStock);

                rawMaterialRepository.updateRawMaterial(connection, rawMaterial);
            }
        }

        supplyOffer.setState(ProcessState.CONFIRMED);

        supplyOfferRepository.updateState(connection, supplyOffer);

        return supplyOffer;
    }

}
