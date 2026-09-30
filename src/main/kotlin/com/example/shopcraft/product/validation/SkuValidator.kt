package com.example.shopcraft.product.validation

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class SkuValidator : ConstraintValidator<ValidSku, String?> {

    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        TODO("Step 1 - Return true if value is null, otherwise validate against the SKU regex pattern ^SKU-[A-Z]{3}-\\d{4}$")
    }
}
