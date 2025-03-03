package org.project.repository;

/**
 * This class manages singleton instances of repositories.
 */
public class Repositories {

    /** The singleton instance of Repositories. */
    private static Repositories instance;
    private ClientRepository clientRepository;
    private AddressRepository addressRepository;
    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    private ProductCategoryRepository productCategoryRepository;
    private ComponentRepository componentRepository;
    private RawMaterialRepository rawMaterialRepository;
    private ProductionTreeRepository productionTreeRepository;
    private OperationRepository operationRepository;
    private OperationTypeRepository operationTypeRepository;
    private WorkstationRepository workstationRepository;
    private WorkstationTypeRepository workstationTypeRepository;
    private SupplyOfferRepository supplyOfferRepository;
    private SupplierRepository SupplierRepository;

    /**
     * Constructs a new Repositories object, initializing all repositories.
     */
    private Repositories() {
        clientRepository = new ClientRepository();
        addressRepository = new AddressRepository();
        orderRepository = new OrderRepository();
        productRepository = new ProductRepository();
        productCategoryRepository = new ProductCategoryRepository();
        componentRepository = new ComponentRepository();
        rawMaterialRepository = new RawMaterialRepository();
        productionTreeRepository = new ProductionTreeRepository();
        operationRepository = new OperationRepository();
        operationTypeRepository = new OperationTypeRepository();
        workstationRepository = new WorkstationRepository();
        workstationTypeRepository = new WorkstationTypeRepository();
        supplyOfferRepository = new SupplyOfferRepository();
        SupplierRepository = new SupplierRepository();
    }

    /**
     * Returns the singleton instance of Repositories, creating it if necessary.
     * @return The singleton instance of Repositories.
     */
    public static Repositories getInstance() {
        if (instance == null) {
            synchronized (Repositories.class) {
                if (instance == null) {
                    instance = new Repositories();
                }
            }
        }
        return instance;
    }

    public ClientRepository getClientRepository() {
        return clientRepository;
    }

    public AddressRepository getAddressRepository() {
        return addressRepository;
    }

    public OrderRepository getOrderRepository() {
        return orderRepository;
    }

    public ProductRepository getProductRepository() {
        return productRepository;
    }

    public ProductCategoryRepository getProductCategoryRepository() {
        return productCategoryRepository;
    }

    public ComponentRepository getComponentRepository() {
        return componentRepository;
    }

    public RawMaterialRepository getRawMaterialRepository() {
        return rawMaterialRepository;
    }

    public ProductionTreeRepository getProductionTreeRepository() {
        return productionTreeRepository;
    }

    public OperationRepository getOperationRepository() {
        return operationRepository;
    }

    public OperationTypeRepository getOperationTypeRepository() {
        return operationTypeRepository;
    }

    public WorkstationRepository getWorkstationRepository() {
        return workstationRepository;
    }

    public WorkstationTypeRepository getWorkstationTypeRepository() {
        return workstationTypeRepository;
    }

    public SupplyOfferRepository getSupplyOfferRepository() {
        return supplyOfferRepository;
    }

    public org.project.repository.SupplierRepository getSupplierRepository() {
        return SupplierRepository;
    }
}

