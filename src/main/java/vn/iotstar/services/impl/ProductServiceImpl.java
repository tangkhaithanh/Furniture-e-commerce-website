package vn.iotstar.services.impl;

import org.springframework.data.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iotstar.dto.ProductDTO;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.Review;
import vn.iotstar.repository.CategoryRepository;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.services.ProductService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // Lấy sản phẩm theo danh mục, status và phân trang
    @Override
    public Page<ProductDTO> getProductsByCategory(Long categoryId, int status, Pageable pageable) {
        // Lấy dữ liệu từ repository với averageRating tính toán từ cơ sở dữ liệu
        Page<Object[]> results = productRepository.findByCategoryIdAndStatusWithAvgRating(categoryId, status, pageable);

        // Chuyển đổi kết quả từ Object[] thành Page<ProductDTO>
        Page<ProductDTO> productDTOPage = results.map(result -> {
            Product product = (Product) result[0];  // Lấy đối tượng Product
            Double averageRating = (Double) result[1];  // Lấy giá trị averageRating
            Long totalSold = (Long) result[2];  // Giả sử tổng bán được là Long

            // Tạo DTO và gán giá trị
            return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getStatus(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getImageUrl(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getCreatedAt(),
                averageRating != null ? Math.round(averageRating * 10.0) / 10.0 : 0, // Làm tròn averageRating
                totalSold.intValue()
            );
        });

        // Trả về Page<ProductDTO>
        return productDTOPage;
    }

    // Lấy tất cả các danh mục
    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // Phân trang sản phẩm, giới hạn 20 sản phẩm mỗi trang
    @Override
    public Page<Product> getProducts(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 6);  // 5 sản phẩm mỗi trang
        return productRepository.findAll(pageable);
    }
    
    @Override
    // Lấy sản phẩm theo ID
    public Product getProductById(Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        return product.orElse(null);  // Trả về sản phẩm nếu tìm thấy, nếu không trả về null
    }

	@Override
	public Product save(Product product) {
		// TODO Auto-generated method stub
		 return productRepository.save(product);
	}

	@Override
	public Product updateProduct(Long productId, Product productDetails) {
		// TODO Auto-generated method stub
		  Product existingProduct = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
	        
	        // Cập nhật thông tin sản phẩm
	        existingProduct.setName(productDetails.getName());
	        existingProduct.setPrice(productDetails.getPrice());
	        existingProduct.setQuantity(productDetails.getQuantity());
	        existingProduct.setStatus(productDetails.getStatus());
	        existingProduct.setImageUrl(productDetails.getImageUrl());
	        existingProduct.setCategory(productDetails.getCategory());

	        // Lưu lại thông tin đã cập nhật
	        return productRepository.save(existingProduct);
	}

	@Override
	public void deleteProduct(Long productId)
	{
		 Product product = productRepository.findById(productId)
			        .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

			    // Xóa sản phẩm khỏi cơ sở dữ liệu
			   productRepository.delete(product);
	}
	
	@Override
    public List<Product> getBestSellingProducts() {
        return productRepository.findBestSellingProducts();
    }
	
	@Override
	public Page<ProductDTO> searchProductsByCategory(Long categoryId, String keyword, int status, Pageable pageable) {
	    // Lấy dữ liệu từ repository với averageRating tính toán từ cơ sở dữ liệu
	    Page<Object[]> results = productRepository.findByCategoryIdAndStatusWithAvgRatingAndKeyword(categoryId, keyword, status, pageable);

	    // Chuyển đổi kết quả từ Object[] thành Page<ProductDTO>
	    Page<ProductDTO> productDTOPage = results.map(result -> {
	        Product product = (Product) result[0];  // Lấy đối tượng Product
	        Double averageRating = (Double) result[1];  // Lấy giá trị averageRating
	        Long totalSold = (Long) result[2];  // Giả sử tổng bán được là Long

	        // Tạo DTO và gán giá trị
	        return new ProductDTO(
	            product.getId(),
	            product.getName(),
	            product.getStatus(),
	            product.getDescription(),
	            product.getPrice(),
	            product.getQuantity(),
	            product.getImageUrl(),
	            product.getCategory().getId(),
	            product.getCategory().getName(),
	            product.getCreatedAt(),
	            averageRating != null ? Math.round(averageRating * 10.0) / 10.0 : 0, // Làm tròn averageRating
	            totalSold != null ? totalSold.intValue() : 0 // Chuyển totalSold thành int, xử lý nếu null
	        );
	    });

	    // Trả về Page<ProductDTO>
	    return productDTOPage;
	}
	
	@Override
	public Page<Product> getApprovedProducts(int pageNum) {
		Pageable pageable = PageRequest.of(pageNum, 5);  // 5 sản phẩm mỗi trang
        return productRepository.findAllByProductStatus(1, pageable);
	}

	@Override
	public Page<Product> getUnapprovedProducts(int pageNum) {
		Pageable pageable = PageRequest.of(pageNum, 5);  // 5 sản phẩm mỗi trang
		return productRepository.findAllByProductStatus(0, pageable);
	}

	@Override
	public Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable) {
		return productRepository.findByNameContainingIgnoreCase(name, pageable);
	}

	@Override
	public Page<Product> findByIdContaining(Long id, Pageable pageable) {
		return productRepository.findById(id, pageable);
	}

	@Override
	public Page<Product> findByProductId(Long id, Pageable pageable) 
	{
		Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            List<Product> productList = new ArrayList<>();
            productList.add(product.get());
            return new PageImpl<>(productList, pageable, 1);
        }
        return new PageImpl<>(new ArrayList<>(), pageable, 0);
	}

	
    
}