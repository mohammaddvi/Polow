package com.app.polow.onboarding

data class CookingLevel(
    val title: String,
    val description: String
)

data class OnboardingData(
    var cookingLevel: String = "",
    var diets: List<String> = emptyList(),
    var allergies: List<String> = emptyList(),
    var healthConditions: List<String> = emptyList()
)