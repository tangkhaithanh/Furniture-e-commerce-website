package vn.iotstar.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import vn.iotstar.entity.Category;
import vn.iotstar.services.CategoryService;
import vn.iotstar.services.ProductService;

@Controller
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Hiển thị tất cả danh mục với phân trang
    @GetMapping("/categories")
    public String getCategories(@RequestParam(value = "page", defaultValue = "0") int page,
                                @RequestParam(value = "size", defaultValue = "5") int size, Model model) {
        Page<Category> categoryPage = categoryService.getCategoriesPage(page, size);
        model.addAttribute("categories", categoryPage.getContent());
        model.addAttribute("totalPages", categoryPage.getTotalPages());
        model.addAttribute("currentPage", page);
        return "category";  // Trang hiển thị danh sách danh mục
    }

}
