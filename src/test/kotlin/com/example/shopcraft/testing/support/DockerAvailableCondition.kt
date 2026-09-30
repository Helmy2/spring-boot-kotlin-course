package com.example.shopcraft.testing.support

import org.junit.jupiter.api.extension.ConditionEvaluationResult
import org.junit.jupiter.api.extension.ExecutionCondition
import org.junit.jupiter.api.extension.ExtensionContext
import org.testcontainers.DockerClientFactory

class DockerAvailableCondition : ExecutionCondition {

    override fun evaluateExecutionCondition(context: ExtensionContext): ConditionEvaluationResult {
        return if (isDockerAvailable()) {
            ConditionEvaluationResult.enabled("Docker environment is available; executing Testcontainers test")
        } else {
            ConditionEvaluationResult.disabled("Docker environment is not available; skipping Testcontainers test")
        }
    }

    private fun isDockerAvailable(): Boolean {
        return try {
            DockerClientFactory.instance().isDockerAvailable
        } catch (_: Throwable) {
            false
        }
    }
}
