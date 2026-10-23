package com.lukeneedham.languagetransfer.ui.feature.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lukeneedham.common.ui.DebugButton
import com.lukeneedham.languagetransfer.R
import com.lukeneedham.languagetransfer.ui.theme.Colors
import com.lukeneedham.languagetransfer.ui.util.SystemBarsColor

/**
 * The settings page. The app has no settings of its own yet, so it only holds the debug button,
 * which is hidden in release builds.
 *
 * @param onBack Callback to be invoked when the close button is clicked
 * @param onOpenDebug Callback to be invoked when the Debug button is clicked
 */
@Composable
fun SettingsPage(
    onBack: () -> Unit,
    onOpenDebug: () -> Unit,
) {
    SystemBarsColor(useDarkIcons = true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Colors.background)
            .systemBarsPadding()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .clickable { onBack() }
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = "Close",
                    modifier = Modifier.size(30.dp)
                )
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Settings", fontSize = 25.sp, color = Color.Black)
            }
            Box(modifier = Modifier.size(50.dp))
        }
        Spacer(modifier = Modifier.height(20.dp))

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            DebugButton(onClick = onOpenDebug)
        }
    }
}
