package com.streaming.movieplatform.repository;

import com.streaming.movieplatform.entity.Director;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import java.util.Optional;

public interface DirectorRepository extends JpaRepository<Director, Long> {
    List<Director> findAllByOrderByNameAsc();
    Optional<Director> findByNameIgnoreCase(String name);
}