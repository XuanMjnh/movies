package com.streaming.movieplatform.controller.admin;

import com.streaming.movieplatform.controller.support.FormFlowSupport;
import com.streaming.movieplatform.dto.AdminBannerRequest;
import com.streaming.movieplatform.dto.AdminGenreRequest;
import com.streaming.movieplatform.dto.AdminPlanRequest;
import com.streaming.movieplatform.dto.AdminUserUpdateRequest;
import com.streaming.movieplatform.dto.AdminVoucherRequest;
import com.streaming.movieplatform.entity.Banner;
import com.streaming.movieplatform.entity.Genre;
import com.streaming.movieplatform.entity.SubscriptionPlan;
import com.streaming.movieplatform.entity.User;
import com.streaming.movieplatform.entity.Voucher;
import com.streaming.movieplatform.exception.BusinessException;
import com.streaming.movieplatform.repository.MovieRepository;
import com.streaming.movieplatform.repository.RoleRepository;
import com.streaming.movieplatform.service.AdminService;
import com.streaming.movieplatform.service.CommentService;
import com.streaming.movieplatform.service.SubscriptionService;
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
import java.time.LocalDateTime;

import com.streaming.movieplatform.enums.VoucherAudienceMatchMode;
import com.streaming.movieplatform.enums.VoucherDiscountType;

@Controller
@RequestMapping("/admin")
public class AdminManagementController {

    private final AdminService adminService;
    private final RoleRepository roleRepository;
    private final MovieRepository movieRepository;
    private final WalletService walletService;
    private final SubscriptionService subscriptionService;
    private final CommentService commentService;

    public AdminManagementController(AdminService adminService,
                                     RoleRepository roleRepository,
                                     MovieRepository movieRepository,
                                     WalletService walletService,
                                     SubscriptionService subscriptionService,
                                     CommentService commentService) {
        this.adminService = adminService;
        this.roleRepository = roleRepository;
        this.movieRepository = movieRepository;
        this.walletService = walletService;
        this.subscriptionService = subscriptionService;
        this.commentService = commentService;
    }

    @GetMapping("/users")
    public String users(Model model) {
        populateUsersPage(model);
        FormFlowSupport.addIfAbsent(model, "userForm", AdminUserUpdateRequest::new);
        return "admin/users";
    }

    @GetMapping("/users/{userId}/edit")
    public String editUser(@PathVariable Long userId, Model model) {
        populateUsersPage(model);
        model.addAttribute("userForm", createUserForm(adminService.getUserById(userId)));
        return "admin/users";
    }

