package com.app.polow.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

sealed class OnboardingDestination(val route: String) {
    object CookingLevel : OnboardingDestination("cooking_level")
    object Diet : OnboardingDestination("diet")
    object Allergies : OnboardingDestination("allergies")
    object HealthConditions : OnboardingDestination("health_conditions")
}

// Simple Navigation State Manager (since we can't use Navigation Compose in shared module)
class OnboardingNavigationState {
    private var _currentDestination by mutableStateOf(OnboardingDestination.CookingLevel.route)
    val currentDestination: String get() = _currentDestination

    private val destinations = listOf(
        OnboardingDestination.CookingLevel.route,
        OnboardingDestination.Diet.route,
        OnboardingDestination.Allergies.route,
        OnboardingDestination.HealthConditions.route
    )

    fun navigate(destination: String) {
        _currentDestination = destination
    }

    fun navigateNext() {
        val currentIndex = destinations.indexOf(_currentDestination)
        if (currentIndex < destinations.size - 1) {
            _currentDestination = destinations[currentIndex + 1]
        }
    }

    fun navigateBack() {
        val currentIndex = destinations.indexOf(_currentDestination)
        if (currentIndex > 0) {
            _currentDestination = destinations[currentIndex - 1]
        }
    }

    fun clearCurrentScreenData(onboardingData: OnboardingData) {
        when (_currentDestination) {
            OnboardingDestination.CookingLevel.route -> onboardingData.cookingLevel = ""
            OnboardingDestination.Diet.route -> onboardingData.diets = emptyList()
            OnboardingDestination.Allergies.route -> onboardingData.allergies = emptyList()
            OnboardingDestination.HealthConditions.route -> onboardingData.healthConditions =
                emptyList()
        }
    }

    fun getCurrentStep(): Int {
        return destinations.indexOf(_currentDestination)
    }

    fun canNavigateBack(): Boolean {
        return getCurrentStep() > 0
    }
}
