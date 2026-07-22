package com.liquor.liquor.repository;

import java.util.UUID;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.liquor.liquor.entity.Brand;

public interface BrandRepository
extends JpaRepository<Brand, UUID>{

    boolean existsByName(String name);

    Optional<Brand> findByName(String name);

}
