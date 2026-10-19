package pl.grupa06.unidzieci.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val bazowa = Typography()

val Typography = Typography(
    headlineSmall = bazowa.headlineSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = (-0.2).sp),
    titleLarge = bazowa.titleLarge.copy(fontWeight = FontWeight.SemiBold),
    titleMedium = bazowa.titleMedium.copy(fontWeight = FontWeight.SemiBold),
    titleSmall = bazowa.titleSmall.copy(fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.2.sp),
    labelSmall = bazowa.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp),
)
