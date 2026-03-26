package com.streaming.movieplatform.repository;

import com.streaming.movieplatform.entity.Actor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import java.util.Optional;

public interface ActorRepository extends JpaRepository<Actor, Long> {
    List<Actor> findAllByOrderByNameAsc();
    Optional<Actor> findByNameIgnoreCase(String name);
}