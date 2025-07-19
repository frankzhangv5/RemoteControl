package zhang.feng.remotecontrol

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import zhang.feng.remotecontrol.ui.RemoteControlScreen
import zhang.feng.remotecontrol.ui.theme.RemoteControlTheme

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: RemoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("RemoteControl", "onCreate called")
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 35) {
            enableEdgeToEdge()
        }
        viewModel = ViewModelProvider(this, RemoteViewModelFactory(applicationContext))
            .get(RemoteViewModel::class.java)
        setContent {
            RemoteControlTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    RemoteControlScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel
                    )
                }
            }
        }
    }


    override fun onStart() {
        super.onStart()
        Log.d("RemoteControl", "onStart called")
    }


    override fun onResume() {
        Log.d("RemoteControl", "onResume called")
        super.onResume()
        val lastDeviceIp = RemoteViewModel.getLastConnectedDevice(this)
        if (lastDeviceIp != null) {
            viewModel.connectToDevice(lastDeviceIp)
        }
    }

    override fun onPause() {
        Log.d("RemoteControl", "onPause called")
        super.onPause()
        val deviceIp = viewModel.uiState.value.connectedDevice
        if (deviceIp != null) {
            viewModel.disconnectDevice(deviceIp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RemoteControlPreview() {
    RemoteControlTheme {
        RemoteControlScreen()
    }
}