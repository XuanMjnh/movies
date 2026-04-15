package com.streaming.movieplatform.service;

import com.streaming.movieplatform.dto.AdminVoucherRequest;
import com.streaming.movieplatform.entity.Voucher;

import java.util.List;

public interface AdminVoucherManagementService {
    List<Voucher> getAllVouchers();
    Voucher getVoucherById(Long voucherId);
    Voucher saveVoucher(AdminVoucherRequest request);
    void deleteVoucher(Long voucherId);
}
