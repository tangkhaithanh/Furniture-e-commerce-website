package vn.iotstar.controllers.admin;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import vn.iotstar.dto.ProductDTO_2;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;
import vn.iotstar.repository.CategoryRepository;
import vn.iotstar.services.CategoryService;
import vn.iotstar.utils.ApiResponse;
import vn.iotstar.utils.ApiResponse_category;

@Controller

@RequestMapping("/admin")
public class AdminCategoryController {
	@Value("${product.image.upload.dir}")
    private String uploadDir;
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
    private CategoryRepository categoryRepository;
	
	@GetMapping
    public String Vendorpage()
    {
    	return "VendorPage";
    }
	
	@GetMapping("/admin_categories")
	public String index(@RequestParam(value = "page", defaultValue = "0") int page, Model model){
		Page<Category> categoryPage = categoryService.getCategories(page);
        List<Category> categories = categoryPage.getContent();
        model.addAttribute("categories", categories);
        model.addAttribute("totalPages", categoryPage.getTotalPages());
        model.addAttribute("currentPage", page);
		return "admin/category/index";
	}
	
	@GetMapping("/add-category")
	public String add(Model model) {
		
		Category category = new Category();
		category.setStatus(true); // set giá trị mặt định của category status là true 
		model.addAttribute("category", category);
		return "admin/category/add";
	}
	
	@PostMapping("/add-category")
	public ResponseEntity<?> addCategory(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("status") Boolean status,
            @RequestParam("images") MultipartFile imageFile) 
    		
    {
        try 
        {
            // Kiểm tra và tạo thư mục nếu không tồn tại
            File uploadDirectory = new File(uploadDir);
            if (!uploadDirectory.exists()) {
                uploadDirectory.mkdirs();  // Tạo thư mục nếu chưa tồn tại
            }

            // Tạo tên file duy nhất
            String uniqueFileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, uniqueFileName);

            // Lưu ảnh vào thư mục
            imageFile.transferTo(filePath.toFile());  // Lưu file vào thư mục
            
            // Lưu thông tin sản phẩm vào cơ sở dữ liệu
            Category category = new Category();
            category.setName(name);
            
            category.setDescription(description);
            category.setStatus(status);

            // Lưu URL ảnh (tương đối) vào DB, ví dụ: /images/abc.jpg
            category.setImages("/images/" + uniqueFileName);

            categoryService.saveCategory(category);

            return ResponseEntity.ok(new ApiResponse_category(true, "Danh mục đã được thêm thành công!", category));

        } 
        catch (IOException e) 
        {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi khi lưu sản phẩm: " + e.getMessage());
        }
    }
	
	@GetMapping("/edit-category/{id}")
	public String getCategoryById(@PathVariable String id, Model model) 
    {
    	Long categoryLongId = Long.parseLong(id);
        Optional<Category> cate = categoryService.getCategoryById(categoryLongId);
        
        if (cate.isPresent()) {
            Category category = cate.get(); // Lấy đối tượng từ Optional 
            model.addAttribute("category", category);
			  
			return "admin/category/edit";
        }
        return "admin/category/index";
    }
	
	@PostMapping("/edit-category/{id}")
	public String updateCategory(
	        @PathVariable("id") Long id,
	        @RequestParam("name") String name,
	        @RequestParam("status") boolean status,
	        @RequestParam(value = "image", required = false) MultipartFile imageFile,
	        @RequestParam("description") String description) {
	    try {
	        // Tìm danh mục
	        Category category = categoryRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("Category not found"));

	        // Cập nhật thông tin danh mục
	        category.setName(name);
	        category.setStatus(status);
	        category.setDescription(description);

	        if (imageFile != null && !imageFile.isEmpty()) {
	            String uniqueFileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
	            Path filePath = Paths.get(uploadDir, uniqueFileName);
	            imageFile.transferTo(filePath.toFile());
	            category.setImages("/images/" + uniqueFileName);
	        }

	        categoryService.saveCategory(category);
	        return "redirect:/admin/admin_categories";
	        //return ResponseEntity.ok(new ApiResponse_category(true, "Danh mục đã được cập nhật thành công!", category));
	    } catch (IOException e) {
	        return "admin/category/add";
	    }
	}
	
	@GetMapping("/delete-category/{id}")
	public String delete(@PathVariable("id") Long id) {
		try {
			this.categoryService.deleteCategoryById(id);
			return "redirect:/admin/admin_categories";
		} catch(Exception e) {
			e.printStackTrace();
		}
		return "redirect:/admin/admin_categories";
	}
	
}
