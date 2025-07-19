package zhang.feng.remotecontrol

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.Socket
import java.util.concurrent.Executors

class NetworkScanner {
    companion object {
        private const val TAG = "NetworkScanner"
        private const val ADB_PORT = 5555
        private const val SCAN_TIMEOUT = 1000
        private const val MAX_THREADS = 50
    }

    suspend fun scanForDevices(context: Context): List<String> = withContext(Dispatchers.IO) {
        val devices = mutableListOf<String>()
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val connectionInfo = wifiManager.connectionInfo

        if (connectionInfo == null) {
            Log.e(TAG, "Unable to get WiFi connection info")
            return@withContext devices
        }

        val ipAddress = connectionInfo.ipAddress
        val networkPrefix = getNetworkPrefix(ipAddress)

        Log.d(TAG, "Start scanning network: $networkPrefix")

        val executor = Executors.newFixedThreadPool(MAX_THREADS)
        val futures = mutableListOf<java.util.concurrent.Future<*>>()

        // Scan IP addresses in the range 1-254
        for (i in 1..254) {
            val targetIp = "$networkPrefix$i"
            val future = executor.submit {
                if (isAdbDevice(targetIp)) {
                    synchronized(devices) {
                        devices.add(targetIp)
                        Log.d(TAG, "Device found: $targetIp")
                    }
                }
            }
            futures.add(future)
        }

        // Wait for all scans to complete
        futures.forEach { it.get() }
        executor.shutdown()

        Log.d(TAG, "Scan complete, found devices: ${devices.size}")
        return@withContext devices
    }

    private fun getNetworkPrefix(ipAddress: Int): String {
        val bytes = ByteArray(4)
        bytes[0] = (ipAddress and 0xFF).toByte()
        bytes[1] = (ipAddress shr 8 and 0xFF).toByte()
        bytes[2] = (ipAddress shr 16 and 0xFF).toByte()
        bytes[3] = (ipAddress shr 24 and 0xFF).toByte()

        return "${bytes[0].toInt() and 0xFF}.${bytes[1].toInt() and 0xFF}.${bytes[2].toInt() and 0xFF}."
    }

    private fun isAdbDevice(ipAddress: String): Boolean {
        return try {
            val socket = Socket()
            socket.connect(java.net.InetSocketAddress(ipAddress, ADB_PORT), SCAN_TIMEOUT)
            socket.close()
            true
        } catch (e: Exception) {
            false
        }
    }
} 