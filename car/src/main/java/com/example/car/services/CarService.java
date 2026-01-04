package com.example.car.services;

import com.example.car.clients.ClientRestClient;
import com.example.car.entities.Car;
import com.example.car.entities.Client;
import com.example.car.models.CarResponse;
import com.example.car.repositories.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

@Service
public class CarService {
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ClientRestClient clientRestClient;
    @Autowired
    private WebClient.Builder webClientBuilder;

    private final String SERVICE_URL = "http://SERVICE-CLIENT";

    // --- RestTemplate Implementation ---
    public List<CarResponse> findAll() {
        List<Car> cars = carRepository.findAll();
        ResponseEntity<Client[]> response = restTemplate.getForEntity(SERVICE_URL + "/api/client", Client[].class);
        Client[] clients = response.getBody();
        return cars.stream().map((Car car) -> mapToCarResponse(car, clients)).toList();
    }

    public CarResponse findById(Long id) throws Exception {
        Car car = carRepository.findById(id).orElseThrow(() -> new Exception("Invalid Car Id"));
        Client client = restTemplate.getForObject(SERVICE_URL + "/api/client/" + car.getClient_id(), Client.class);
        return mapToCarResponse(car, client);
    }

    // --- Feign Implementation ---
    public List<CarResponse> findAllFeign() {
        List<Car> cars = carRepository.findAll();
        List<Client> clients = clientRestClient.findAll();
        // Convert List to Array for reusing the mapper or adjust mapper
        Client[] clientArray = clients.toArray(new Client[0]);
        return cars.stream().map((Car car) -> mapToCarResponse(car, clientArray)).toList();
    }

    public CarResponse findByIdFeign(Long id) throws Exception {
        Car car = carRepository.findById(id).orElseThrow(() -> new Exception("Invalid Car Id"));
        Client client = clientRestClient.findById(car.getClient_id());
        return mapToCarResponse(car, client);
    }

    // --- WebClient Implementation ---
    public List<CarResponse> findAllWebClient() {
        List<Car> cars = carRepository.findAll();
        Client[] clients = webClientBuilder.build()
                .get()
                .uri(SERVICE_URL + "/api/client")
                .retrieve()
                .bodyToMono(Client[].class)
                .block(); // Blocking for synchronous return, acceptable for this TP context

        return cars.stream().map((Car car) -> mapToCarResponse(car, clients)).toList();
    }

    public CarResponse findByIdWebClient(Long id) throws Exception {
        Car car = carRepository.findById(id).orElseThrow(() -> new Exception("Invalid Car Id"));
        Client client = webClientBuilder.build()
                .get()
                .uri(SERVICE_URL + "/api/client/" + car.getClient_id())
                .retrieve()
                .bodyToMono(Client.class)
                .block();
        return mapToCarResponse(car, client);
    }

    // --- Mappers ---
    private CarResponse mapToCarResponse(Car car, Client[] clients) {
        Client foundClient = Arrays.stream(clients)
                .filter(client -> client.getId().equals(car.getClient_id()))
                .findFirst()
                .orElse(null);

        return mapToCarResponse(car, foundClient);
    }

    private CarResponse mapToCarResponse(Car car, Client client) {
        return CarResponse.builder()
                .id(car.getId())
                .brand(car.getBrand())
                .client(client)
                .matricue(car.getMatricule())
                .model(car.getModel())
                .build();
    }
}
