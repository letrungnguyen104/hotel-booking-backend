package com.project.hotel.service;

import com.project.hotel.dto.request.WithdrawRequestDto;
import com.project.hotel.entity.User;
import com.project.hotel.entity.Wallet;
import com.project.hotel.entity.WithdrawalRequest;
import com.project.hotel.enums.WithdrawalStatus;
import com.project.hotel.exception.AppException;
import com.project.hotel.exception.ErrorCode;
import com.project.hotel.repository.UserRepository;
import com.project.hotel.repository.WalletRepository;
import com.project.hotel.repository.WithdrawalRequestRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WalletService {

    WalletRepository walletRepository;
    WithdrawalRequestRepository withdrawalRequestRepository;
    UserRepository userRepository;
    EmailService emailService;

    static final double COMMISSION_RATE = 0.10;

    public void createWallet(User user) {
        if (walletRepository.findByUserId(user.getId()).isEmpty()) {
            Wallet wallet = Wallet.builder().user(user).balance(0.0).build();
            walletRepository.save(wallet);
        }
    }

    @Transactional
    public void creditWallet(User hotelOwner, Double bookingAmount) {
        Wallet wallet = walletRepository.findByUserId(hotelOwner.getId())
                .orElseGet(() -> {
                    Wallet w = Wallet.builder().user(hotelOwner).balance(0.0).build();
                    return walletRepository.save(w);
                });

        double netEarnings = bookingAmount * (1 - COMMISSION_RATE);
        wallet.setBalance(wallet.getBalance() + netEarnings);
        walletRepository.save(wallet);
    }

    @Transactional
    public void requestWithdrawal(Integer userId, WithdrawRequestDto request) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (wallet.getBalance() < request.getAmount()) {
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(wallet.getBalance() - request.getAmount());
        walletRepository.save(wallet);

        WithdrawalRequest withdrawal = WithdrawalRequest.builder()
                .wallet(wallet)
                .amount(request.getAmount())
                .bankName(request.getBankName())
                .bankAccountNumber(request.getBankAccountNumber())
                .accountHolderName(request.getAccountHolderName())
                .status(WithdrawalStatus.PENDING)
                .build();

        withdrawalRequestRepository.save(withdrawal);
    }

    @Transactional
    public void approveWithdrawal(Integer requestId) {
        WithdrawalRequest request = withdrawalRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (request.getStatus() != WithdrawalStatus.PENDING) return;

        request.setStatus(WithdrawalStatus.APPROVED);
        withdrawalRequestRepository.save(request);

        User owner = request.getWallet().getUser();
        String subject = "Withdrawal Request Approved";
        String body = String.format(
                "Hello %s,\n\nYour withdrawal request #%d for %,.0f VND has been APPROVED.\n" +
                        "The funds have been transferred to your bank account.\n\nThank you.",
                owner.getFullName(), request.getId(), request.getAmount()
        );
        emailService.sendSimpleMessage(owner.getEmail(), subject, body);
    }

    @Transactional
    public void rejectWithdrawal(Integer requestId) {
        WithdrawalRequest request = withdrawalRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (request.getStatus() != WithdrawalStatus.PENDING) return;

        request.setStatus(WithdrawalStatus.REJECTED);
        withdrawalRequestRepository.save(request);

        Wallet wallet = request.getWallet();
        wallet.setBalance(wallet.getBalance() + request.getAmount());
        walletRepository.save(wallet);

        User owner = wallet.getUser();
        String subject = "Withdrawal Request Rejected";
        String body = String.format(
                "Hello %s,\n\nYour withdrawal request #%d for %,.0f VND has been REJECTED.\n" +
                        "The amount has been refunded to your wallet balance.\n\nPlease contact admin for more details.",
                owner.getFullName(), request.getId(), request.getAmount()
        );
        emailService.sendSimpleMessage(owner.getEmail(), subject, body);
    }

    public Wallet getMyWallet(Integer userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
    }

    public List<WithdrawalRequest> getWithdrawalHistory(Integer userId) {
        return withdrawalRequestRepository.findByWallet_User_IdOrderByCreatedAtDesc(userId);
    }

    public List<WithdrawalRequest> getPendingRequests() {
        return withdrawalRequestRepository.findByStatusOrderByCreatedAtDesc(WithdrawalStatus.PENDING);
    }
}