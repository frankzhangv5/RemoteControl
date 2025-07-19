package zhang.feng.remotecontrol.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.AngleDown
import compose.icons.fontawesomeicons.solid.AngleLeft
import compose.icons.fontawesomeicons.solid.AngleRight
import compose.icons.fontawesomeicons.solid.AngleUp
import compose.icons.fontawesomeicons.solid.ArrowRight
import compose.icons.fontawesomeicons.solid.Bars
import compose.icons.fontawesomeicons.solid.Home
import compose.icons.fontawesomeicons.solid.PowerOff
import compose.icons.fontawesomeicons.solid.Tv
import compose.icons.fontawesomeicons.solid.VolumeDown
import compose.icons.fontawesomeicons.solid.VolumeMute
import compose.icons.fontawesomeicons.solid.VolumeUp
import zhang.feng.remotecontrol.R
import zhang.feng.remotecontrol.RemoteKeys
import zhang.feng.remotecontrol.RemoteUiState
import zhang.feng.remotecontrol.RemoteViewModel

object RemoteButtonColors {
    val background: Color @Composable get() = MaterialTheme.colorScheme.primaryContainer
    val foreground: Color @Composable get() = MaterialTheme.colorScheme.onPrimaryContainer
    val border: Color @Composable get() = MaterialTheme.colorScheme.outline
    val text: Color @Composable get() = MaterialTheme.colorScheme.onPrimaryContainer
    val accentRed: Color @Composable get() = MaterialTheme.colorScheme.errorContainer
    val accentGreen: Color @Composable get() = MaterialTheme.colorScheme.tertiaryContainer
    val accentBlue: Color @Composable get() = MaterialTheme.colorScheme.secondaryContainer
}

@Composable
fun RemoteControlScreen(
    modifier: Modifier = Modifier,
    viewModel: RemoteViewModel = viewModel(
        factory = zhang.feng.remotecontrol.RemoteViewModelFactory(LocalContext.current.applicationContext)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (uiState.isConnected) {
            TopStatusBar(uiState, viewModel, context)
            Spacer(modifier = Modifier.height(8.dp))
            RemoteControlInterface(viewModel)
        } else {
            DeviceScanInterface(uiState, viewModel, context)
        }
        uiState.errorMessage?.let { error ->
            ErrorSnackbar(error) {
                viewModel.clearError()
            }
        }
    }
}

@Composable
fun TopStatusBar(uiState: RemoteUiState, viewModel: RemoteViewModel, context: Context) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                FontAwesomeIcons.Solid.Tv,
                contentDescription = "TV device",
                tint = RemoteButtonColors.background,
                modifier = Modifier.size(24.dp),
            )
            uiState.connectedDevice?.let { device ->
                Text(
                    text = "$device",
                    fontSize = 16.sp,
                    color = RemoteButtonColors.background
                )
            }

            if (uiState.isConnected) {
                Button(
                    onClick = {
                        viewModel.disconnect()
                        RemoteViewModel.clearLastConnectedDevice(context)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Icon(Icons.Default.Close, contentDescription = stringResource(id = R.string.disconnect))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(stringResource(id = R.string.disconnect))
                }
            }
        }
    }
}

@Composable
fun DeviceScanInterface(
    uiState: RemoteUiState,
    viewModel: RemoteViewModel,
    context: Context
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.title_remote),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp),
            color = RemoteButtonColors.background
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = { viewModel.scanForDevices() },
            enabled = !uiState.isScanning,
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (uiState.isScanning) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = RemoteButtonColors.foreground
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(id = R.string.scanning))
            } else {
                Icon(Icons.Default.Search, contentDescription = stringResource(id = R.string.scan_device))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(id = R.string.scan_device))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.devices.isNotEmpty()) {
            Text(
                text = stringResource(id = R.string.found_devices),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp),
                color = RemoteButtonColors.background
            )

            LazyColumn {
                items(uiState.devices) { device ->
                    DeviceItem(
                        ipAddress = device,
                        onClick = { viewModel.connectToDevice(device) },
                        isConnecting = uiState.isConnecting
                    )
                }
            }
        } else if (!uiState.isScanning) {
            Text(
                text = stringResource(id = R.string.no_device),
                textAlign = TextAlign.Center,
                color = Color.Gray,
                modifier = Modifier.padding(top = 32.dp)
            )
        }
    }
}

