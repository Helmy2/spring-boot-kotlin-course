package com.example.shopcraft.product.exception

import com.example.shopcraft.common.exception.ResourceNotFoundException

class ProductNotFoundException(id: Long) : ResourceNotFoundException("Product with id '$id' was not found")
