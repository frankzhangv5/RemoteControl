package zhang.feng.remotecontrol

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RemoteViewModel(private val context: Context) : ViewModel() {
    private val adbManager = AdbManager(context)
    private val networkScanner = NetworkScanner()

    private val _uiState = MutableStateFlow(RemoteUiState())
    val uiState: StateFlow<RemoteUiState> = _uiState.asStateFlow()

    fun scanForDevices() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isScanning = true,
                devices = emptyList(),
                errorMessage = null
            )

            try {
                val devices = networkScanner.scanForDevices(context)
                _uiState.value = _uiState.value.copy(
                    isScanning = false,
                    devices = devices
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isScanning = false,
                    errorMessage = "Scan failed: ${e.message}"
                )
            }
        }
    }

    fun connectToDevice(ipAddress: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isConnecting = true,
                errorMessage = null
            )

            try {
                val success = adbManager.connect(ipAddress)
                if (success) {
                    saveLastConnectedDevice(context, ipAddress)
                    _uiState.value = _uiState.value.copy(
                        isConnecting = false,
                        isConnected = true,
                        connectedDevice = ipAddress
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isConnecting = false,
                        errorMessage = "Connection failed"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isConnecting = false,
                    errorMessage = "Connection exception: ${e.message}"
                )
            }
        }
    }

    fun disconnect() {
        adbManager.disconnectAll()
        _uiState.value = _uiState.value.copy(
            isConnected = false,
            connectedDevice = null
        )
    }

    fun disconnectDevice(ipAddress: String) {
        adbManager.disconnect(ipAddress)
        if (_uiState.value.connectedDevice == ipAddress) {
            _uiState.value = _uiState.value.copy(
                isConnected = false,
                connectedDevice = null
            )
        }
    }

    fun sendKeyEvent(keyCode: Int, longPress: Boolean = false) {
        if (!_uiState.value.isConnected) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Not connected to device"
            )
            return
        }

        viewModelScope.launch {
            try {
                val success =
                    adbManager.sendKeyEvent(_uiState.value.connectedDevice!!, keyCode, longPress)
                if (!success) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Failed to send key event"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Exception sending key event: ${e.message}"
                )
            }
        }
    }

    fun sendShellCommand(command: String) {
        if (!_uiState.value.isConnected) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Not connected to device"
            )
            return
        }
        viewModelScope.launch {
            try {
                val success = adbManager.sendShellCommand(_uiState.value.connectedDevice!!, command)
                if (!success) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Command sending failed"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Exception sending command: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    override fun onCleared() {
        super.onCleared()
        adbManager.disconnectAll()
    }

    companion object {
        fun getLastConnectedDevice(context: Context): String? {
            return context.getSharedPreferences("adb_prefs", Context.MODE_PRIVATE)
                .getString("last_connected_device", null)
        }

        fun saveLastConnectedDevice(context: Context, ip: String) {
            context.getSharedPreferences("adb_prefs", Context.MODE_PRIVATE)
                .edit()
                .putString("last_connected_device", ip)
                .apply()
        }

        fun clearLastConnectedDevice(context: Context) {
            context.getSharedPreferences("adb_prefs", Context.MODE_PRIVATE)
                .edit()
                .remove("last_connected_device")
                .apply()
        }
    }
}

// Recommended to use with ViewModelProvider.Factory
class RemoteViewModelFactory(private val context: Context) :
    androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RemoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RemoteViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

data class RemoteUiState(
    val isScanning: Boolean = false,
    val isConnecting: Boolean = false,
    val isConnected: Boolean = false,
    val devices: List<String> = emptyList(),
    val connectedDevice: String? = null,
    val errorMessage: String? = null
) 