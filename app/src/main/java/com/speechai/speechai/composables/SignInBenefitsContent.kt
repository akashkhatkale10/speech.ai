package com.speechai.speechai.composables

import android.app.Activity.RESULT_OK
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.identity.Identity
import com.speechai.speechai.CustomTextStyle
import com.speechai.speechai.R
import com.speechai.speechai.auth.AuthViewModel
import com.speechai.speechai.auth.GoogleAuthUiClient
import com.speechai.speechai.auth.LoginState
import com.speechai.speechai.data.models.UserData
import com.speechai.speechai.screens.history.HistoryUtils.signInBenefits
import com.speechai.speechai.subtitleTextColor
import com.speechai.speechai.tertiaryColor
import com.speechai.speechai.whiteColor
import kotlinx.coroutines.launch

@Composable
fun SignInBenefitsContent(
    modifier: Modifier = Modifier,
    onResult: (state: LoginState?) -> Unit = {},
) {
    val width = LocalConfiguration.current.screenWidthDp
    val authViewModel = hiltViewModel<AuthViewModel>()
    val context = LocalContext.current
    val pager = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val googleAuthUiClient by remember {
        mutableStateOf(
            GoogleAuthUiClient(
                context = context,
                oneTapClient = Identity.getSignInClient(context)
            )
        )
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if(result.resultCode == RESULT_OK) {
                scope.launch {
                    val signInResult = googleAuthUiClient.signInWithIntent(
                        intent = result.data ?: return@launch
                    )
                    authViewModel.loginUser(signInResult)
                }
            } else {
                authViewModel.loadingState(false)
            }
        }
    )
    val state by authViewModel.loginState.collectAsState()

    LaunchedEffect(state) {
        onResult(state)
    }


    Column(
        modifier = modifier
    ) {
        Text(
            "why sign in ?",
            style = CustomTextStyle.copy(
                color = whiteColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier
                .padding(horizontal = 20.dp)
        )

        LazyRow(
            state = pager,
            contentPadding = PaddingValues(horizontal = 20.dp,),
            modifier = Modifier
                .padding(top = 30.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            flingBehavior = rememberSnapFlingBehavior(
                lazyListState = pager
            )
        ) {
            itemsIndexed(signInBenefits) { index, item ->
                Box(
                    modifier = Modifier
                        .width(
                            (width * 0.8f).dp
                        )
                        .background(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .border(
                            width = 1.dp,
                            shape = RoundedCornerShape(16.dp),
                            color = tertiaryColor
                        )
                        .padding(
                            horizontal = 20.dp,
                            vertical = 20.dp
                        )
                ) {
                    Text(
                        text = item,
                        style = CustomTextStyle.copy(
                            fontSize = 14.sp,
                        )
                    )
                }
            }
        }

        CustomButton(
            text = "sign in",
            isLoading = state.isLoading,
            icon = Icons.Default.ArrowForward,
            onClick = {
                scope.launch {
                    authViewModel.loadingState()
                    val signInIntentSender = googleAuthUiClient.signIn()
                    launcher.launch(
                        IntentSenderRequest.Builder(
                            signInIntentSender ?: return@launch
                        ).build()
                    )
                }
            },
            modifier = Modifier
                .padding(top = 30.dp)
                .padding(horizontal = 28.dp),
            startComposable = {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = whiteColor,
                            shape = CircleShape
                        )
                        .padding(4.dp)
                ) {
                    Image(
                        painterResource(R.drawable.google),
                        contentDescription = null,
                    )
                }
            }
        )
        Text(
            text = "we’re committed to keeping your data safe",
            style = CustomTextStyle.copy(
                color = subtitleTextColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .padding(top = 30.dp)
                .fillMaxWidth()
        )
    }
}