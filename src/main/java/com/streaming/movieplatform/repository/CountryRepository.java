package com.streaming.movieplatform.repository;

import com.streaming.movieplatform.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long> {
    List<Country> findAllByOrderByNameAsc();
    Optional<Country> findByNameIgnoreCase(String name);
}
