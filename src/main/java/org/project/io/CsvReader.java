package org.project.io;

import org.project.dto.OperationDTO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
                    int typeId = Integer.parseInt(values[1].trim());
                    String name = values[2].trim();
                    int executionTime = Integer.parseInt(values[3].trim());

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


}
