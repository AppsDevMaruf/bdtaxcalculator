package com.maruf.bdtaxcalculator.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.Velocity

@Composable
internal fun rememberKeyboardDismissOnScrollConnection(): NestedScrollConnection {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    return remember(focusManager, keyboardController) {
        object : NestedScrollConnection {
            override suspend fun onPreFling(available: Velocity): Velocity {
                focusManager.clearFocus()
                keyboardController?.hide()
                return Velocity.Zero
            }
        }
    }
}
