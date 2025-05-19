package vn.iotstar.dto;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Category;
import vn.iotstar.services.CategoryService;

@Service
public class CategoryDTOService {
	@Autowired
	CategoryService categoryService;

	public List<CategoryDTO_2> getAllCategories() 
	{
        // Lấy tất cả các danh mục từ CategoryService
        List<Category> categories = categoryService. getAllCategories();

        // Chuyển đổi từ Category entity sang CategoryDTO_2
        return categories.stream()
                         .map(category -> {
                             CategoryDTO_2 categoryDTO = new CategoryDTO_2();
                             categoryDTO.setId(category.getId());
                             categoryDTO.setName(category.getName());
                             return categoryDTO;
                         })
                         .collect(Collectors.toList());
    }
}
