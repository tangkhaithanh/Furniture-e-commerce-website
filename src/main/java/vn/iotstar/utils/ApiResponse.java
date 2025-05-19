package vn.iotstar.utils;

import vn.iotstar.entity.Product;

public class ApiResponse {
    private boolean success;
    private String message;
    private Product product;  // Nếu cần trả về sản phẩm vừa thêm

    // Constructor
    public ApiResponse(boolean success, String message, Product product) {
        this.success = success;
        this.message = message;
        this.product = product;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
