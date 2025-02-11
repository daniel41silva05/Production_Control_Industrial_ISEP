package org.project.controller;

import org.project.domain.Client;
import org.project.repository.ClientRepository;
import org.project.repository.Repositories;

public class RegisterClientController {

    private ClientRepository clientRepository;
    private Client client;

    public RegisterClientController() {
        getClientRepository();
    }

    public Client registerClient (String key) {
        Client client = null;
        return client;
    }

    public ClientRepository getClientRepository() {
        if (clientRepository == null) {
            Repositories repositories = Repositories.getInstance();
            clientRepository = repositories.getClientRepository();
        }
        return clientRepository;
    }

}
