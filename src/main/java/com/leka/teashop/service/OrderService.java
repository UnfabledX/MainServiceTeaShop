package com.leka.teashop.service;

import com.leka.teashop.model.User;
import com.leka.teashop.model.dto.ProductDto;

public interface OrderService {


    void addToOrder(User currentUser, String quantity, ProductDto productDto);
}
