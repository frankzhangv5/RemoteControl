package zhang.feng.remotecontrol

import android.content.Context
import android.util.Base64
import android.util.Log
import com.cgutman.adblib.AdbBase64
import com.cgutman.adblib.AdbConnection
import com.cgutman.adblib.AdbCrypto
import com.cgutman.adblib.AdbStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.Socket

class AdbManager(private val context: Context) {
    companion object {
        private const val TAG = "AdbManager"
        private const val ADB_PORT = 5555
    }

    private val connectionCache = mutableMapOf<String, AdbConnection>()
    private val streamCache = mutableMapOf<String, AdbStream>()
    private var crypto: AdbCrypto? = null

    // Base64 implementation
    class SimpleBase64 : AdbBase64 {
        override fun encodeToString(data: ByteArray): String {
            return Base64.encodeToString(data, Base64.NO_WRAP)
        }
    }

    // Initialize or load key
    private fun initCrypto(): AdbCrypto {
        if (crypto != null) return crypto!!
        val privKey = File(context.filesDir, "priv.key")
        val pubKey = File(context.filesDir, "pub.key")
        crypto = if (pubKey.exists() && privKey.exists()) {
            try {
                Log.d(TAG, "Load existing ADB key")
                AdbCrypto.loadAdbKeyPair(SimpleBase64(), privKey, pubKey)
            } catch (e: Exception) {
                Log.e(TAG, "Key loading failed, regenerate: ${e.message}")
                generateAndSaveCrypto(privKey, pubKey)
            }
        } else {
            generateAndSaveCrypto(privKey, pubKey)
        }
        return crypto!!
    }

    private fun generateAndSaveCrypto(privKey: File, pubKey: File): AdbCrypto {
        Log.d(TAG, "Generate new ADB key")
        val crypto = AdbCrypto.generateAdbKeyPair(SimpleBase64())
        crypto.saveAdbKeyPair(privKey, pubKey)
        return crypto
    }

    // Establish and cache connection
    suspend fun connect(deviceIp: String, port: Int = ADB_PORT): Boolean =
        withContext(Dispatchers.IO) {
            Log.d(TAG, "connect called: deviceIp=$deviceIp, port=$port")
            val key = "$deviceIp:$port"
            if (crypto == null) initCrypto()
            if (!connectionCache.containsKey(key)) {
                try {
                    val socket = Socket(deviceIp, port)
                    val connection = AdbConnection.create(socket, crypto)
                    connection.connect()
                    connectionCache[key] = connection
                    Log.d(TAG, "ADB connection established and cached: $key")
                } catch (e: Exception) {
                    Log.e(TAG, "Connection failed: $key, ${e.message}", e)
                    return@withContext false
                }
            }
            return@withContext true
        }

    // Send shell command, prefer cached stream
    suspend fun sendShellCommand(deviceIp: String, command: String): Boolean =
        withContext(Dispatchers.IO) {
            Log.d(TAG, "sendShellCommand called: deviceIp=$deviceIp, command=$command")
            val key = "$deviceIp:$ADB_PORT"
            var stream: AdbStream? = null
            try {
                // Prefer cached stream
                stream = streamCache[key]
                if (stream == null) {
                    val connection = connectionCache[key]
                    if (connection == null) {
                        Log.e(TAG, "ADB connection not established: $key")
                        return@withContext false
                    }
                    stream = connection.open("shell:")
                    streamCache[key] = stream
                }
                stream.write("$command\n")
                Log.d(TAG, "Command sent successfully: $command")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Command sending failed: $command, exception: ${e.message}", e)
                false
            }
        }

    // Send key event
    suspend fun sendKeyEvent(deviceIp: String, keyCode: Int, longPress: Boolean = false): Boolean {
        Log.d(
            TAG,
            "sendKeyEvent called: deviceIp=$deviceIp, keyCode=$keyCode, longPress=$longPress"
        )
        val command = if (longPress) {
            // 长按事件用两次 keyevent 或自定义 shell 命令
            "input keyevent --longpress $keyCode"
        } else {
            "input keyevent $keyCode"
        }
        return sendShellCommand(deviceIp, command)
    }

    // Disconnect all connections
    fun disconnectAll() {
        Log.d(TAG, "disconnectAll called")
        for ((_, stream) in streamCache) {
            try {
                stream.close()
            } catch (_: Exception) {
            }
        }
        streamCache.clear()

        for ((_, conn) in connectionCache) {
            try {
                conn.close()
            } catch (_: Exception) {
            }
        }
        connectionCache.clear()
        Log.d(TAG, "All ADB connections and streams disconnected")
    }

    // Check if device is connected
    fun isConnected(deviceIp: String, port: Int = ADB_PORT): Boolean {
        Log.d(TAG, "isConnected called: deviceIp=$deviceIp, port=$port")
        val key = "$deviceIp:$port"
        return connectionCache.containsKey(key)
    }

    // Disconnect specified device connection and stream
    fun disconnect(deviceIp: String, port: Int = ADB_PORT) {
        Log.d(TAG, "disconnect called: deviceIp=$deviceIp, port=$port")
        val key = "$deviceIp:$port"
        // Close stream
        streamCache[key]?.let {
            try {
                it.close()
            } catch (_: Exception) {
            }
            streamCache.remove(key)
        }
        // Close connection
        connectionCache[key]?.let {
            try {
                it.close()
            } catch (_: Exception) {
            }
            connectionCache.remove(key)
        }
        Log.d(TAG, "Device disconnected: $key")
    }
} 