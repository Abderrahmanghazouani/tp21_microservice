package com.example.car.repositories;

import com.example.car.entities.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {
    // Methode derivee : SELECT ... FROM car WHERE client_id = ?
    List<Car> findByClientId(Long clientId);
}
