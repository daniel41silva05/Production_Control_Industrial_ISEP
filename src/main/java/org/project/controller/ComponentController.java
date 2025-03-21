package org.project.controller;

import org.project.model.Component;
import org.project.service.ComponentService;

import java.util.List;

public class ComponentController {

    private ComponentService componentService;

    public ComponentController() {
        componentService = new ComponentService();
    }

    public List<Component> getComponents() {
        return componentService.getComponents();
    }

    public Component registerComponent(String id, String name, String description) {
        return componentService.registerComponent(id, name, description);
    }

    public List<Component> registerComponentsFromCSV(String filePath) {
        return componentService.registerComponentsFromCSV(filePath);
    }


}
