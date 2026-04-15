package com.streaming.movieplatform.service.impl;

import com.streaming.movieplatform.dto.AdminVoucherRequest;
import com.streaming.movieplatform.entity.Voucher;
import com.streaming.movieplatform.enums.VoucherAudienceMatchMode;
import com.streaming.movieplatform.exception.BusinessException;
import com.streaming.movieplatform.exception.ResourceNotFoundException;
import com.streaming.movieplatform.repository.VoucherRepository;
import com.streaming.movieplatform.service.AdminVoucherManagementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class AdminVoucherManagementServiceImpl implements AdminVoucherManagementService {

    private final VoucherRepository voucherRepository;

    public AdminVoucherManagementServiceImpl(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Voucher> getAllVouchers() {
        return voucherRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Voucher getVoucherById(Long voucherId) {
        return voucherRepository.findById(voucherId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay voucher"));
    }

    @Override
    public Voucher saveVoucher(AdminVoucherRequest request) {
        validateVoucherRequest(request);

        String normalizedCode = request.getCode().trim().toUpperCase(Locale.ROOT);
        voucherRepository.findByCodeIgnoreCase(normalizedCode)
                .filter(existing -> request.getId() == null || !existing.getId().equals(request.getId()))
                .ifPresent(existing -> {
                    throw new BusinessException("Ma voucher da ton tai");
                });

        Voucher voucher = request.getId() == null ? new Voucher() : getVoucherById(request.getId());
        BigDecimal minTotalSpentAmount = defaultAmount(request.getMinTotalSpentAmount());
        Integer minAccountAgeDays = defaultDays(request.getMinAccountAgeDays());

        voucher.setCode(normalizedCode);
        voucher.setName(request.getName().trim());
        voucher.setDescription(StringUtils.hasText(request.getDescription()) ? request.getDescription().trim() : null);
        voucher.setDiscountType(request.getDiscountType());
        voucher.setDiscountValue(request.getDiscountValue());
        voucher.setMaxDiscountAmount(request.getMaxDiscountAmount());
        voucher.setMinOrderAmount(request.getMinOrderAmount());
        voucher.setQuantity(request.getQuantity());
        voucher.setActive(request.isActive());
        voucher.setMinTotalSpentAmount(minTotalSpentAmount);
        voucher.setMinAccountAgeDays(minAccountAgeDays);
        voucher.setAudienceMatchMode(resolveAudienceMatchMode(request));
        voucher.setAutoDisplayEnabled(request.isAutoDisplayEnabled());
        voucher.setStartAt(request.getStartAt());
        voucher.setEndAt(request.getEndAt());
        if (voucher.getUsedCount() == null) {
            voucher.setUsedCount(0);
        }
        if (voucher.getUsedCount() > voucher.getQuantity()) {
            throw new BusinessException("So luong moi khong duoc nho hon so luot da dung");
        }
        return voucherRepository.save(voucher);
    }

    @Override
    public void deleteVoucher(Long voucherId) {
        voucherRepository.deleteById(voucherId);
    }

    private void validateVoucherRequest(AdminVoucherRequest request) {
        if (request.getEndAt().isBefore(request.getStartAt()) || request.getEndAt().isEqual(request.getStartAt())) {
            throw new BusinessException("Thoi gian ket thuc phai sau thoi gian bat dau");
        }
        if (request.getDiscountType() == null) {
            throw new BusinessException("Loai giam gia khong hop le");
        }
        if (request.getDiscountValue() == null || request.getDiscountValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Gia tri giam phai lon hon 0");
        }
        if (request.getDiscountType().name().equals("PERCENT")
                && request.getDiscountValue().compareTo(new BigDecimal("100")) > 0) {
            throw new BusinessException("Voucher theo phan tram khong duoc lon hon 100");
        }
        if (defaultAmount(request.getMinTotalSpentAmount()).compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Tong chi tieu toi thieu khong duoc am");
        }
        if (defaultDays(request.getMinAccountAgeDays()) < 0) {
            throw new BusinessException("Tuoi tai khoan toi thieu khong duoc am");
        }
    }

    private BigDecimal defaultAmount(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private Integer defaultDays(Integer value) {
        return value == null ? 0 : value;
    }

    private VoucherAudienceMatchMode resolveAudienceMatchMode(AdminVoucherRequest request) {
        return request.getAudienceMatchMode() == null
                ? VoucherAudienceMatchMode.ALL
                : request.getAudienceMatchMode();
    }
}
