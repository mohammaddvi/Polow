package com.app.polow.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.polow.onboarding.screens.AllergiesScreen
import com.app.polow.onboarding.screens.DietScreen
import com.app.polow.onboarding.screens.CookingLevelScreen
import com.app.polow.onboarding.screens.HealthConditionsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingFlow(
    onComplete: (OnboardingData) -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = remember { OnboardingViewModel() }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    if (viewModel.canNavigateBack()) {
                        IconButton(onClick = { viewModel.previousStep() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFE4E1)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFE4E1))
                .padding(paddingValues)
        ) {
            // Welcome Header (only on first screen)
            if (viewModel.currentStep == 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Welcome to Polow",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE91E63)
                    )
                }
            }

            // Progress Indicator
            ProgressIndicator(
                currentStep = viewModel.currentStep,
                totalSteps = viewModel.totalSteps,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )

            // Content based on current step
            when (viewModel.currentStep) {
                0 -> CookingLevelScreen(
                    selectedLevel = viewModel.cookingLevel,
                    onLevelSelected = viewModel::selectCookingLevel,
                    onNext = viewModel::nextStep,
                    onSkip = viewModel::nextStep,
                    canProceed = viewModel.canProceedFromCookingLevel()
                )
                1 -> DietScreen(
                    selectedDiets = viewModel.selectedDiets,
                    onDietToggle = viewModel::toggleDiet,
                    onNext = viewModel::nextStep,
                    onSkip = viewModel::nextStep,
                    isDietSelected = viewModel::isDietSelected
                )
                2 -> AllergiesScreen(
                    selectedAllergies = viewModel.selectedAllergies,
                    onAllergyToggle = viewModel::toggleAllergy,
                    onNext = viewModel::nextStep,
                    onSkip = viewModel::nextStep,
                    isAllergySelected = viewModel::isAllergySelected
                )
                3 -> HealthConditionsScreen(
                    selectedConditions = viewModel.selectedHealthConditions,
                    onConditionToggle = viewModel::toggleHealthCondition,
                    onNext = {
                        onComplete(viewModel.getAllData())
                    },
                    onSkip = {
                        onComplete(viewModel.getAllData())
                    },
                    isConditionSelected = viewModel::isHealthConditionSelected
                )
            }
        }
    }
}

@Composable
private fun ProgressIndicator(
    currentStep: Int,
    totalSteps: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalSteps) { step ->
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = if (step <= currentStep) Color(0xFFE91E63) else Color.Gray.copy(alpha = 0.3f),
                        shape = CircleShape
                    )
            )
            if (step < totalSteps - 1) {
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(2.dp)
                        .background(
                            color = if (step < currentStep) Color(0xFFE91E63) else Color.Gray.copy(alpha = 0.3f)
                        )
                )
            }
        }
    }
}