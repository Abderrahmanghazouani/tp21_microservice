package com.example.car.services;

import com.example.car.entities.Client;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ClientApi {
    private final WebClient.Builder builder;

    public ClientApi(WebClient.Builder builder) {
        this.builder = builder;
    }

    public Client findClientById(Long id) {
        return builder.build()
                .get()                                              // requete HTTP GET
                .uri("http://SERVICE-CLIENT/api/clients/" + id)    // URL LOGIQUE (nom Eureka)
                .retrieve()                                         // execute + lit la reponse
                .bodyToMono(Client.class)                           // 1 objet attendu -> Mono<Client>
                .block();                                           // attente bloquante (simple pour le TP)
    }
}
