package com.streaming.movieplatform.service.impl;

import com.streaming.movieplatform.entity.Comment;
import com.streaming.movieplatform.entity.Movie;
import com.streaming.movieplatform.entity.User;
import com.streaming.movieplatform.exception.ResourceNotFoundException;
import com.streaming.movieplatform.repository.CommentRepository;
import com.streaming.movieplatform.repository.MovieRepository;
import com.streaming.movieplatform.service.CommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final MovieRepository movieRepository;

    public CommentServiceImpl(CommentRepository commentRepository, MovieRepository movieRepository) {
        this.commentRepository = commentRepository;
        this.movieRepository = movieRepository;
    }

    @Override
    public Comment addComment(User user, Long movieId, String content) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phim"));
        Comment comment = new Comment();
        comment.setMovie(movie);
        comment.setUser(user);
        comment.setContent(content.trim());
        comment.setHidden(false);
        return commentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getVisibleComments(Long movieId) {
        return commentRepository.findByMovieIdAndHiddenFalseOrderByCreatedAtDesc(movieId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getAllComments() {
        return commentRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public void toggleHidden(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bình luận"));
        comment.setHidden(!comment.isHidden());
        commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}
