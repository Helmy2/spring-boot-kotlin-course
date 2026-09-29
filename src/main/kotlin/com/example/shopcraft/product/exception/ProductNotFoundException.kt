package com.example.shopcraft.product.exception

class ProductNotFoundException(id: Long) : RuntimeException("Product with id '$id' was not found")
