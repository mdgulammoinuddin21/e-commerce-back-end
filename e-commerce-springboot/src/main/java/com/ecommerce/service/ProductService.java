package com.ecommerce.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
	
	public List<Product> getAllProducts(int pageNumber) {
		Pageable pageable = PageRequest.of(pageNumber, 2);
		return (List<Product>) productDao.findAll(pageable);
	}
	
	public void deleteProductDetails(Integer productId) {
		productDao.deleteById(productId);
	}

	public Product getProductDetailsById(Integer productId) {
		// TODO Auto-generated method stub
		return productDao.findById(productId).get();
	}
	
	public List<Product> getProductDetails(boolean isSingleProductCheckout, Integer productId) {
	    if (isSingleProductCheckout) {
	        // We are going to buy a single product
	        List<Product> list = new ArrayList<>();
	        Product product = productDao.findById(productId).get();
	        list.add(product);
	        return list;
	    } else {
	        // We are going to checkout entire cart
	        
	    }
	    return new ArrayList<>();
	}

}
