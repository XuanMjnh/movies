package com.streaming.movieplatform.controller.admin;

import com.streaming.movieplatform.controller.support.FormFlowSupport;
import com.streaming.movieplatform.dto.AdminPlanRequest;
import com.streaming.movieplatform.entity.SubscriptionPlan;
import com.streaming.movieplatform.service.AdminPlanManagementService;
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
@RequestMapping("/admin/plans")
public class AdminPlanManagementController {

    private final AdminPlanManagementService adminPlanManagementService;

    public AdminPlanManagementController(AdminPlanManagementService adminPlanManagementService) {
        this.adminPlanManagementService = adminPlanManagementService;
    }

    @GetMapping
    public String plans(Model model) {
        populatePlansPage(model);
        FormFlowSupport.addIfAbsent(model, "planForm", AdminPlanRequest::new);
        return "admin/plans";
    }

    @GetMapping("/{planId}/edit")
    public String editPlan(@PathVariable Long planId, Model model) {
        populatePlansPage(model);
        model.addAttribute("planForm", createPlanForm(adminPlanManagementService.getPlanById(planId)));
        return "admin/plans";
    }

    @PostMapping("/save")
    public String savePlan(@Valid @ModelAttribute("planForm") AdminPlanRequest request,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populatePlansPage(model);
            return "admin/plans";
        }
        adminPlanManagementService.savePlan(request);
        redirectAttributes.addFlashAttribute("successMessage", "Luu goi thanh vien thanh cong");
        return "redirect:/admin/plans";
    }

    @PostMapping("/{planId}/delete")
    public String deletePlan(@PathVariable Long planId, RedirectAttributes redirectAttributes) {
        adminPlanManagementService.deletePlan(planId);
        redirectAttributes.addFlashAttribute("successMessage", "Da xoa goi thanh vien");
        return "redirect:/admin/plans";
    }

    private AdminPlanRequest createPlanForm(SubscriptionPlan plan) {
        AdminPlanRequest form = new AdminPlanRequest();
        form.setId(plan.getId());
        form.setName(plan.getName());
        form.setAccessLevel(plan.getAccessLevel());
        form.setPrice(plan.getPrice());
        form.setDurationDays(plan.getDurationDays());
        form.setActive(plan.isActive());
        form.setFeatureDescription(plan.getFeatureDescription());
        return form;
    }

    private void populatePlansPage(Model model) {
        model.addAttribute("plans", adminPlanManagementService.getAllPlans());
    }
}
