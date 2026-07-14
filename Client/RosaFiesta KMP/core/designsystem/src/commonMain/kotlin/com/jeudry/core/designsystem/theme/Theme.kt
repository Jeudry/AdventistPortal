package com.adventistportal.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }

val ColorScheme.extended: ExtendedColors
    @ReadOnlyComposable
    @Composable
    get() = LocalExtendedColors.current

@Immutable
data class ExtendedColors(
    // Button states
    val primaryHover: Color,
    val destructiveHover: Color,
    val destructiveSecondaryOutline: Color,
    val disabledOutline: Color,
    val disabledFill: Color,
    val successOutline: Color,
    val success: Color,
    val onSuccess: Color,
    val secondaryFill: Color,

    // Text variants
    val textPrimary: Color,
    val textTertiary: Color,
    val textSecondary: Color,
    val textPlaceholder: Color,
    val textDisabled: Color,

    // Surface variants
    val surfaceLower: Color,
    val surfaceHigher: Color,
    val surfaceOutline: Color,
    val overlay: Color,

    // Accent colors
    val accentBlue: Color,
    val accentPurple: Color,
    val accentViolet: Color,
    val accentPink: Color,
    val accentOrange: Color,
    val accentYellow: Color,
    val accentGreen: Color,
    val accentTeal: Color,
    val accentLightBlue: Color,
    val accentGrey: Color,

    // Cake colors for chat bubbles
    val cakeViolet: Color,
    val cakeGreen: Color,
    val cakeBlue: Color,
    val cakePink: Color,
    val cakeOrange: Color,
    val cakeYellow: Color,
    val cakeTeal: Color,
    val cakePurple: Color,
    val cakeRed: Color,
    val cakeMint: Color,
)

val LightExtendedColors = ExtendedColors(
    primaryHover = AdventistPortalBrand600,
    destructiveHover = AdventistPortalRed600,
    destructiveSecondaryOutline = AdventistPortalRed200,
    disabledOutline = AdventistPortalBase200,
    disabledFill = AdventistPortalBase150,
    successOutline = AdventistPortalBrand100,
    success = AdventistPortalBrand600,
    onSuccess = AdventistPortalBase0,
    secondaryFill = AdventistPortalBase100,

    textPrimary = AdventistPortalBase1000,
    textTertiary = AdventistPortalBase800,
    textSecondary = AdventistPortalBase900,
    textPlaceholder = AdventistPortalBase700,
    textDisabled = AdventistPortalBase400,

    surfaceLower = AdventistPortalBase100,
    surfaceHigher = AdventistPortalBase100,
    surfaceOutline = AdventistPortalBase1000Alpha14,
    overlay = AdventistPortalBase1000Alpha80,

    accentBlue = AdventistPortalBlue,
    accentPurple = AdventistPortalPurple,
    accentViolet = AdventistPortalViolet,
    accentPink = AdventistPortalPink,
    accentOrange = AdventistPortalOrange,
    accentYellow = AdventistPortalYellow,
    accentGreen = AdventistPortalGreen,
    accentTeal = AdventistPortalTeal,
    accentLightBlue = AdventistPortalLightBlue,
    accentGrey = AdventistPortalGrey,

    cakeViolet = AdventistPortalCakeLightViolet,
    cakeGreen = AdventistPortalCakeLightGreen,
    cakeBlue = AdventistPortalCakeLightBlue,
    cakePink = AdventistPortalCakeLightPink,
    cakeOrange = AdventistPortalCakeLightOrange,
    cakeYellow = AdventistPortalCakeLightYellow,
    cakeTeal = AdventistPortalCakeLightTeal,
    cakePurple = AdventistPortalCakeLightPurple,
    cakeRed = AdventistPortalCakeLightRed,
    cakeMint = AdventistPortalCakeLightMint,
)

