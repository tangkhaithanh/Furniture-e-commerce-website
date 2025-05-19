package vn.iotstar.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.iotstar.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Tìm danh mục theo id, sửa kiểu trả về thành Optional<Category>
    @Override
    Optional<Category> findById(Long categoryId);
}
