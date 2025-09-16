package com.app.polow.onboarding.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.polow.onboarding.components.BottomButtons
import com.app.polow.onboarding.components.MultiSelectCard

@Composable
fun DietScreen(
    selectedDiets: List<String>,
    onDietToggle: (String) -> Unit,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    isDietSelected: (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    val diets = remember {
        listOf(
            "none", "Vegetarian", "Vegan", "Pescatarian", "Gluten-free",
            "Dairy-free", "Low-carb / Keto", "Mediterranean", "Intermittent fasting",
            "Halal", "Kosher"
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Do you follow any of the following diets?",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(diets) { diet ->
                MultiSelectCard(
                    title = diet,
                    isSelected = isDietSelected(diet),
                    onClick = {
                        onDietToggle(diet)
                    }
                )
            }

            item {
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    placeholder = { Text("Other: _______") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE91E63),
                        focusedLabelColor = Color(0xFFE91E63)
                    )
                )
            }
        }

        BottomButtons(
            onNext = onNext,
            onSkip = onSkip,
            canProceed = true // Always can proceed for multi-select
        )
    }
}