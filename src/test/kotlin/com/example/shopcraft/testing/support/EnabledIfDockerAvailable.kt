package com.example.shopcraft.testing.support

import org.junit.jupiter.api.extension.ExtendWith

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@ExtendWith(DockerAvailableCondition::class)
annotation class EnabledIfDockerAvailable
