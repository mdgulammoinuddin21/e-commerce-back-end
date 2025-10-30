package com.ecommerce.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.dao.ProductDao;
import com.ecommerce.entity.Product;

@Service
public class ProductService {
	
	@Autowired
	private ProductDao productDao;
	
	public Product addNewProduct(Product product) {
		return productDao.save(product);
	}
	
	public List<Product> getAllProducts() {
		return (List<Product>) productDao.findAll();
	}
	
	public void deleteProductDetails(Integer productId) {
		productDao.deleteById(productId);
	}

	public Product getProductDetailsById(Integer productId) {
		// TODO Auto-generated method stub
		return productDao.findById(productId).get();
	}
}
