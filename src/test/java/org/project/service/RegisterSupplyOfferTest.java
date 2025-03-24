package org.project.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.project.controller.SupplyOfferController;
import org.project.data.DatabaseConnection;
import org.project.exceptions.ProductException;
import org.project.exceptions.SupplyOfferException;
import org.project.model.*;
import org.project.repository.AddressRepository;
import org.project.repository.RawMaterialRepository;
import org.project.repository.SupplyOfferRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RegisterSupplyOfferTest {

    private SupplyOfferController supplyOfferController;

    @Mock
    private SupplyOfferService supplyOfferService;

    @Mock
    private RawMaterialService rawMaterialService;

    @Mock
    private DatabaseConnection connection;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private SupplyOfferRepository supplyOfferRepository;

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        supplyOfferService = new SupplyOfferService(connection, supplyOfferRepository, addressRepository, rawMaterialRepository);
        rawMaterialService = new RawMaterialService(connection, rawMaterialRepository);

        supplyOfferController = new SupplyOfferController(supplyOfferService, rawMaterialService);
    }

    @Test
    public void testRegisterSupplyOffer_Success() throws ParseException {
        Supplier supplier = new Supplier(1, "Supplier Name", 123456789, "supplier@example.com");
        int supplyOfferID = 1;
        String deliveryStreet = "Main St";
        String deliveryZipCode = "1111-111";
        String deliveryTown = "Springfield";
        String deliveryCountry = "USA";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date startDate = sdf.parse("26/01/2024");
        Date endDate = sdf.parse("26/01/2026");

        Map<String, Map<Integer, Double>> rawMaterialIDsQuantityCost = new HashMap<>();
        Map<Integer, Double> quantityCost1 = new HashMap<>();
        quantityCost1.put(10, 5.0); // 10 units at 5.0 cost each
        rawMaterialIDsQuantityCost.put("mat1", quantityCost1);

        Map<Integer, Double> quantityCost2 = new HashMap<>();
        quantityCost2.put(20, 3.5); // 20 units at 3.5 cost each
        rawMaterialIDsQuantityCost.put("mat2", quantityCost2);

        RawMaterial material1 = new RawMaterial("mat1", "Material 1", "Description 1", 100, 10);
        when(rawMaterialRepository.getRawMaterialByID(connection, "mat1")).thenReturn(material1);
        RawMaterial material2 = new RawMaterial("mat2", "Material 2", "Description 2", 200, 20);
        when(rawMaterialRepository.getRawMaterialByID(connection, "mat2")).thenReturn(material2);

        when(supplyOfferRepository.getSupplyOfferExists(connection, supplyOfferID)).thenReturn(false);
        when(addressRepository.findAddress(connection, deliveryStreet, deliveryZipCode, deliveryTown, deliveryCountry)).thenReturn(null);
        when(addressRepository.getAddressCount(connection)).thenReturn(1);

        SupplyOffer supplyOffer = supplyOfferController.registerSupplyOffer(
                supplier, supplyOfferID, deliveryStreet, deliveryZipCode,
                deliveryTown, deliveryCountry, startDate, endDate, rawMaterialIDsQuantityCost);

        assertNotNull(supplyOffer);
        assertEquals(supplyOfferID, supplyOffer.getId());

        verify(supplyOfferRepository, times(1)).save(eq(connection), any(SupplyOffer.class), any(Supplier.class));
        verify(addressRepository, times(1)).save(eq(connection), any(Address.class));
    }

    @Test
    public void testRegisterSupplyOffer_InvalidDateRange() throws ParseException {
        Supplier supplier = new Supplier(1, "Supplier Name", 123456789, "supplier@example.com");
        int supplyOfferID = 1;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date startDate = sdf.parse("26/01/2024");
        Date endDate = sdf.parse("26/01/2022"); // End date before start date

        Map<String, Map<Integer, Double>> rawMaterialIDsQuantityCost = new HashMap<>();
        Map<Integer, Double> quantityCost = new HashMap<>();
        quantityCost.put(10, 5.0);
        rawMaterialIDsQuantityCost.put("mat1", quantityCost);

        RawMaterial material = new RawMaterial("mat1", "Material 1", "Description 1", 100, 10);
        when(rawMaterialRepository.getRawMaterialByID(connection, "mat1")).thenReturn(material);

        assertThrows(SupplyOfferException.class, () -> {
            supplyOfferController.registerSupplyOffer(
                    supplier, supplyOfferID, "Main St", "1111-111",
                    "Springfield", "USA", startDate, endDate, rawMaterialIDsQuantityCost);
        });
    }

    @Test
    public void testRegisterSupplyOffer_SupplierNotFound() throws ParseException {
        int supplyOfferID = 1;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date startDate = sdf.parse("26/01/2024");
        Date endDate = sdf.parse("26/01/2026");

        Map<String, Map<Integer, Double>> rawMaterialIDsQuantityCost = new HashMap<>();
        Map<Integer, Double> quantityCost = new HashMap<>();
        quantityCost.put(10, 5.0);
        rawMaterialIDsQuantityCost.put("mat1", quantityCost);

        RawMaterial material = new RawMaterial("mat1", "Material 1", "Description 1", 100, 10);
        when(rawMaterialRepository.getRawMaterialByID(connection, "mat1")).thenReturn(material);

        assertNull(supplyOfferService.registerSupplyOffer(
                null, supplyOfferID, "Main St", "1111-111",
                "Springfield", "USA", startDate, endDate, new HashMap<>()));

        verify(supplyOfferRepository, never()).save(eq(connection), any(SupplyOffer.class), any(Supplier.class));
    }

    @Test
    public void testRegisterSupplyOffer_SupplyOfferAlreadyExists() throws ParseException {
        int supplyOfferID = 1;
        when(supplyOfferRepository.getSupplyOfferExists(connection, supplyOfferID)).thenReturn(true);

        Supplier supplier = new Supplier(1, "Supplier Name", 123456789, "supplier@example.com");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date startDate = sdf.parse("26/01/2024");
        Date endDate = sdf.parse("26/01/2026");

        Map<String, Map<Integer, Double>> rawMaterialIDsQuantityCost = new HashMap<>();
        Map<Integer, Double> quantityCost = new HashMap<>();
        quantityCost.put(10, 5.0);
        rawMaterialIDsQuantityCost.put("mat1", quantityCost);

        RawMaterial material = new RawMaterial("mat1", "Material 1", "Description 1", 100, 10);
        when(rawMaterialRepository.getRawMaterialByID(connection, "mat1")).thenReturn(material);

        assertThrows(SupplyOfferException.class, () -> {
            supplyOfferService.registerSupplyOffer(
                    supplier, supplyOfferID, "Main St", "1111-111",
                    "Springfield", "USA", startDate, endDate, new HashMap<>());
        });
    }

    @Test
    public void testRegisterSupplyOffer_InvalidZipCode() throws ParseException {
        String invalidZipCode = "111";

        Supplier supplier = new Supplier(1, "Supplier Name", 123456789, "supplier@example.com");
        int supplyOfferID = 1;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date startDate = sdf.parse("26/01/2024");
        Date endDate = sdf.parse("26/01/2026");

        Map<String, Map<Integer, Double>> rawMaterialIDsQuantityCost = new HashMap<>();
        Map<Integer, Double> quantityCost = new HashMap<>();
        quantityCost.put(10, 5.0);
        rawMaterialIDsQuantityCost.put("mat1", quantityCost);

        RawMaterial material = new RawMaterial("mat1", "Material 1", "Description 1", 100, 10);
        when(rawMaterialRepository.getRawMaterialByID(connection, "mat1")).thenReturn(material);

        assertThrows(SupplyOfferException.class, () -> {
            supplyOfferService.registerSupplyOffer(
                    supplier, supplyOfferID, "Main St", invalidZipCode,
                    "Springfield", "USA", startDate, endDate, new HashMap<>());
        });
    }

    @Test
    public void testRegisterSupplyOffer_ExistingAddress() throws ParseException {
        String deliveryStreet = "Main St";
        String deliveryZipCode = "1111-111";
        String deliveryTown = "Springfield";
        String deliveryCountry = "USA";
        Address existingAddress = new Address(5, deliveryStreet, deliveryZipCode, deliveryTown, deliveryCountry);

        Supplier supplier = new Supplier(1, "Supplier Name", 123456789, "supplier@example.com");
        int supplyOfferID = 1;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date startDate = sdf.parse("26/01/2024");
        Date endDate = sdf.parse("26/01/2026");

        Map<String, Map<Integer, Double>> rawMaterialIDsQuantityCost = new HashMap<>();
        Map<Integer, Double> quantityCost = new HashMap<>();
        quantityCost.put(10, 5.0);
        rawMaterialIDsQuantityCost.put("mat1", quantityCost);

        RawMaterial material = new RawMaterial("mat1", "Material 1", "Description 1", 100, 10);
        when(rawMaterialRepository.getRawMaterialByID(connection, "mat1")).thenReturn(material);
        when(addressRepository.findAddress(connection, deliveryStreet, deliveryZipCode, deliveryTown, deliveryCountry))
                .thenReturn(existingAddress);

        SupplyOffer supplyOffer = supplyOfferService.registerSupplyOffer(
                supplier, supplyOfferID, deliveryStreet, deliveryZipCode,
                deliveryTown, deliveryCountry, startDate, endDate, new HashMap<>());

        assertNotNull(supplyOffer);
        assertEquals(supplyOfferID, supplyOffer.getId());
        assertEquals(existingAddress, supplyOffer.getDeliveryAddress());

        verify(supplyOfferRepository, times(1)).save(eq(connection), any(SupplyOffer.class), any(Supplier.class));
        verify(addressRepository, never()).save(eq(connection), any(Address.class));
    }

    @Test
    public void testRegisterSupplyOffer_RawMaterialNotFound() throws ParseException {
        Supplier supplier = new Supplier(1, "Supplier Name", 123456789, "supplier@example.com");
        int supplyOfferID = 1;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date startDate = sdf.parse("26/01/2024");
        Date endDate = sdf.parse("26/01/2026");

        Map<String, Map<Integer, Double>> rawMaterialIDsQuantityCost = new HashMap<>();
        Map<Integer, Double> quantityCost = new HashMap<>();
        quantityCost.put(10, 5.0);
        rawMaterialIDsQuantityCost.put("mat1", quantityCost);
        rawMaterialIDsQuantityCost.put("mat2", quantityCost);

        RawMaterial material1 = new RawMaterial("mat1", "Material 1", "Description 1", 100, 10);
        when(rawMaterialRepository.getRawMaterialByID(connection, "mat1")).thenReturn(material1);
        when(rawMaterialRepository.getRawMaterialByID(connection, "mat2")).thenReturn(null);

        assertThrows(ProductException.class, () -> {
            supplyOfferController.registerSupplyOffer(
                    supplier, supplyOfferID, "Main St", "1111-111",
                    "Springfield", "USA", startDate, endDate, rawMaterialIDsQuantityCost);
        });
    }

}