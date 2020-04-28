package com.checc.gateway.service;

import com.checc.gateway.entities.Payment;

public interface PaymentService {
    public int create(Payment payment);
    public Payment getPaymentById(Long id);
}
