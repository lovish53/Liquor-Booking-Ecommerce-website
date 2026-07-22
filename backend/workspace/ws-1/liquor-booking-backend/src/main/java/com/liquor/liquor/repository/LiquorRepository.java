package com.liquor.liquor.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.liquor.liquor.entity.Liquor;

public interface LiquorRepository
extends JpaRepository<Liquor, UUID> {

boolean existsByName(String name);

}