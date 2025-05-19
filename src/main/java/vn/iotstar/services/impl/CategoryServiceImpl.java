package vn.iotstar.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;
import vn.iotstar.repository.CategoryRepository;
import vn.iotstar.services.CategoryService;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // Lấy tất cả danh mục
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Lấy danh mục theo ID
    @Override
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);  // Tự động trả về Optional<Category>
    }

    // Lấy danh sách danh mục với phân trang
    @Override
    public Page<Category> getCategoriesPage(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return categoryRepository.findAll(pageRequest);
    }

    // Phân trang sản phẩm, giới hạn 20 sản phẩm mỗi trang
    @Override
    public Page<Category> getCategories(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 5);  // 5 sản phẩm mỗi trang
        return categoryRepository.findAll(pageable);
    }
    
    // Lưu danh mục mới
    @Override
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    // Xóa danh mục theo ID
    @Override
    public void deleteCategoryById(Long id) {
        categoryRepository.deleteById(id);
    }
}