val DarkExtendedColors = ExtendedColors(
    primaryHover = AdventistPortalBrand600,
    destructiveHover = AdventistPortalRed600,
    destructiveSecondaryOutline = AdventistPortalRed200,
    disabledOutline = AdventistPortalBase900,
    disabledFill = AdventistPortalBase1000,
    successOutline = AdventistPortalBrand500Alpha40,
    success = AdventistPortalBrand500,
    onSuccess = AdventistPortalBase1000,
    secondaryFill = AdventistPortalBase900,

    textPrimary = AdventistPortalBase0,
    textTertiary = AdventistPortalBase200,
    textSecondary = AdventistPortalBase150,
    textPlaceholder = AdventistPortalBase400,
    textDisabled = AdventistPortalBase500,

    surfaceLower = AdventistPortalBase1000,
    surfaceHigher = AdventistPortalBase900,
    surfaceOutline = AdventistPortalBase100Alpha10Alt,
    overlay = AdventistPortalBase1000Alpha80,

    accentBlue = AdventistPortalBlue,
    accentPurple = AdventistPortalPurple,
    accentViolet = AdventistPortalViolet,
    accentPink = AdventistPortalPink,
    accentOrange = AdventistPortalOrange,
    accentYellow = AdventistPortalYellow,
    accentGreen = AdventistPortalGreen,
    accentTeal = AdventistPortalTeal,
    accentLightBlue = AdventistPortalLightBlue,
    accentGrey = AdventistPortalGrey,

    cakeViolet = AdventistPortalCakeDarkViolet,
    cakeGreen = AdventistPortalCakeDarkGreen,
    cakeBlue = AdventistPortalCakeDarkBlue,
    cakePink = AdventistPortalCakeDarkPink,
    cakeOrange = AdventistPortalCakeDarkOrange,
    cakeYellow = AdventistPortalCakeDarkYellow,
    cakeTeal = AdventistPortalCakeDarkTeal,
    cakePurple = AdventistPortalCakeDarkPurple,
    cakeRed = AdventistPortalCakeDarkRed,
    cakeMint = AdventistPortalCakeDarkMint,
)

val LightColorScheme = lightColorScheme(
    primary = AdventistPortalBrand500,
    onPrimary = AdventistPortalBrand1000,
    primaryContainer = AdventistPortalBrand100,
    onPrimaryContainer = AdventistPortalBrand900,

    secondary = AdventistPortalBase700,
    onSecondary = AdventistPortalBase0,
    secondaryContainer = AdventistPortalBase100,
    onSecondaryContainer = AdventistPortalBase900,

    tertiary = AdventistPortalBrand900,
    onTertiary = AdventistPortalBase0,
    tertiaryContainer = AdventistPortalBrand100,
    onTertiaryContainer = AdventistPortalBrand1000,

    error = AdventistPortalRed500,
    onError = AdventistPortalBase0,
    errorContainer = AdventistPortalRed200,
    onErrorContainer = AdventistPortalRed600,

    background = AdventistPortalBrand1000,
    onBackground = AdventistPortalBase0,
    surface = AdventistPortalBase0,
    onSurface = AdventistPortalBase1000,
    surfaceVariant = AdventistPortalBase100,
    onSurfaceVariant = AdventistPortalBase900,

    outline = AdventistPortalBase1000Alpha8,
    outlineVariant = AdventistPortalBase200,
)

val DarkColorScheme = darkColorScheme(
    primary = AdventistPortalBrand500,
    onPrimary = AdventistPortalBrand1000,
    primaryContainer = AdventistPortalBrand900,
    onPrimaryContainer = AdventistPortalBrand500,

    secondary = AdventistPortalBase400,
    onSecondary = AdventistPortalBase1000,
    secondaryContainer = AdventistPortalBase900,
    onSecondaryContainer = AdventistPortalBase150,

    tertiary = AdventistPortalBrand500,
    onTertiary = AdventistPortalBase1000,
    tertiaryContainer = AdventistPortalBrand900,
    onTertiaryContainer = AdventistPortalBrand500,

    error = AdventistPortalRed500,
    onError = AdventistPortalBase0,
    errorContainer = AdventistPortalRed600,
    onErrorContainer = AdventistPortalRed200,

    background = AdventistPortalBase1000,
    onBackground = AdventistPortalBase0,
    surface = AdventistPortalBase950,
    onSurface = AdventistPortalBase0,
    surfaceVariant = AdventistPortalBase900,
    onSurfaceVariant = AdventistPortalBase150,

    outline = AdventistPortalBase100Alpha10,
    outlineVariant = AdventistPortalBase800,
)