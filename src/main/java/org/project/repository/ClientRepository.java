package org.project.repository;

import org.project.domain.Client;
import java.util.ArrayList;
import java.util.List;

public class ClientRepository {

    private List<Client> clients;

    public ClientRepository(List<Client> clients) {
        this.clients = clients;
    }

    public ClientRepository() {
        this.clients = new ArrayList<>();
    }

    public void addClient(Client client) {
        clients.add(client);
    }

    public void addClients(List<Client> clients) {
        this.clients.addAll(clients);
    }

    public List<Client> getAllClients() {
        return new ArrayList<>(clients);
    }

    public void clearClients() {
        clients.clear();
    }

    public boolean isEmpty() {
        return clients.isEmpty();
    }

    public int getClientsCount() {
        return clients.size();
    }
}