@Composable
fun DeviceItem(
    ipAddress: String,
    onClick: () -> Unit,
    isConnecting: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(enabled = !isConnecting) { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .background(RemoteButtonColors.background)
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                FontAwesomeIcons.Solid.Tv,
                contentDescription = stringResource(id = R.string.found_devices),
                tint = RemoteButtonColors.foreground,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = ipAddress,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = RemoteButtonColors.text
            )
            Spacer(modifier = Modifier.weight(1f))
            if (isConnecting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    FontAwesomeIcons.Solid.ArrowRight,
                    contentDescription = stringResource(id = R.string.scan_device),
                    tint = RemoteButtonColors.foreground,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
fun RemoteControlInterface(viewModel: RemoteViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    uiState.connectedDevice
    LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // First row: Power off, Mute
        Row(horizontalArrangement = Arrangement.spacedBy(96.dp)) {
            RemoteButton(
                icon = FontAwesomeIcons.Solid.PowerOff, // Power off
                onClick = { viewModel.sendKeyEvent(RemoteKeys.KEY_POWER) },
                modifier = Modifier.size(60.dp),
            )
            RemoteButton(
                icon = FontAwesomeIcons.Solid.VolumeMute, // Mute
                onClick = { viewModel.sendKeyEvent(RemoteKeys.KEY_MUTE) },
                modifier = Modifier.size(60.dp),
            )
        }
        Spacer(modifier = Modifier.height(48.dp))

        // Second row: Direction keys and OK
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            RemoteButton(
                icon = FontAwesomeIcons.Solid.AngleUp,
                onClick = { viewModel.sendKeyEvent(RemoteKeys.KEY_UP) },
                modifier = Modifier.size(60.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RemoteButton(
                    icon = FontAwesomeIcons.Solid.AngleLeft,
                    onClick = { viewModel.sendKeyEvent(RemoteKeys.KEY_LEFT) },
                    modifier = Modifier.size(60.dp)
                )
                RemoteButton(
                    text = stringResource(id = R.string.ok),
                    onClick = { viewModel.sendKeyEvent(RemoteKeys.KEY_ENTER) },
                    modifier = Modifier.size(60.dp),
                )
                RemoteButton(
                    icon = FontAwesomeIcons.Solid.AngleRight,
                    onClick = { viewModel.sendKeyEvent(RemoteKeys.KEY_RIGHT) },
                    modifier = Modifier.size(60.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            RemoteButton(
                icon = FontAwesomeIcons.Solid.AngleDown,
                onClick = { viewModel.sendKeyEvent(RemoteKeys.KEY_DOWN) },
                modifier = Modifier.size(60.dp)
            )
        }
        Spacer(modifier = Modifier.height(36.dp))

        // Third row: Back, Home, Menu
        Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
            RemoteButton(
                icon = FontAwesomeIcons.Solid.AngleLeft,
                onClick = { viewModel.sendKeyEvent(RemoteKeys.KEY_BACK) },
                modifier = Modifier.size(60.dp),
            )
            RemoteButton(
                icon = FontAwesomeIcons.Solid.Home,
                onClick = { viewModel.sendShellCommand(RemoteKeys.HOME_SHELL) },
                modifier = Modifier.size(60.dp),
            )
            RemoteButton(
                icon = FontAwesomeIcons.Solid.Bars,
                onClick = { viewModel.sendKeyEvent(RemoteKeys.KEY_MENU) },
                modifier = Modifier.size(60.dp),
            )
        }
        Spacer(modifier = Modifier.height(36.dp))

        // Fourth row: Volume -, Volume +
        Row(horizontalArrangement = Arrangement.spacedBy(48.dp)) {
            VolumeButton(
                icon = FontAwesomeIcons.Solid.VolumeDown,
                label = "-",
                onClick = { viewModel.sendKeyEvent(RemoteKeys.KEY_VOLUME_DOWN) },
                modifier = Modifier.size(width = 100.dp, height = 50.dp),
            )
            VolumeButton(
                icon = FontAwesomeIcons.Solid.VolumeUp,
                label = "+",
                onClick = { viewModel.sendKeyEvent(RemoteKeys.KEY_VOLUME_UP) },
                modifier = Modifier.size(width = 100.dp, height = 50.dp),
            )
        }
    }
}

@Composable
fun RemoteButton(
    icon: ImageVector? = null,
    text: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = RemoteButtonColors.background,
    contentColor: Color = RemoteButtonColors.foreground,
    borderColor: Color = RemoteButtonColors.border,
    textColor: Color = RemoteButtonColors.text
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { onClick() }
            .border(1.dp, borderColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
        } else if (text != null) {
            Text(
                text = text,
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun VolumeButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = RemoteButtonColors.background,
    contentColor: Color = RemoteButtonColors.foreground,
    borderColor: Color = RemoteButtonColors.border,
    textColor: Color = RemoteButtonColors.text
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .border(1.dp, borderColor, RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                color = textColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ErrorSnackbar(
    message: String,
    onDismiss: () -> Unit
) {
    Snackbar(
        modifier = Modifier.padding(16.dp),
        action = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.confirm))
            }
        }
    ) {
        Text(message)
    }
} 