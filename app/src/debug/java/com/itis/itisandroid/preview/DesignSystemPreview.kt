package com.itis.itisandroid.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.itis.itisandroid.ui.showcase.DesignSystemScreen
import com.itis.itisandroid.ui.theme.ItisAndroidTheme

/**
 * Preview for the design-system showcase screen in both themes.
 */

@Preview(name = "UI Kit – light", group = "Design system", showBackground = true, heightDp = 1600)
@Composable
private fun DesignSystemLightPreview() {
    ItisAndroidTheme {
        DesignSystemScreen()
    }
}

@Preview(name = "UI Kit – dark", group = "Design system", showBackground = true, heightDp = 1600)
@Composable
private fun DesignSystemDarkPreview() {
    ItisAndroidTheme(darkTheme = true) {
        DesignSystemScreen()
    }
}
