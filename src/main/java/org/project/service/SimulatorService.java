package org.project.service;

import org.project.common.NaryTree;
import org.project.common.NaryTreeNode;
import org.project.exceptions.ProductException;
import org.project.model.*;

import java.util.*;

public class SimulatorService {

    private int currentTime;
    private NaryTree<ProductionElement> tree;
    private HashMap<Integer, Set<ProductionElement>> elementsByLevel;
    private PriorityQueue<Event> eventsQueue;
    private LinkedList<Event> events;

    public SimulatorService(NaryTree<ProductionElement> tree) {
        this.currentTime = 0;
        this.tree = tree;
        this.elementsByLevel = new HashMap<>();
        this.eventsQueue = new PriorityQueue<>();
        this.events = new LinkedList<>();
    }

    private void initializeLevels (NaryTreeNode<ProductionElement> node) {
        if (node == null) {
            return;
        }

        int level = node.getLevel();
        ProductionElement element = node.getElement();

        elementsByLevel.computeIfAbsent(level, k -> new HashSet<>()).add(element);

        for (NaryTreeNode<ProductionElement> child : node.getChildren()) {
            initializeLevels(child);
        }
    }

    public LinkedList<Event> simulator () {
        initializeLevels(tree.getRoot());

        int maxLevel = elementsByLevel.keySet().stream().max(Integer::compare).orElse(0);

        for (int level = maxLevel; level >= 0; level--) {
            Set<ProductionElement> elements = elementsByLevel.get(level);
            if (elements == null) continue;

            runSimulation(elements);
        }

        return events;
    }

    private void runSimulation (Set<ProductionElement> elements) {
        do {
            updateCurrentTime();

            processCompletedEvents();

            for (ProductionElement element : elements) {
                if (!element.isProcessed()) {
                    processEvent(element);
                }
            }

        } while (!eventsQueue.isEmpty());

        for (ProductionElement element : elements) {
            if (!element.isProcessed()) {
                throw new ProductException("No workstations available to process: " + element.getPart().getId());
            }
        }

    }

    private void updateCurrentTime () {
        if (!eventsQueue.isEmpty()) {
            Event event = eventsQueue.peek();
            currentTime = event.getEndTime();
        }
    }

    private void processCompletedEvents() {

        while (!eventsQueue.isEmpty() && eventsQueue.peek().getEndTime() == currentTime) {

            Event event = eventsQueue.poll();
            Workstation workStation = event.getWorkStation();
            workStation.setAvailable(true);

        }
    }

    private void processEvent(ProductionElement element) {
        Operation operation = element.getOperation();

        Map<WorkstationType, Integer> workstationTypeTimeMap = operation.getType().getWorkstationSetupTime();

        Workstation bestWorkstation = null;
        int minSetupTime = Integer.MAX_VALUE;

        for (Map.Entry<WorkstationType, Integer> entry : workstationTypeTimeMap.entrySet()) {
            WorkstationType wsType = entry.getKey();
            int setupTime = entry.getValue();

            for (Workstation ws : wsType.getWorkstations()) {
                if (ws.isAvailable() && setupTime < minSetupTime) {
                    minSetupTime = setupTime;
                    bestWorkstation = ws;
                }
            }
        }

        int duration = (int) ((element.getQuantity() * operation.getExecutionTime()) + minSetupTime);

        if (bestWorkstation != null) {
            bestWorkstation.setAvailable(false);
            element.setProcessed(true);

            Event event = new Event(element, bestWorkstation, currentTime, currentTime + duration);
            eventsQueue.add(event);
            events.add(event);
        }
    }

}
