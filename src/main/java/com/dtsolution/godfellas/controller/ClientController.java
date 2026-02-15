package com.dtsolution.godfellas.controller;

import com.dtsolution.godfellas.entity.Client;
import com.dtsolution.godfellas.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<Client> create(@RequestBody Client client) {
        return ResponseEntity.ok(clientService.addClient(client));
    }

    @GetMapping
    public ResponseEntity<List<Client>> all() {
        return ResponseEntity.ok(clientService.getAllClients());
    }
}
