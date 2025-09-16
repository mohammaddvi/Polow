package com.app.polow.onboarding.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.polow.onboarding.components.BottomButtons

@Composable
fun CookingLevelScreen(
    selectedLevel: String,
    onLevelSelected: (String) -> Unit,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    canProceed: Boolean,
    modifier: Modifier = Modifier
) {
    val cookingLevels = remember {
        listOf(
            CookingLevel("Beginner", "Can follow simple instructions, limited knife skills, prefers minimal prep."),
            CookingLevel("Novice", "Comfortable with basic techniques like boiling, sautéing, and baking. Can follow recipes with 5-7 ingredients."),
            CookingLevel("Confident Cook", "Understands timing, seasoning, and multitasking. Can improvise a bit and handle intermediate recipes."),
            CookingLevel("Advanced", "Skilled in techniques like braising, dough-making, and layering flavors. Comfortable with longer prep and complex instructions."),
            CookingLevel("Expert / Culinary Artist", "Can create recipes from scratch, plate beautifully, and use advanced tools. Often cooks intuitively.")
        )
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Question Title
        Text(
            text = "What's your cooking confidence level?",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
        )

        // Cooking Level Options
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cookingLevels) { level ->
                CookingLevelCard(
                    level = level,
                    isSelected = selectedLevel == level.title,
                    onSelected = {
                        onLevelSelected(level.title)
                    }
                )
            }
        }

        // Bottom Buttons
        BottomButtons(
            onNext = onNext,
            onSkip = onSkip,
            canProceed = canProceed
        )
    }
}

@Composable
private fun CookingLevelCard(
    level: CookingLevel,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onSelected()
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (isSelected) {
            BorderStroke(3.dp, Color(0xFFE91E63))
        } else {
            BorderStroke(1.dp, Color.Gray.copy(alpha = 0.2f))
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = level.title,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = if (isSelected) Color(0xFFE91E63) else Color.Black
            )

            // Show description when selected
            if (isSelected) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = level.description,
                    fontSize = 14.sp,
                    color = Color.Gray.copy(alpha = 0.8f),
                    lineHeight = 20.sp
                )
            }
        }
    }
}

data class CookingLevel(
    val title: String,
    val description: String
)