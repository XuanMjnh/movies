package com.streaming.movieplatform.controller.admin;

import com.streaming.movieplatform.controller.support.FormFlowSupport;
import com.streaming.movieplatform.dto.AdminUserUpdateRequest;
import com.streaming.movieplatform.entity.User;
import com.streaming.movieplatform.exception.BusinessException;
import com.streaming.movieplatform.service.AdminUserManagementService;
import com.streaming.movieplatform.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin/users")
public class AdminUserManagementController {

    private final AdminUserManagementService adminUserManagementService;
    private final WalletService walletService;

    public AdminUserManagementController(AdminUserManagementService adminUserManagementService,
                                         WalletService walletService) {
        this.adminUserManagementService = adminUserManagementService;
        this.walletService = walletService;
    }

    @GetMapping
    public String users(Model model) {
        populateUsersPage(model);
        FormFlowSupport.addIfAbsent(model, "userForm", AdminUserUpdateRequest::new);
        return "admin/users";
    }

    @GetMapping("/{userId}/edit")
    public String editUser(@PathVariable Long userId, Model model) {
        populateUsersPage(model);
        model.addAttribute("userForm", createUserForm(adminUserManagementService.getUserById(userId)));
        return "admin/users";
    }

    @PostMapping("/save")
    public String saveUser(@Valid @ModelAttribute("userForm") AdminUserUpdateRequest request,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateUsersPage(model);
            return "admin/users";
        }
        adminUserManagementService.updateUser(request);
        redirectAttributes.addFlashAttribute("successMessage", "Cap nhat nguoi dung thanh cong");
        return "redirect:/admin/users";
    }

    @PostMapping("/{userId}/wallet-adjust")
    public String adjustWallet(@PathVariable Long userId,
                               @RequestParam BigDecimal amount,
                               @RequestParam String action,
                               @RequestParam(required = false) String note,
                               RedirectAttributes redirectAttributes) {
        try {
            walletService.adjustBalanceByAdmin(userId, amount, "ADD".equalsIgnoreCase(action), note);
            redirectAttributes.addFlashAttribute("successMessage", "Dieu chinh so du thanh cong");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/users";
    }

    private AdminUserUpdateRequest createUserForm(User user) {
        AdminUserUpdateRequest form = new AdminUserUpdateRequest();
        form.setId(user.getId());
        form.setFullName(user.getFullName());
        form.setEmail(user.getEmail());
        form.setPhone(user.getPhone());
        form.setEnabled(user.isEnabled());
        form.setRoleNames(user.getRoles().stream().map(role -> role.getName().name()).toList());
        return form;
    }

    private void populateUsersPage(Model model) {
        model.addAttribute("users", adminUserManagementService.getAllUsers());
        model.addAttribute("roles", adminUserManagementService.getAllRoles());
    }
}
