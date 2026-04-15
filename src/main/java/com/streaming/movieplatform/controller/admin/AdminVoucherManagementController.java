package com.streaming.movieplatform.controller.admin;

import com.streaming.movieplatform.controller.support.FormFlowSupport;
import com.streaming.movieplatform.dto.AdminVoucherRequest;
import com.streaming.movieplatform.entity.Voucher;
import com.streaming.movieplatform.enums.VoucherAudienceMatchMode;
import com.streaming.movieplatform.enums.VoucherDiscountType;
import com.streaming.movieplatform.exception.BusinessException;
import com.streaming.movieplatform.service.AdminVoucherManagementService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/vouchers")
public class AdminVoucherManagementController {

    private final AdminVoucherManagementService adminVoucherManagementService;

    public AdminVoucherManagementController(AdminVoucherManagementService adminVoucherManagementService) {
        this.adminVoucherManagementService = adminVoucherManagementService;
    }

    @GetMapping
    public String vouchers(Model model) {
        populateVouchersPage(model);
        FormFlowSupport.addIfAbsent(model, "voucherForm", this::createDefaultVoucherForm);
        return "admin/vouchers";
    }

    @GetMapping("/{voucherId}/edit")
    public String editVoucher(@PathVariable Long voucherId, Model model) {
        populateVouchersPage(model);
        model.addAttribute("voucherForm", createVoucherForm(adminVoucherManagementService.getVoucherById(voucherId)));
        return "admin/vouchers";
    }

    @PostMapping("/save")
    public String saveVoucher(@Valid @ModelAttribute("voucherForm") AdminVoucherRequest request,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateVouchersPage(model);
            return "admin/vouchers";
        }
        try {
            adminVoucherManagementService.saveVoucher(request);
            redirectAttributes.addFlashAttribute("successMessage", "Luu voucher thanh cong");
            return "redirect:/admin/vouchers";
        } catch (BusinessException ex) {
            populateVouchersPage(model);
            model.addAttribute("errorMessage", ex.getMessage());
            return "admin/vouchers";
        }
    }

    @PostMapping("/{voucherId}/delete")
    public String deleteVoucher(@PathVariable Long voucherId, RedirectAttributes redirectAttributes) {
        adminVoucherManagementService.deleteVoucher(voucherId);
        redirectAttributes.addFlashAttribute("successMessage", "Da xoa voucher");
        return "redirect:/admin/vouchers";
    }

    private AdminVoucherRequest createDefaultVoucherForm() {
        AdminVoucherRequest form = new AdminVoucherRequest();
        form.setActive(true);
        form.setStartAt(LocalDateTime.now());
        form.setEndAt(LocalDateTime.now().plusDays(7));
        form.setDiscountType(VoucherDiscountType.PERCENT);
        form.setMinOrderAmount(BigDecimal.ZERO);
        form.setQuantity(100);
        form.setMinTotalSpentAmount(BigDecimal.ZERO);
        form.setMinAccountAgeDays(0);
        form.setAudienceMatchMode(VoucherAudienceMatchMode.ALL);
        form.setAutoDisplayEnabled(false);
        return form;
    }

    private AdminVoucherRequest createVoucherForm(Voucher voucher) {
        AdminVoucherRequest form = createDefaultVoucherForm();
        form.setId(voucher.getId());
        form.setCode(voucher.getCode());
        form.setName(voucher.getName());
        form.setDescription(voucher.getDescription());
        form.setDiscountType(voucher.getDiscountType());
        form.setDiscountValue(voucher.getDiscountValue());
        form.setMaxDiscountAmount(voucher.getMaxDiscountAmount());
        form.setMinOrderAmount(voucher.getMinOrderAmount());
        form.setQuantity(voucher.getQuantity());
        form.setActive(voucher.isActive());
        form.setMinTotalSpentAmount(voucher.getMinTotalSpentAmount());
        form.setMinAccountAgeDays(voucher.getMinAccountAgeDays());
        form.setAudienceMatchMode(voucher.getAudienceMatchMode());
        form.setAutoDisplayEnabled(voucher.isAutoDisplayEnabled());
        form.setStartAt(voucher.getStartAt());
        form.setEndAt(voucher.getEndAt());
        return form;
    }

    private void populateVouchersPage(Model model) {
        model.addAttribute("vouchers", adminVoucherManagementService.getAllVouchers());
    }
}
