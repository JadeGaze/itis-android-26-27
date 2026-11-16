package com.itis.itisandroid.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * APP COLOR PALETTE (design tokens).
 *
 * Design-system idea: colors are defined ONCE here, and screens use only
 * semantic MaterialTheme.colorScheme roles (primary, surface, onSurface...).
 * No screen should know a concrete hex value, so the whole palette can be
 * changed in a single file.
 *
 * Material 3 colors come in pairs: a color and an "on" color (text/icon
 * drawn on top of it), e.g. primary and onPrimary.
 */

/* ---------- Light theme ---------- */

val PrimaryLight = Color(0xFF1B5FA8)
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFD6E3FF)
val OnPrimaryContainerLight = Color(0xFF001B3D)

val SecondaryLight = Color(0xFF4A6178)
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = Color(0xFFD3E4F8)
val OnSecondaryContainerLight = Color(0xFF051D31)

// Warm accent — used, for example, for the sun and hot temperatures.
val TertiaryLight = Color(0xFF8A5100)
val OnTertiaryLight = Color(0xFFFFFFFF)
val TertiaryContainerLight = Color(0xFFFFDDB8)
val OnTertiaryContainerLight = Color(0xFF2C1700)

val BackgroundLight = Color(0xFFF7F9FC)
val OnBackgroundLight = Color(0xFF191C20)
val SurfaceLight = Color(0xFFFFFFFF)
val OnSurfaceLight = Color(0xFF191C20)
val SurfaceVariantLight = Color(0xFFE0E5EC)
val OnSurfaceVariantLight = Color(0xFF44474F)
val OutlineLight = Color(0xFF74777F)
val ErrorLight = Color(0xFFBA1A1A)
val OnErrorLight = Color(0xFFFFFFFF)
val ErrorContainerLight = Color(0xFFFFDAD6)
val OnErrorContainerLight = Color(0xFF410002)

/* ---------- Dark theme ---------- */

val PrimaryDark = Color(0xFFA9C7FF)
val OnPrimaryDark = Color(0xFF003063)
val PrimaryContainerDark = Color(0xFF00468B)
val OnPrimaryContainerDark = Color(0xFFD6E3FF)

val SecondaryDark = Color(0xFFB7C8DD)
val OnSecondaryDark = Color(0xFF213548)
val SecondaryContainerDark = Color(0xFF35495D)
val OnSecondaryContainerDark = Color(0xFFD3E4F8)

val TertiaryDark = Color(0xFFFFB95C)
val OnTertiaryDark = Color(0xFF4A2800)
val TertiaryContainerDark = Color(0xFF693C00)
val OnTertiaryContainerDark = Color(0xFFFFDDB8)

val BackgroundDark = Color(0xFF111418)
val OnBackgroundDark = Color(0xFFE1E2E8)
val SurfaceDark = Color(0xFF191C20)
val OnSurfaceDark = Color(0xFFE1E2E8)
val SurfaceVariantDark = Color(0xFF44474F)
val OnSurfaceVariantDark = Color(0xFFC4C6D0)
val OutlineDark = Color(0xFF8E9099)
val ErrorDark = Color(0xFFFFB4AB)
val OnErrorDark = Color(0xFF690005)
val ErrorContainerDark = Color(0xFF93000A)
val OnErrorContainerDark = Color(0xFFFFDAD6)
