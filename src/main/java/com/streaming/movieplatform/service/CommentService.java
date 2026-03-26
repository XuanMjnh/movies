package com.streaming.movieplatform.service;

import com.streaming.movieplatform.entity.Comment;
import com.streaming.movieplatform.entity.User;

import java.util.List;

public interface CommentService {
    Comment addComment(User user, Long movieId, String content);
    List<Comment> getVisibleComments(Long movieId);
    List<Comment> getAllComments();
    void toggleHidden(Long commentId);
    void deleteComment(Long commentId);
}
