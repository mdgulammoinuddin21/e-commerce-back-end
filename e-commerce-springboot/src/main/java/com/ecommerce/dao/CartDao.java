package com.ecommerce.dao;

import org.springframework.data.repository.CrudRepository;

import com.ecommerce.entity.Cart;

public interface CartDao extends CrudRepository<Cart, Integer> {

}
