package org.project.service;

import org.project.data.ConnectionFactory;
import org.project.data.DatabaseConnection;
import org.project.exceptions.ProductException;
import org.project.io.CsvReader;
import org.project.model.Component;
import org.project.repository.ComponentRepository;
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

    public List<Component> getComponents() {
        return componentRepository.getAllComponents(connection);
    }

    public Component registerComponent(String id, String name, String description) throws ProductException {
        if (componentRepository.getComponentExists(connection, id)) {
            throw new ProductException("Component with ID " + id + " already exists.");
        }

        Component component = new Component(id, name, description);
        boolean success = componentRepository.saveComponent(connection, component);
        if (!success) {
            return null;
        }
        return component;
    }

    public List<Component> registerComponentsFromCSV(String filePath) {
        List<Component> components = CsvReader.loadComponents(filePath);

        for (Component component : components) {

            if (!componentRepository.getComponentExists(connection, component.getId())) {

                boolean success = componentRepository.saveComponent(connection, component);
                if (!success) {
                    return null;
                }
            }
        }
        return components;
    }

}
