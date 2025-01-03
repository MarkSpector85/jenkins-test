package com.tarento.payment.service;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.tarento.payment.dto.PaymentRequestDTO;
import com.tarento.payment.dto.PaymentResponseDTO;
import com.tarento.payment.dto.PaymentStatus;

import jakarta.annotation.PostConstruct;

@Service
@Slf4j
public class PaymentService {

	private Map<Integer, Double> paymentMap;

	@PostConstruct
	private void init() {
		paymentMap = new HashMap<>();

		paymentMap.put(1, 500d);
		paymentMap.put(2, 1000d);
		paymentMap.put(3, 700d);
	}

	/**
	 * Processes a debit payment request. Deducts the requested amount from the user's balance
	 * if sufficient funds are available; otherwise, rejects the payment.
	 *
	 * @param requestDTO the payment request containing userId, orderId, and amount
	 * @return a {@link PaymentResponseDTO} containing the orderId, userId, amount,
	 * and payment status (approved or rejected)
	 */
	public PaymentResponseDTO debit(PaymentRequestDTO requestDTO) {
		try {
			validatePaymentRequest(requestDTO);

			double balance = paymentMap.getOrDefault(requestDTO.getUserId(), 0d);

			PaymentResponseDTO responseDTO = new PaymentResponseDTO();
			responseDTO.setOrderId(requestDTO.getOrderId());
			responseDTO.setUserId(requestDTO.getUserId());
			responseDTO.setAmount(requestDTO.getAmount());
			responseDTO.setStatus(PaymentStatus.PAYMENT_REJECTED);
			log.info("Processing debit for User ID: {}, Current Balance: {}, Requested Amount: {}",
					requestDTO.getUserId(), balance, requestDTO.getAmount());
			if (balance >= requestDTO.getAmount()) {
				responseDTO.setStatus(PaymentStatus.PAYMENT_APPROVED);
				paymentMap.put(requestDTO.getUserId(), balance - requestDTO.getAmount());
				log.info("Debit successful. Updated Balance: {}", paymentMap.get(requestDTO.getUserId()));
			} else {
				log.warn("Insufficient balance for User ID: {}", requestDTO.getUserId());
			}
			return responseDTO;
		} catch (IllegalArgumentException ex) {
			log.error("Payment validation failed {}", ex.getMessage());
			throw ex;
		} catch (Exception ex) {
			log.error("Error processing debit payment for User ID: {}: {}", requestDTO.getUserId(), ex.getMessage());
			throw new RuntimeException("Error processing debit payment. Please try again later.");
		}
	}

	/**
	 * Processes a credit payment request. Adds the specified amount to the user's balance.
	 *
	 * @param requestDTO the payment request containing userId and amount
	 */
	public void credit(PaymentRequestDTO requestDTO) {
		try {
			validatePaymentRequest(requestDTO);
			log.info("Processing credit for User ID: {}, Amount: {}", requestDTO.getUserId(), requestDTO.getAmount());
			if (paymentMap.containsKey(requestDTO.getUserId())) {
				paymentMap.computeIfPresent(requestDTO.getUserId(), (k, v) -> v + requestDTO.getAmount());
				log.info("Credit successful. Updated Balance: {}", paymentMap.get(requestDTO.getUserId()));
			} else {
				log.warn("User ID: {} not found. Credit operation skipped.", requestDTO.getUserId());
			}
		} catch (IllegalArgumentException ex) {
			log.error("Payment validation failed: {}", ex.getMessage());
			throw ex;
		} catch (Exception ex) {
			log.error("Error processing credit payment for User ID: {}: {}", requestDTO.getUserId(), ex.getMessage());
			throw new RuntimeException("Error processing credit payment. Please try again later.");
		}
	}

	/**
	 * Validates a {@link PaymentRequestDTO}.
	 * <p>
	 * Ensures that the request is not null and contains all required fields, such as userId and amount.
	 *
	 * @param requestDTO the payment request to validate
	 * @throws IllegalArgumentException if the request is null or missing required fields
	 */
	public static void validatePaymentRequest(PaymentRequestDTO requestDTO) {
		try {
			if (requestDTO == null) {
				log.error("Payment request is null.");
				throw new IllegalArgumentException("Payment request cannot be null.");
			}

			if (requestDTO.getUserId() == null || requestDTO.getAmount() == null) {
				log.error("Invalid payment request: Missing required fields. User ID and amount are mandatory.");
				throw new IllegalArgumentException("User ID and amount are required.");
			}

			if (requestDTO.getAmount() <= 0) {
				log.error("Invalid payment request: Amount must be greater than zero.");
				throw new IllegalArgumentException("Amount must be greater than zero.");
			}

			log.info("Payment request validated successfully for User ID: {}", requestDTO.getUserId());
		} catch (IllegalArgumentException ex) {
			log.error("Validation failed for PaymentRequestDTO: {}", ex.getMessage());
			throw ex;
		} catch (Exception ex) {
			log.error("Unexpected error during validation of PaymentRequestDTO: {}", ex.getMessage());
			throw new RuntimeException("Unexpected error during payment validation.");
		}
	}
}
