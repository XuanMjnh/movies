package com.streaming.movieplatform.repository;

import com.streaming.movieplatform.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByMovieIdAndHiddenFalseOrderByCreatedAtDesc(Long movieId);
    List<Comment> findAllByOrderByCreatedAtDesc();
    void deleteByMovieId(Long movieId);
}
