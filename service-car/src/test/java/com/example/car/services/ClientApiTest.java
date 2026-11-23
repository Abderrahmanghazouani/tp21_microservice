package com.example.car.services;

import com.example.car.entities.Client;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

// Test unitaire : un faux service-client (serveur HTTP du JDK) + un filtre qui joue le role du
// load balancer (remplace l'hote logique SERVICE-CLIENT par localhost:port)
class ClientApiTest {
    HttpServer server;
    int port;
    String dernierChemin;

    @BeforeEach
    void start() throws Exception {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/api/clients/", ex -> {
            dernierChemin = ex.getRequestURI().getPath();
            boolean existe = dernierChemin.endsWith("/1");
            byte[] body = (existe ? "{\"id\":1,\"nom\":\"Salma\",\"age\":22.0}" : "").getBytes(StandardCharsets.UTF_8);
            ex.getResponseHeaders().add("Content-Type", "application/json");
            ex.sendResponseHeaders(existe ? 200 : 404, existe ? body.length : -1);
            if (existe) ex.getResponseBody().write(body);
            ex.close();
        });
        server.start();
        port = server.getAddress().getPort();
    }

    @AfterEach
    void stop() { server.stop(0); }

    private ClientApi api() {
        WebClient.Builder builder = WebClient.builder().filter((request, next) -> {
            assertEquals("SERVICE-CLIENT", request.url().getHost());     // URL logique utilisee
            URI reel = URI.create(request.url().toString().replace("SERVICE-CLIENT", "localhost:" + port));
            return next.exchange(org.springframework.web.reactive.function.client.ClientRequest.from(request).url(reel).build());
        });
        return new ClientApi(builder);
    }

    @Test
    void recupereLeClientParSonNomLogique() {
        Client c = api().findClientById(1L);
        assertEquals("Salma", c.getNom());
        assertEquals(22.0f, c.getAge());
        assertEquals("/api/clients/1", dernierChemin);
    }

    @Test
    void clientInexistantLeveUneErreur404() {
        WebClientResponseException e = assertThrows(WebClientResponseException.class, () -> api().findClientById(99L));
        assertEquals(404, e.getStatusCode().value());
    }
}
