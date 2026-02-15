package com.dtsolution.godfellas.service;


import com.dtsolution.godfellas.entity.Client;
import com.dtsolution.godfellas.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientService {

    private final ClientRepository clientRepo;

    public Client addClient(Client client) {
        log.info("Adding new client: {}", client.getName());
        return clientRepo.save(client);
    }

    public List<Client> getAllClients() {
        return clientRepo.findAll();
    }
}

