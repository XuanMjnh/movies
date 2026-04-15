package com.streaming.movieplatform.controller.admin;

import com.streaming.movieplatform.service.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/comments")
public class AdminCommentManagementController {

    private final CommentService commentService;

    public AdminCommentManagementController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public String comments(Model model) {
        model.addAttribute("comments", commentService.getAllComments());
        return "admin/comments";
    }

    @PostMapping("/{commentId}/toggle")
    public String toggleComment(@PathVariable Long commentId, RedirectAttributes redirectAttributes) {
        commentService.toggleHidden(commentId);
        redirectAttributes.addFlashAttribute("successMessage", "Da cap nhat trang thai binh luan");
        return "redirect:/admin/comments";
    }

    @PostMapping("/{commentId}/delete")
    public String deleteComment(@PathVariable Long commentId, RedirectAttributes redirectAttributes) {
        commentService.deleteComment(commentId);
        redirectAttributes.addFlashAttribute("successMessage", "Da xoa binh luan");
        return "redirect:/admin/comments";
    }
}
