package org.project.service;

import org.project.data.ConnectionFactory;
import org.project.data.DatabaseConnection;
import org.project.exceptions.ProductException;
import org.project.io.CsvReader;
import org.project.model.Component;
import org.project.repository.ComponentRepository;
import org.project.repository.ProductCategoryRepository;
import org.project.repository.ProductRepository;
import org.project.repository.Repositories;

import java.util.List;

public class ComponentService {

    private DatabaseConnection connection;
    private ComponentRepository componentRepository;

    public ComponentService() {
        connection = ConnectionFactory.getInstance().getDatabaseConnection();
        Repositories repositories = Repositories.getInstance();
        componentRepository = repositories.getComponentRepository();
    }

    public ComponentService(DatabaseConnection connection, ComponentRepository componentRepository) {
        this.connection = connection;
        this.componentRepository = componentRepository;
    }

    public List<Component> getComponents() {
        return componentRepository.getAllComponents(connection);
    }

    public Component registerComponent(String id, String name, String description) {
        if (componentRepository.getComponentExists(connection, id)) {
            throw ProductException.componentAlreadyExists(id);
        }

        Component component = new Component(id, name, description);

        componentRepository.saveComponent(connection, component);

        return component;
    }

    public List<Component> registerComponentsFromCSV(String filePath) {
        List<Component> components = CsvReader.loadComponents(filePath);

        for (Component component : components) {

            if (!componentRepository.getComponentExists(connection, component.getId())) {
                componentRepository.saveComponent(connection, component);
            }
        }
        return components;
    }

}
