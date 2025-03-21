package org.project.io;

import org.project.dto.OperationDTO;
import org.project.dto.ProductionElementDTO;
import org.project.model.*;

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

    public static List<Component> loadComponents(String componentsFile) {
        List<Component> components = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(componentsFile))) {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");

                if (values.length < 3) {
                    continue;
                }

                String id = values[0].trim();
                String name = values[1].trim();
                String description = values[1].trim();

                components.add(new Component(id, name, description));

            }
        } catch (IOException e) {
            System.err.println("Error loading components: " + e.getMessage());
        }
        return components;
    }

    public static List<RawMaterial> loadRawMaterials(String rawMaterialsFile) {
        List<RawMaterial> rawMaterials = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(rawMaterialsFile))) {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");

                if (values.length < 5) {
                    continue;
                }

                try {
                    String id = values[0].trim();
                    String name = values[1].trim();
                    String description = values[2].trim();
                    int currentStock = Integer.parseInt(values[3].trim());
                    int minimumStock = Integer.parseInt(values[4].trim());

                    rawMaterials.add(new RawMaterial(id, name, description, currentStock, minimumStock));

                } catch (NumberFormatException e) {
                    continue;
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading raw materials: " + e.getMessage());
        }
        return rawMaterials;
    }

    public static HashMap<ProductionElementDTO, List<Integer>> loadBOO(String booFile) {
        HashMap<ProductionElementDTO, List<Integer>> elementNextOperationsDto = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(booFile))) {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");

                if (values.length < 4) {
                    continue;
                }

                try {
                    int opId = Integer.parseInt(values[0].trim());
                    String partId = values[1].trim();
                    int quantity = Integer.parseInt(values[2].trim());

                    ProductionElementDTO element = new ProductionElementDTO(opId, partId, quantity);
                    List<Integer> nextOperations = new ArrayList<>();

                    for (int i = 3; i < values.length; i++) {
                        nextOperations.add(Integer.parseInt(values[i].trim()));
                    }

                    elementNextOperationsDto.put(element, nextOperations);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format in BOO file: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading BOO file: " + e.getMessage());
        }

        return elementNextOperationsDto;
    }


}
