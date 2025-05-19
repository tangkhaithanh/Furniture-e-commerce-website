package vn.iotstar.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Product;
import vn.iotstar.repository.ProductRepository;
@Service
public class ProductDTOService {
	@Autowired
	ProductRepository productRepository;
	public ProductDTO_2 getProductById(Long productId) {
	    Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
	    // Chuyển đổi từ Entity sang DTO
	    ProductDTO_2 productDTO = new ProductDTO_2();
	    productDTO.setId(product.getId());
	    productDTO.setName(product.getName());
	    productDTO.setPrice(product.getPrice());
	    productDTO.setQuantity(product.getQuantity());
	    productDTO.setImageUrl(product.getImageUrl());
	    productDTO.setCategoryName(product.getCategory().getName());
	    productDTO.setCategoryId(product.getCategory().getId());
	    productDTO.setStatus(product.getStatus());
	    productDTO.setDescription(product.getDescription());
	    return productDTO;
	}
}