package io.paku.kmp_template.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kmp_template.app.shared.generated.resources.Res
import kmp_template.app.shared.generated.resources.pretendard_bold
import kmp_template.app.shared.generated.resources.pretendard_extrabold
import kmp_template.app.shared.generated.resources.pretendard_regular
import org.jetbrains.compose.resources.Font

@Composable
fun PretendardTypography(): Typography {
    val pretendard = FontFamily(
        Font(
            resource = Res.font.pretendard_regular,
            weight = FontWeight.Normal)
        ,
        Font(
            resource = Res.font.pretendard_bold,
            weight = FontWeight.Bold
        ),
        Font(
            resource =   Res.font.pretendard_extrabold,
            weight = FontWeight.ExtraBold
        ),
    )

    return Typography(
        headlineSmall = TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            fontFamily = pretendard
        ),
        titleLarge = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 18.sp,
            fontFamily = pretendard
        ),
        bodyLarge = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            fontFamily = pretendard
        ),
        bodyMedium = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            fontFamily = pretendard
        ),
        labelMedium = TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            fontFamily = pretendard
        ),
    )
}