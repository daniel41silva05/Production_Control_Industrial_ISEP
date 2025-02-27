package org.project.service;

import org.project.data.ConnectionFactory;
import org.project.data.DatabaseConnection;
import org.project.exceptions.WorkstationException;
import org.project.model.Workstation;
import org.project.model.WorkstationType;
import org.project.repository.Repositories;
import org.project.repository.WorkstationRepository;
import org.project.repository.WorkstationTypeRepository;

import java.util.List;

public class WorkstationService {

    private DatabaseConnection connection;
    private WorkstationRepository workstationRepository;
    private WorkstationTypeRepository workstationTypeRepository;

    public WorkstationService () {
        connection = ConnectionFactory.getInstance().getDatabaseConnection();
        Repositories repositories = Repositories.getInstance();
        workstationRepository = repositories.getWorkstationRepository();
        workstationTypeRepository = repositories.getWorkstationTypeRepository();
    }

    public List<WorkstationType> getWorkstationTypes() {
        return workstationTypeRepository.getAll(connection);
    }

    private WorkstationType getWorkstationTypeById(int id) throws WorkstationException {
        if (!workstationTypeRepository.getWorkstationTypeExists(connection, id)) {
            throw new WorkstationException("Workstation Type with ID " + id + " not exists.");
        }

        return workstationTypeRepository.getById(connection, id);
    }

    public Workstation registerWorkstation (int id, String name, int typeId) throws WorkstationException {
        if (workstationRepository.getWorkstationExists(connection, id)) {
            throw new WorkstationException("Workstation with ID " + id + " already exists.");
        }

        WorkstationType type = getWorkstationTypeById(typeId);

        Workstation workstation = new Workstation(id, name);
        boolean success = workstationRepository.save(connection, workstation, type);
        if (!success) {
            return null;
        }
        return workstation;
    }

    public WorkstationType registerWorkstationType (int id, String name) throws WorkstationException {
        if (workstationTypeRepository.getWorkstationTypeExists(connection, id)) {
            throw new WorkstationException("Workstation Type with ID " + id + " already exists.");
        }

        WorkstationType workstationType = new WorkstationType(id, name);
        boolean success = workstationTypeRepository.save(connection, workstationType);
        if (!success) {
            return null;
        }
        return workstationType;
    }

    public Workstation deleteWorkstation (int id) throws WorkstationException {
        if (!workstationRepository.getWorkstationExists(connection, id)) {
            throw new WorkstationException("Workstation with ID " + id + " not exists.");
        }

        Workstation workstation = workstationRepository.getById(connection, id);

        boolean success = workstationRepository.delete(connection, workstation);
        if (!success) {
            return null;
        }
        return workstation;
    }

    public WorkstationType deleteWorkstationType (int id) throws WorkstationException {
        WorkstationType workstationType = getWorkstationTypeById(id);

        if (!workstationType.getWorkstations().isEmpty()) {
            throw new WorkstationException("Workstation Type cannot be deleted because it contains active workstations.");
        }

        boolean success = workstationTypeRepository.delete(connection, workstationType);
        if (!success) {
            return null;
        }
        return workstationType;
    }
}
