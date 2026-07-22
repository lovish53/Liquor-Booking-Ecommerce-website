package com.liquor.liquor.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.liquor.liquor.entity.Category;

public interface CategoryRepository
extends JpaRepository<Category, UUID> {

boolean existsByName(String name);

Optional<Category> findByName(String name);

}
