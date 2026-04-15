package com.streaming.movieplatform.controller.admin;

import com.streaming.movieplatform.controller.support.FormFlowSupport;
import com.streaming.movieplatform.dto.AdminGenreRequest;
import com.streaming.movieplatform.entity.Genre;
import com.streaming.movieplatform.service.AdminGenreManagementService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/genres")
public class AdminGenreManagementController {

    private final AdminGenreManagementService adminGenreManagementService;

    public AdminGenreManagementController(AdminGenreManagementService adminGenreManagementService) {
        this.adminGenreManagementService = adminGenreManagementService;
    }

    @GetMapping
    public String genres(Model model) {
        populateGenresPage(model);
        FormFlowSupport.addIfAbsent(model, "genreForm", AdminGenreRequest::new);
        return "admin/genres";
    }

    @GetMapping("/{genreId}/edit")
    public String editGenre(@PathVariable Long genreId, Model model) {
        populateGenresPage(model);
        model.addAttribute("genreForm", createGenreForm(adminGenreManagementService.getGenreById(genreId)));
        return "admin/genres";
    }

    @PostMapping("/save")
    public String saveGenre(@Valid @ModelAttribute("genreForm") AdminGenreRequest request,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateGenresPage(model);
            return "admin/genres";
        }
        adminGenreManagementService.saveGenre(request);
        redirectAttributes.addFlashAttribute("successMessage", "Luu the loai thanh cong");
        return "redirect:/admin/genres";
    }

    @PostMapping("/{genreId}/delete")
    public String deleteGenre(@PathVariable Long genreId, RedirectAttributes redirectAttributes) {
        adminGenreManagementService.deleteGenre(genreId);
        redirectAttributes.addFlashAttribute("successMessage", "Da xoa the loai");
        return "redirect:/admin/genres";
    }

    private AdminGenreRequest createGenreForm(Genre genre) {
        AdminGenreRequest form = new AdminGenreRequest();
        form.setId(genre.getId());
        form.setName(genre.getName());
        form.setDescription(genre.getDescription());
        return form;
    }

    private void populateGenresPage(Model model) {
        model.addAttribute("genres", adminGenreManagementService.getAllGenres());
    }
}
