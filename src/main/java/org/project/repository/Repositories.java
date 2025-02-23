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

    /**
     * Constructs a new Repositories object, initializing all repositories.
     */
    private Repositories() {
        clientRepository = new ClientRepository();
        addressRepository = new AddressRepository();
        orderRepository = new OrderRepository();
        productRepository = new ProductRepository();
        productCategoryRepository = new ProductCategoryRepository();
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

}

