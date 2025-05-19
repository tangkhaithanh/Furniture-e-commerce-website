package vn.iotstar.controllers.admin;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.mail.Multipart;

import vn.iotstar.dto.ProductDTO;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;
import vn.iotstar.services.CategoryService;
import vn.iotstar.services.ProductService;


@Controller
@RequestMapping("/admin")
public class AdminProductController {

	@Value("${product.image.upload.dir}")
    private String uploadDir;
	
	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;
	
	// Hiển thị sản phẩm đã duyệt
    @GetMapping("/products/approved")
    public String approvedProducts(@RequestParam(value = "page", defaultValue = "0") int page, Model model) {
        Page<Product> approvedProductsPage = productService.getApprovedProducts(page);
        model.addAttribute("approvedProducts", approvedProductsPage.getContent());
        model.addAttribute("totalApprovedPages", approvedProductsPage.getTotalPages());
        model.addAttribute("currentApprovedPage", page);
        return "admin/product/index_approved";
    }
    
    // Hiển thị sản phẩm chưa duyệt
    @GetMapping("/products/unapproved")
    public String unapprovedProducts(@RequestParam(value = "page", defaultValue = "0") int page, Model model) {
        Page<Product> unapprovedProductsPage = productService.getUnapprovedProducts(page);
        model.addAttribute("unapprovedProducts", unapprovedProductsPage.getContent());
        model.addAttribute("totalUnapprovedPages", unapprovedProductsPage.getTotalPages());
        model.addAttribute("currentUnapprovedPage", page);
        return "admin/product/index_unapproved";
    }
    
    // Duyệt sản phẩm
    @GetMapping("/approve-product/{id}")
    public String approveProduct(@PathVariable Long id) {
        Product product= productService.getProductById(id);
        
        product.setProductStatus(1); // Đặt trạng thái đã duyệt
        productService.save(product);
       
        return "redirect:/admin/products/unapproved";
    }
    
    // Xóa sản phẩm
    @GetMapping("/delete-product/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/admin/products/approved";
    }
	
//	@RequestMapping("/add-product")
//	public String add(Model model) {
//
//		
//		return "admin/product/add";
//	}
//
//	@PostMapping("/add-product")
//	public String save(@ModelAttribute("product") Product product, @RequestParam("fileImage") MultipartFile file) {
//
//		
//		return "admin/product/add";
//	}
//	
//	@GetMapping("/edit-product/{id}")
//	public String edit(Model model, @PathVariable("id") Long id) {
//	
//	    return "admin/product/edit";  // Trả về view chỉnh sửa danh mục
//	}
//
//	@PostMapping("/edit-product/{id}")
//	public String update(@ModelAttribute("product") Product product, @RequestParam("fileImage") MultipartFile file) {
//		
//		// lưu vào database
//		try {
//			
//			return "redirect:/admin/product";
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//		return "admin/product/add";
//	}
	
	

}
