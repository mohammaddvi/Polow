package com.app.polow.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class OnboardingData(
    var cookingLevel: String = "",
    var diets: List<String> = emptyList(),
    var allergies: List<String> = emptyList(),
    var healthConditions: List<String> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingFlow(onComplete: () -> Unit) {
    val navigationState = remember { OnboardingNavigationState() }
    val onboardingData = remember { OnboardingData() }
    val totalSteps = 4

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        if (navigationState.canNavigateBack()) {
                            IconButton(onClick = {
                                navigationState.clearCurrentScreenData(onboardingData)
                                navigationState.navigateBack()
                            }) {
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
                    .padding(horizontal = 16.dp)
            ) {
                // Progress indicator
                OnboardingProgressIndicator(
                    currentStep = navigationState.getCurrentStep(),
                    totalSteps = totalSteps
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Navigation content
                when (navigationState.currentDestination) {
                    OnboardingDestination.CookingLevel.route -> CookingLevelScreen(
                        selectedLevel = onboardingData.cookingLevel,
                        onLevelSelected = {
                            onboardingData.cookingLevel = it
                            navigationState.navigateNext()
                        },
                        onSkip = { navigationState.navigateNext() }
                    )

                    OnboardingDestination.Diet.route -> DietScreen(
                        selectedDiets = onboardingData.diets,
                        onDietsSelected = {
                            onboardingData.diets = it
                            navigationState.navigateNext()
                        },
                        onSkip = { navigationState.navigateNext() }
                    )

                    OnboardingDestination.Allergies.route -> AllergiesScreen(
                        selectedAllergies = onboardingData.allergies,
                        onAllergiesSelected = {
                            onboardingData.allergies = it
                            navigationState.navigateNext()
                        },
                        onSkip = { navigationState.navigateNext() }
                    )

                    OnboardingDestination.HealthConditions.route -> HealthConditionsScreen(
                        selectedConditions = onboardingData.healthConditions,
                        onConditionsSelected = {
                            onboardingData.healthConditions = it
                            onComplete() // Auto-advance to main screen when selecting health conditions
                        },
                        onComplete = onComplete
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingProgressIndicator(
    currentStep: Int,
    totalSteps: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(totalSteps) { step ->
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = if (step <= currentStep) Color(0xFFE91E63) else Color.Gray.copy(
                            alpha = 0.3f
                        ),
                        shape = CircleShape
                    )
            )
            if (step < totalSteps - 1) {
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(2.dp)
                        .align(Alignment.CenterVertically)
                        .background(
                            color = if (step < currentStep) Color(0xFFE91E63) else Color.Gray.copy(
                                alpha = 0.3f
                            )
                        )
                )
            }
        }
    }
}

@Composable
fun CookingLevelScreen(
    selectedLevel: String,
    onLevelSelected: (String) -> Unit,
    onSkip: () -> Unit
) {
    val cookingLevels = listOf(
        "Beginner" to "Can follow simple instructions.\nLikes to try basic skills, prefers minimal prep.",
        "Novice" to null,
        "Confident Cook" to null,
        "Advanced" to null,
        "Expert / Culinary Artist" to null
    )

    Column {
        Text(
            text = "What's your cooking confidence level?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Column(
            modifier = Modifier.selectableGroup()
        ) {
            cookingLevels.forEach { (level, description) ->
                OnboardingOptionCard(
                    title = level,
                    description = description,
                    isSelected = selectedLevel == level,
                    onClick = { onLevelSelected(level) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        SkipButton(onSkip = onSkip)
    }
}

@Composable
fun DietScreen(
    selectedDiets: List<String>,
    onDietsSelected: (List<String>) -> Unit,
    onSkip: () -> Unit
) {
    val diets = listOf(
        "none", "Vegetarian", "Vegan", "Pescatarian", "Gluten-free",
        "Dairy-free", "Low-carb / Keto", "Mediterranean", "Intermittent fasting",
        "Halal", "Kosher"
    )

    Column {
        Text(
            text = "Do you follow any of the following diets?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(diets) { diet ->
                MultiSelectOptionCard(
                    title = diet,
                    isSelected = selectedDiets.contains(diet),
                    onClick = {
                        if (diet == "none") {
                            onDietsSelected(
                                if (selectedDiets.contains("none")) emptyList() else listOf(
                                    "none"
                                )
                            )
                        } else {
                            val newList = selectedDiets.toMutableList()
                            newList.remove("none") // Remove "none" if selecting specific diets
                            if (selectedDiets.contains(diet)) {
                                newList.remove(diet)
                            } else {
                                newList.add(diet)
                            }
                            onDietsSelected(newList)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    placeholder = { Text("Other: _______") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
        }

        SkipButton(onSkip = onSkip)
    }
}

@Composable
fun AllergiesScreen(
    selectedAllergies: List<String>,
    onAllergiesSelected: (List<String>) -> Unit,
    onSkip: () -> Unit
) {
    val allergies = listOf(
        "none", "Nuts & Seeds", "Dairy", "Seafood", "Grains & Gluten",
        "Eggs", "Fruits & Vegetables", "Legumes", "Food Additives",
        "Animal Products", "Cross-Reactive Foods"
    )

    Column {
        Text(
            text = "Are you allergic or intolerant to any of the following?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(allergies) { allergy ->
                MultiSelectOptionCard(
                    title = allergy,
                    isSelected = selectedAllergies.contains(allergy),
                    onClick = {
                        if (allergy == "none") {
                            onAllergiesSelected(
                                if (selectedAllergies.contains("none")) emptyList() else listOf(
                                    "none"
                                )
                            )
                        } else {
                            val newList = selectedAllergies.toMutableList()
                            newList.remove("none")
                            if (selectedAllergies.contains(allergy)) {
                                newList.remove(allergy)
                            } else {
                                newList.add(allergy)
                            }
                            onAllergiesSelected(newList)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    placeholder = { Text("Other: _______") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
        }

        SkipButton(onSkip = onSkip)
    }
}

@Composable
fun HealthConditionsScreen(
    selectedConditions: List<String>,
    onConditionsSelected: (List<String>) -> Unit,
    onComplete: () -> Unit
) {
    val conditions = listOf(
        "none", "Diabetes", "High blood pressure (Hypertension)", "Heart disease",
        "Celiac disease", "Lactose intolerance", "Kidney disease", "Gout",
        "IBS (Irritable Bowel Syndrome)", "Crohn's disease / Ulcerative colitis",
        "Obesity / Weight management", "PKU (Phenylketonuria)"
    )

    Column {
        Text(
            text = "Do you have any of the following health conditions that affect your diet?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(conditions) { condition ->
                MultiSelectOptionCard(
                    title = condition,
                    isSelected = selectedConditions.contains(condition),
                    onClick = {
                        if (condition == "none") {
                            onConditionsSelected(
                                if (selectedConditions.contains("none")) emptyList() else listOf(
                                    "none"
                                )
                            )
                        } else {
                            val newList = selectedConditions.toMutableList()
                            newList.remove("none")
                            if (selectedConditions.contains(condition)) {
                                newList.remove(condition)
                            } else {
                                newList.add(condition)
                            }
                            onConditionsSelected(newList)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            item {
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    placeholder = { Text("Other: _______") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
        }

        SkipButton(onSkip = onComplete)
    }
}

@Composable
fun SkipButton(onSkip: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(onClick = onSkip) {
            Text("Skip →", color = Color(0xFFE91E63))
        }
    }
}

@Composable
fun OnboardingOptionCard(
    title: String,
    description: String? = null,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.RadioButton
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE91E63) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) Color.White else Color.Black
            )
            description?.let {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    color = if (isSelected) Color.White.copy(alpha = 0.9f) else Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun MultiSelectOptionCard(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.Checkbox
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE91E63) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(16.dp),
            color = if (isSelected) Color.White else Color.Black
        )
    }
}
