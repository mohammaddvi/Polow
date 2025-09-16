package com.app.polow.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class OnboardingViewModel {

    // Current step state
    var currentStep by mutableStateOf(0)
        private set

    // Onboarding data state
    var cookingLevel by mutableStateOf("")
        private set

    var selectedDiets by mutableStateOf<List<String>>(emptyList())
        private set

    var selectedAllergies by mutableStateOf<List<String>>(emptyList())
        private set

    var selectedHealthConditions by mutableStateOf<List<String>>(emptyList())
        private set

    val totalSteps = 4

    // Navigation methods
    fun nextStep() {
        if (currentStep < totalSteps - 1) {
            currentStep++
        }
    }

    fun previousStep() {
        if (currentStep > 0) {
            // Clear current step data when going back
            when (currentStep) {
                1 -> selectedDiets = emptyList()
                2 -> selectedAllergies = emptyList()
                3 -> selectedHealthConditions = emptyList()
            }
            currentStep--
        }
    }

    fun canNavigateBack(): Boolean = currentStep > 0

    // Cooking level methods
    fun selectCookingLevel(level: String) {
        cookingLevel = level
    }

    fun canProceedFromCookingLevel(): Boolean = cookingLevel.isNotEmpty()

    // Diet methods
    fun toggleDiet(diet: String) {
        val currentList = selectedDiets.toMutableList()

        if (diet == "none") {
            selectedDiets = if (selectedDiets.contains("none")) {
                emptyList()
            } else {
                listOf("none")
            }
        } else {
            // Remove "none" if selecting specific diets
            currentList.remove("none")

            if (selectedDiets.contains(diet)) {
                currentList.remove(diet)
            } else {
                currentList.add(diet)
            }
            selectedDiets = currentList
        }
    }

    fun isDietSelected(diet: String): Boolean = selectedDiets.contains(diet)

    // Allergies methods
    fun toggleAllergy(allergy: String) {
        val currentList = selectedAllergies.toMutableList()

        if (allergy == "none") {
            selectedAllergies = if (selectedAllergies.contains("none")) {
                emptyList()
            } else {
                listOf("none")
            }
        } else {
            // Remove "none" if selecting specific allergies
            currentList.remove("none")

            if (selectedAllergies.contains(allergy)) {
                currentList.remove(allergy)
            } else {
                currentList.add(allergy)
            }
            selectedAllergies = currentList
        }
    }

    fun isAllergySelected(allergy: String): Boolean = selectedAllergies.contains(allergy)

    // Health conditions methods
    fun toggleHealthCondition(condition: String) {
        val currentList = selectedHealthConditions.toMutableList()

        if (condition == "none") {
            selectedHealthConditions = if (selectedHealthConditions.contains("none")) {
                emptyList()
            } else {
                listOf("none")
            }
        } else {
            // Remove "none" if selecting specific conditions
            currentList.remove("none")

            if (selectedHealthConditions.contains(condition)) {
                currentList.remove(condition)
            } else {
                currentList.add(condition)
            }
            selectedHealthConditions = currentList
        }
    }

    fun isHealthConditionSelected(condition: String): Boolean = selectedHealthConditions.contains(condition)

    // Get current step data for debugging/logging
    fun getCurrentStepData(): String {
        return when (currentStep) {
            0 -> "Cooking Level: $cookingLevel"
            1 -> "Diets: $selectedDiets"
            2 -> "Allergies: $selectedAllergies"
            3 -> "Health Conditions: $selectedHealthConditions"
            else -> "Unknown step"
        }
    }

    // Get all data for final submission
    fun getAllData(): OnboardingData {
        return OnboardingData(
            cookingLevel = cookingLevel,
            diets = selectedDiets,
            allergies = selectedAllergies,
            healthConditions = selectedHealthConditions
        )
    }

    // Reset all data (useful for testing or if user wants to restart)
    fun reset() {
        currentStep = 0
        cookingLevel = ""
        selectedDiets = emptyList()
        selectedAllergies = emptyList()
        selectedHealthConditions = emptyList()
    }
}