    @PostMapping("/users/save")
    public String saveUser(@Valid @ModelAttribute("userForm") AdminUserUpdateRequest request,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateUsersPage(model);
            return "admin/users";
        }
        adminService.updateUser(request);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật người dùng thành công");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{userId}/wallet-adjust")
    public String adjustWallet(@PathVariable Long userId,
                               @RequestParam BigDecimal amount,
                               @RequestParam String action,
                               @RequestParam(required = false) String note,
                               RedirectAttributes redirectAttributes) {
        try {
            walletService.adjustBalanceByAdmin(userId, amount, "ADD".equalsIgnoreCase(action), note);
            redirectAttributes.addFlashAttribute("successMessage", "Điều chỉnh số dư thành công");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/genres")
    public String genres(Model model) {
        populateGenresPage(model);
        FormFlowSupport.addIfAbsent(model, "genreForm", AdminGenreRequest::new);
        return "admin/genres";
    }

    @GetMapping("/genres/{genreId}/edit")
    public String editGenre(@PathVariable Long genreId, Model model) {
        populateGenresPage(model);
        model.addAttribute("genreForm", createGenreForm(adminService.getGenreById(genreId)));
        return "admin/genres";
    }

    @PostMapping("/genres/save")
    public String saveGenre(@Valid @ModelAttribute("genreForm") AdminGenreRequest request,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateGenresPage(model);
            return "admin/genres";
        }
        adminService.saveGenre(request);
        redirectAttributes.addFlashAttribute("successMessage", "Lưu thể loại thành công");
        return "redirect:/admin/genres";
    }

    @PostMapping("/genres/{genreId}/delete")
    public String deleteGenre(@PathVariable Long genreId, RedirectAttributes redirectAttributes) {
        adminService.deleteGenre(genreId);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa thể loại");
        return "redirect:/admin/genres";
    }

    @GetMapping("/plans")
    public String plans(Model model) {
        populatePlansPage(model);
        FormFlowSupport.addIfAbsent(model, "planForm", AdminPlanRequest::new);
        return "admin/plans";
    }

    @GetMapping("/plans/{planId}/edit")
    public String editPlan(@PathVariable Long planId, Model model) {
        populatePlansPage(model);
        model.addAttribute("planForm", createPlanForm(adminService.getPlanById(planId)));
        return "admin/plans";
    }

    @PostMapping("/plans/save")
    public String savePlan(@Valid @ModelAttribute("planForm") AdminPlanRequest request,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populatePlansPage(model);
            return "admin/plans";
        }
        adminService.savePlan(request);
        redirectAttributes.addFlashAttribute("successMessage", "Lưu gói thành viên thành công");
        return "redirect:/admin/plans";
    }

    @PostMapping("/plans/{planId}/delete")
    public String deletePlan(@PathVariable Long planId, RedirectAttributes redirectAttributes) {
        adminService.deletePlan(planId);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa gói thành viên");
        return "redirect:/admin/plans";
    }

    @GetMapping("/vouchers")
    public String vouchers(Model model) {
        populateVouchersPage(model);
        FormFlowSupport.addIfAbsent(model, "voucherForm", this::createDefaultVoucherForm);
        return "admin/vouchers";
    }

    @GetMapping("/vouchers/{voucherId}/edit")
    public String editVoucher(@PathVariable Long voucherId, Model model) {
        populateVouchersPage(model);
        model.addAttribute("voucherForm", createVoucherForm(adminService.getVoucherById(voucherId)));
        return "admin/vouchers";
    }

    @PostMapping("/vouchers/save")
    public String saveVoucher(@Valid @ModelAttribute("voucherForm") AdminVoucherRequest request,
                              BindingResult bindingResult,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateVouchersPage(model);
            return "admin/vouchers";
        }
        try {
            adminService.saveVoucher(request);
            redirectAttributes.addFlashAttribute("successMessage", "Lưu voucher thành công");
            return "redirect:/admin/vouchers";
        } catch (BusinessException ex) {
            populateVouchersPage(model);
            model.addAttribute("errorMessage", ex.getMessage());
            return "admin/vouchers";
        }
    }

    @PostMapping("/vouchers/{voucherId}/delete")
    public String deleteVoucher(@PathVariable Long voucherId, RedirectAttributes redirectAttributes) {
        adminService.deleteVoucher(voucherId);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa voucher");
        return "redirect:/admin/vouchers";
    }

    @GetMapping("/banners")
    public String banners(Model model) {
        populateBannersPage(model);
        FormFlowSupport.addIfAbsent(model, "bannerForm", AdminBannerRequest::new);
        return "admin/banners";
    }

    @GetMapping("/banners/{bannerId}/edit")
    public String editBanner(@PathVariable Long bannerId, Model model) {
        populateBannersPage(model);
        model.addAttribute("bannerForm", createBannerForm(adminService.getBannerById(bannerId)));
        return "admin/banners";
    }

    @PostMapping("/banners/save")
    public String saveBanner(@Valid @ModelAttribute("bannerForm") AdminBannerRequest request,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateBannersPage(model);
            return "admin/banners";
        }
        adminService.saveBanner(request);
        redirectAttributes.addFlashAttribute("successMessage", "Lưu banner thành công");
        return "redirect:/admin/banners";
    }

    @PostMapping("/banners/{bannerId}/delete")
    public String deleteBanner(@PathVariable Long bannerId, RedirectAttributes redirectAttributes) {
        adminService.deleteBanner(bannerId);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa banner");
        return "redirect:/admin/banners";
    }

    @GetMapping("/transactions")
    public String transactions(Model model) {
        model.addAttribute("walletTransactions", walletService.getLatestTransactions());
        model.addAttribute("paymentTransactions", walletService.getPaymentTransactions());
        model.addAttribute("subscriptionHistory", subscriptionService.getLatestSubscriptions());
        return "admin/transactions";
    }

    @GetMapping("/comments")
    public String comments(Model model) {
        model.addAttribute("comments", commentService.getAllComments());
        return "admin/comments";
    }

    @PostMapping("/comments/{commentId}/toggle")
    public String toggleComment(@PathVariable Long commentId, RedirectAttributes redirectAttributes) {
        commentService.toggleHidden(commentId);
        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật trạng thái bình luận");
        return "redirect:/admin/comments";
    }

    @PostMapping("/comments/{commentId}/delete")
    public String deleteComment(@PathVariable Long commentId, RedirectAttributes redirectAttributes) {
        commentService.deleteComment(commentId);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa bình luận");
        return "redirect:/admin/comments";
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

    private AdminGenreRequest createGenreForm(Genre genre) {
        AdminGenreRequest form = new AdminGenreRequest();
        form.setId(genre.getId());
        form.setName(genre.getName());
        form.setDescription(genre.getDescription());
        return form;
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

    private void populateUsersPage(Model model) {
        model.addAttribute("users", adminService.getAllUsers());
        model.addAttribute("roles", roleRepository.findAll());
    }

    private void populateGenresPage(Model model) {
        model.addAttribute("genres", adminService.getAllGenres());
    }

    private void populatePlansPage(Model model) {
        model.addAttribute("plans", adminService.getAllPlans());
    }

    private void populateVouchersPage(Model model) {
        model.addAttribute("vouchers", adminService.getAllVouchers());
    }

    private void populateBannersPage(Model model) {
        model.addAttribute("banners", adminService.getAllBanners());
        model.addAttribute("movies", movieRepository.findAll());
    }
}
