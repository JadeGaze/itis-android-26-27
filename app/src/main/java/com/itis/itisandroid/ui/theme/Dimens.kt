package com.itis.itisandroid.ui.theme

import androidx.compose.ui.unit.dp

/**
 * SPACING AND SIZES (design tokens).
 *
 * One spacing grid: 4 / 8 / 16 / 24 / 32 dp.
 * Instead of "magic numbers" on every screen use Spacing.*.
 * This makes reviews easier: a stray padding(13.dp) is almost certainly
 * a mistake.
 */
object Spacing {
    /** 4 dp — minimal gap between an icon and text */
    val xs = 4.dp

    /** 8 dp — gap inside a component */
    val sm = 8.dp

    /** 16 dp — standard horizontal screen padding */
    val md = 16.dp

    /** 24 dp — gap between sections */
    val lg = 24.dp

    /** 32 dp — large vertical gap */
    val xl = 32.dp
}

object ComponentSize {
    /** Standard primary button height */
    val buttonHeight = 56.dp

    /** Square icon container in lists */
    val listIconSize = 56.dp

    /** Minimum touch target from the Material guidelines */
    val touchTarget = 48.dp
}
