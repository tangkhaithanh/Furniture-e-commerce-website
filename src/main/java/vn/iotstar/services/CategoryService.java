package vn.iotstar.services;

import org.springframework.data.domain.Page;
import vn.iotstar.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    // Lấy tất cả danh mục
    List<Category> getAllCategories();

    // Lấy danh mục theo ID
    Optional<Category> getCategoryById(Long id);

    // Lấy danh sách danh mục với phân trang
    Page<Category> getCategoriesPage(int page, int size);

    // Lưu danh mục mới
    Category saveCategory(Category category);

    // Xóa danh mục theo ID
    void deleteCategoryById(Long id);

	Page<Category> getCategories(int pageNumber);
}
