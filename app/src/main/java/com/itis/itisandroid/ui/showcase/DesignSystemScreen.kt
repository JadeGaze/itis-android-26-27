package com.itis.itisandroid.ui.showcase

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.itis.itisandroid.R
import com.itis.itisandroid.ui.components.AppCard
import com.itis.itisandroid.ui.components.AppTopBar
import com.itis.itisandroid.ui.components.DangerButton
import com.itis.itisandroid.ui.components.ErrorView
import com.itis.itisandroid.ui.components.LoadingView
import com.itis.itisandroid.ui.components.PrimaryButton
import com.itis.itisandroid.ui.components.SecondaryButton
import com.itis.itisandroid.ui.components.SectionHeader
import com.itis.itisandroid.ui.components.StatTile
import com.itis.itisandroid.ui.theme.Spacing

/**
 * DESIGN-SYSTEM SHOWCASE SCREEN.
 *
 * This is not an end-user screen but a teaching one: it shows every theme
 * token (colors, typography, spacing) and every shared component.
 * It is convenient during a lesson: change a value in ui/theme and the
 * result is immediately visible on this page.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesignSystemScreen() {
    Scaffold(
        topBar = {
            AppTopBar(title = stringResource(R.string.design_title))
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(
                start = Spacing.md,
                end = Spacing.md,
                top = Spacing.sm,
                bottom = Spacing.lg,
            ),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            item(key = "intro") {
                Text(
                    text = stringResource(R.string.design_intro),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            /* ---------- Colors ---------- */
            item(key = "colors_header") {
                SectionHeader(title = stringResource(R.string.design_section_colors))
            }
            item(key = "colors") {
                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    ColorSwatch(
                        label = stringResource(R.string.design_color_primary),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f),
                    )
                    ColorSwatch(
                        label = stringResource(R.string.design_color_secondary),
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f),
                    )
                    ColorSwatch(
                        label = stringResource(R.string.design_color_tertiary),
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f),
                    )
                    ColorSwatch(
                        label = stringResource(R.string.design_color_surface),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.weight(1f),
                    )
                    ColorSwatch(
                        label = stringResource(R.string.design_color_error),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            /* ---------- Typography ---------- */
            item(key = "type_header") {
                SectionHeader(title = stringResource(R.string.design_section_typography))
            }
            item(key = "type") {
                AppCard {
                    TypographySample("displaySmall", MaterialTheme.typography.displaySmall)
                    TypographySample("headlineMedium", MaterialTheme.typography.headlineMedium)
                    TypographySample("titleLarge", MaterialTheme.typography.titleLarge)
                    TypographySample("titleMedium", MaterialTheme.typography.titleMedium)
                    TypographySample("bodyLarge", MaterialTheme.typography.bodyLarge)
                    TypographySample("bodyMedium", MaterialTheme.typography.bodyMedium)
                    TypographySample("labelLarge", MaterialTheme.typography.labelLarge)
                    TypographySample("labelSmall", MaterialTheme.typography.labelSmall)
                }
            }

            /* ---------- Buttons ---------- */
            item(key = "buttons_header") {
                SectionHeader(title = stringResource(R.string.design_section_buttons))
            }
            item(key = "buttons") {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    PrimaryButton(
                        text = "Primary",
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.Add,
                    )
                    SecondaryButton(
                        text = "Secondary",
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                    )
                    DangerButton(
                        text = "Danger",
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Default.Delete,
                    )
                    PrimaryButton(
                        text = "Disabled",
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false,
                    )
                }
            }

            /* ---------- Cards and metrics ---------- */
            item(key = "cards_header") {
                SectionHeader(title = stringResource(R.string.design_section_cards))
            }
            item(key = "cards") {
                AppCard {
                    Text(
                        text = "AppCard",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Text(
                        text = "Cards, sections and stat tiles share the same paddings, shapes and colors.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(Spacing.md))
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                        StatTile(
                            label = stringResource(R.string.design_sample_stat_label),
                            value = stringResource(R.string.design_sample_stat_value),
                            modifier = Modifier.weight(1f),
                        )
                        StatTile(
                            label = "Wind",
                            value = "3.4 m/s",
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            /* ---------- States ---------- */
            item(key = "states_header") {
                SectionHeader(title = stringResource(R.string.design_section_states))
            }
            item(key = "loading") {
                AppCard(contentPadding = PaddingValues(Spacing.md)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                    ) {
                        LoadingView()
                    }
                }
            }
            item(key = "error") {
                AppCard(contentPadding = PaddingValues(Spacing.md)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                    ) {
                        ErrorView(
                            message = "This is how a failed network request looks.",
                            onRetry = {},
                        )
                    }
                }
            }

            /* ---------- Spacing ---------- */
            item(key = "spacing_header") {
                SectionHeader(title = stringResource(R.string.design_section_spacing))
            }
            item(key = "spacing") {
                AppCard {
                    SpacingRow("xs", Spacing.xs.value)
                    SpacingRow("sm", Spacing.sm.value)
                    SpacingRow("md", Spacing.md.value)
                    SpacingRow("lg", Spacing.lg.value)
                    SpacingRow("xl", Spacing.xl.value)
                }
            }
        }
    }
}

@Composable
private fun ColorSwatch(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color),
        )
        Spacer(modifier = Modifier.height(Spacing.xs))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun TypographySample(name: String, style: TextStyle) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Aa",
            style = style,
            modifier = Modifier.width(72.dp),
        )
        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "The quick brown fox",
                style = style,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun SpacingRow(name: String, value: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.width(40.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Box(
            modifier = Modifier
                .width(value.dp)
                .height(12.dp)
                .clip(MaterialTheme.shapes.extraSmall)
                .background(MaterialTheme.colorScheme.primary),
        )
        Spacer(modifier = Modifier.width(Spacing.sm))
        Text(
            text = "${value.toInt()} dp",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
