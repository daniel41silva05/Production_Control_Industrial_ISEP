package org.project.io;

import org.project.dto.OperationDTO;
import org.project.model.OperationType;
import org.project.model.Workstation;
import org.project.model.WorkstationType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CsvReader {

    public static List<OperationDTO> loadOperations(String operationsFile) {
        List<OperationDTO> operations = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(operationsFile))) {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");

                if (values.length < 4) {
                    continue;
                }

                try {
                    int id = Integer.parseInt(values[0].trim());
                    String name = values[1].trim();
                    int executionTime = Integer.parseInt(values[2].trim());
                    int typeId = Integer.parseInt(values[3].trim());

                    operations.add(new OperationDTO(id, typeId, name, executionTime));

                } catch (NumberFormatException e) {
                    continue;
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading operations: " + e.getMessage());
        }
        return operations;
    }

    public static List<OperationType> loadOperationTypes(String operationTypesFile) {
        List<OperationType> operationTypes = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(operationTypesFile))) {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");

                if (values.length < 2) {
                    continue;
                }

                try {
                    int id = Integer.parseInt(values[0].trim());
                    String name = values[1].trim();

                    operationTypes.add(new OperationType(id, name));

                } catch (NumberFormatException e) {
                    continue;
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading operation types: " + e.getMessage());
        }
        return operationTypes;
    }

    public static Map<WorkstationType, Map<Integer, Integer>> loadWorkstationTypes(String workstationTypesFile) {
        Map<WorkstationType, Map<Integer, Integer>> map = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(workstationTypesFile))) {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");

                if (values.length < 4) {
                    continue;
                }

                try {
                    int workstationID = Integer.parseInt(values[0].trim());
                    String workstationName = values[1].trim();
                    int operationID = Integer.parseInt(values[2].trim());
                    int setupTime = Integer.parseInt(values[3].trim());

                    WorkstationType workstationType = new WorkstationType(workstationID, workstationName);
                    if (!map.containsKey(workstationType)) {
                        Map<Integer, Integer> indoorMap = new HashMap<>();
                        indoorMap.put(operationID, setupTime);
                        map.put(workstationType, indoorMap);
                    } else {
                        Map<Integer, Integer> indoorMap = map.get(workstationType);
                        indoorMap.put(operationID, setupTime);
                    }

                } catch (NumberFormatException e) {
                    continue;
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading workstation types: " + e.getMessage());
        }
        return map;
    }

    public static Map<Workstation, Integer> loadWorkstations(String workstationsFile) {
        Map<Workstation, Integer> workstationTypeMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(workstationsFile))) {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");

                if (values.length < 3) {
                    continue;
                }

                try {
                    int id = Integer.parseInt(values[0].trim());
                    String name = values[1].trim();
                    int typeId = Integer.parseInt(values[2].trim());

                    workstationTypeMap.put(new Workstation(id, name), typeId);

                } catch (NumberFormatException e) {
                    continue;
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading workstations: " + e.getMessage());
        }
        return workstationTypeMap;
    }

}
