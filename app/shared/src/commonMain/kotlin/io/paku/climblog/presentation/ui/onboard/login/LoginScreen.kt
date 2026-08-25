package io.paku.climblog.presentation.ui.onboard.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.paku.climblog.business.domain.model.SocialLoginType
import io.paku.climblog.presentation.theme.AppTheme
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun LoginRoute(
    viewModel: LoginViewModel = koinViewModel(),
    navigateToRegister: (SocialLoginType) -> Unit
) {
    val state by viewModel.state

    LaunchedEffect(Unit) {
        viewModel.action.collectLatest {
            when (it) {
                is LoginViewModelAction.NavigateToRegister -> navigateToRegister(it.socialLoginType)
            }
        }
    }

    LoginScreen(
        state = state,
        onLoginClick = {
            viewModel.onEvent(LoginViewModelEvent.OnSocialLoginClick(it))
        }
    )
}

@Composable
private fun LoginScreen(
    state: LoginViewModelState,
    onLoginClick: (SocialLoginType) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "ClimbLog",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = "Record your climbing journey",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 64.dp)
        )

        SocialLoginButton(
            text = "Continue with Google",
            containerColor = Color.White,
            contentColor = Color.Black,
            onClick = { onLoginClick(SocialLoginType.GOOGLE) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        SocialLoginButton(
            text = "Continue with Kakao",
            containerColor = Color(0xFFFEE500),
            contentColor = Color.Black,
            onClick = { onLoginClick(SocialLoginType.KAKAO) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        SocialLoginButton(
            text = "Continue with Naver",
            containerColor = Color(0xFF03C75A),
            contentColor = Color.White,
            onClick = { onLoginClick(SocialLoginType.NAVER) }
        )

        if (state.loginError != null) {
            Text(
                text = state.loginError,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
private fun SocialLoginButton(
    text: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
    ) {
        Text(text = text, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    AppTheme {
        LoginScreen(
            state = LoginViewModelState()
        )
    }
}