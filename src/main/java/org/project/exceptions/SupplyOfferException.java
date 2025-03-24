package org.project.exceptions;

public class SupplyOfferException extends RuntimeException {

    public static final String SUPPLY_OFFER_ALREADY_EXISTS = "Supply Offer with ID %d already exists.";
    public static final String SUPPLY_OFFER_NOT_FOUND = "Supply Offer with ID %d does not exist.";
    public static final String INVALID_DATE_RANGE = "End date cannot be before Start date.";
    public static final String INVALID_ZIP_CODE = "Invalid zip code. It must follow the format xxxx-xxx.";

    public SupplyOfferException(String message) {
        super(message);
    }

    public static SupplyOfferException supplyOfferAlreadyExists(int id) {
        return new SupplyOfferException(String.format(SUPPLY_OFFER_ALREADY_EXISTS, id));
    }

    public static SupplyOfferException supplyOfferNotFound(int id) {
        return new SupplyOfferException(String.format(SUPPLY_OFFER_NOT_FOUND, id));
    }

    public static SupplyOfferException invalidDateRange() {
        return new SupplyOfferException(INVALID_DATE_RANGE);
    }

    public static SupplyOfferException invalidZipCode() {
        return new SupplyOfferException(INVALID_ZIP_CODE);
    }

}
