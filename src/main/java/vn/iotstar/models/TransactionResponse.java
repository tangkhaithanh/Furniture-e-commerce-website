package vn.iotstar.models;

import lombok.Data;

@Data
public class TransactionResponse {
    private boolean success;
    private String status;
    private double amount;
    private String transactionId;
    private String message;
}
