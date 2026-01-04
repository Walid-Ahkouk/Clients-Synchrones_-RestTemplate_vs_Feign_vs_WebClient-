package com.example.car.controllers;

import com.example.car.models.CarResponse;
import com.example.car.services.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/car")
public class CarController {
    @Autowired
    private CarService carService;

    // --- RestTemplate (Default) ---
    @GetMapping
    public List<CarResponse> findAll() {
        return carService.findAll();
    }

    @GetMapping("/{id}")
    public CarResponse findById(@PathVariable Long id) throws Exception {
        return carService.findById(id);
    }

    // --- Feign ---
    @GetMapping("/feign")
    public List<CarResponse> findAllFeign() {
        return carService.findAllFeign();
    }

    @GetMapping("/feign/{id}")
    public CarResponse findByIdFeign(@PathVariable Long id) throws Exception {
        return carService.findByIdFeign(id);
    }

    // --- WebClient ---
    @GetMapping("/webclient")
    public List<CarResponse> findAllWebClient() {
        return carService.findAllWebClient();
    }

    @GetMapping("/webclient/{id}")
    public CarResponse findByIdWebClient(@PathVariable Long id) throws Exception {
        return carService.findByIdWebClient(id);
    }
}