package vn.iotstar.services.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

@Service
public class BankQRService {
    private static final String MERCHANT_ID = "970436"; // Mã định danh của Vietcombank
    private static final String ACCOUNT_NUMBER = "1022576784";
    private static final String ACCOUNT_NAME = "NGO THANH THANG";

    public String generateQRContent(BigDecimal amount, String orderId) {
        StringBuilder content = new StringBuilder();
        
        // Phiên bản
        content.append("000201");  // Version 01
        
        // Kiểu QR
        content.append("010212");  // Static QR

        // Thông tin merchant
        content.append("26580012").append(MERCHANT_ID); // Định danh ngân hàng thụ hưởng
        content.append("0115").append(ACCOUNT_NUMBER);  // Số tài khoản
        
        // Số tiền
        String amountStr = amount.setScale(0, RoundingMode.HALF_UP).toString();
        content.append("54").append(String.format("%02d", amountStr.length())).append(amountStr);

        // Tiền tệ
        content.append("5303704");  // VND

        // Nội dung chuyển khoản
        String transferContent = "Thanh toan don " + orderId;
        content.append("62").append(String.format("%02d", transferContent.length())).append(transferContent);

        return content.toString();
    }
}