package com.streaming.movieplatform.controller.admin;

import com.streaming.movieplatform.controller.support.FormFlowSupport;
import com.streaming.movieplatform.dto.AdminBannerRequest;
import com.streaming.movieplatform.entity.Banner;
import com.streaming.movieplatform.service.AdminBannerManagementService;
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
@RequestMapping("/admin/banners")
public class AdminBannerManagementController {

    private final AdminBannerManagementService adminBannerManagementService;

    public AdminBannerManagementController(AdminBannerManagementService adminBannerManagementService) {
        this.adminBannerManagementService = adminBannerManagementService;
    }

    @GetMapping
    public String banners(Model model) {
        populateBannersPage(model);
        FormFlowSupport.addIfAbsent(model, "bannerForm", AdminBannerRequest::new);
        return "admin/banners";
    }

    @GetMapping("/{bannerId}/edit")
    public String editBanner(@PathVariable Long bannerId, Model model) {
        populateBannersPage(model);
        model.addAttribute("bannerForm", createBannerForm(adminBannerManagementService.getBannerById(bannerId)));
        return "admin/banners";
    }

    @PostMapping("/save")
    public String saveBanner(@Valid @ModelAttribute("bannerForm") AdminBannerRequest request,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateBannersPage(model);
            return "admin/banners";
        }
        adminBannerManagementService.saveBanner(request);
        redirectAttributes.addFlashAttribute("successMessage", "Luu banner thanh cong");
        return "redirect:/admin/banners";
    }

    @PostMapping("/{bannerId}/delete")
    public String deleteBanner(@PathVariable Long bannerId, RedirectAttributes redirectAttributes) {
        adminBannerManagementService.deleteBanner(bannerId);
        redirectAttributes.addFlashAttribute("successMessage", "Da xoa banner");
        return "redirect:/admin/banners";
    }

    private AdminBannerRequest createBannerForm(Banner banner) {
        AdminBannerRequest form = new AdminBannerRequest();
        form.setId(banner.getId());
        form.setTitle(banner.getTitle());
        form.setSubtitle(banner.getSubtitle());
        form.setExistingImageUrl(banner.getImageUrl());
        form.setCtaText(banner.getCtaText());
        form.setCtaLink(banner.getCtaLink());
        form.setDisplayOrder(banner.getDisplayOrder());
        form.setActive(banner.isActive());
        form.setMovieId(banner.getMovie() == null ? null : banner.getMovie().getId());
        return form;
    }

    private void populateBannersPage(Model model) {
        model.addAttribute("banners", adminBannerManagementService.getAllBanners());
        model.addAttribute("movies", adminBannerManagementService.getAvailableMovies());
    }
}
