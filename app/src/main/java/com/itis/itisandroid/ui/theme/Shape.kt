package com.itis.itisandroid.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * COMPONENT SHAPES (design tokens).
 *
 * Every corner radius in the app comes from MaterialTheme.shapes:
 *  - extraSmall — small chips;
 *  - small — input fields, icons;
 *  - medium — buttons and cards;
 *  - large — bigger cards and modal sheets;
 *  - extraLarge — large surfaces (the current-weather card).
 */
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp),
)
