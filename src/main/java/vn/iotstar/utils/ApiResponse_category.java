package vn.iotstar.utils;

import vn.iotstar.entity.Category;

public class ApiResponse_category {
    private boolean success;
    private String message;
    private Category category;  // Nếu cần trả về sản phẩm vừa thêm

    // Constructor
    public ApiResponse_category(boolean success, String message, Category category2) {
        this.success = success;
        this.message = message;
        this.category = category2;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
