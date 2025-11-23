package util

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.platform.win32.Kernel32
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinNT
import com.sun.jna.ptr.IntByReference
import com.sun.jna.win32.StdCallLibrary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.swing.filechooser.FileSystemView

// Define Psapi interface for process path extraction
interface Psapi : StdCallLibrary {
    fun GetModuleFileNameExA(hProcess: WinNT.HANDLE, hModule: WinDef.HMODULE?, lpFilename: ByteArray, nSize: Int): Int

    companion object {
        val INSTANCE: Psapi = Native.load("psapi", Psapi::class.java)
    }
}

data class ActiveAppInfo(
    val title: String,
    val icon: ImageBitmap?
)

object AppMonitor {
    private val user32 = User32.INSTANCE
    private val kernel32 = Kernel32.INSTANCE
    private val psapi = Psapi.INSTANCE

    fun activeAppFlow(): Flow<ActiveAppInfo> = flow {
        var lastHwnd: WinDef.HWND? = null

        while (true) {
            try {
                val foregroundHwnd = user32.GetForegroundWindow()

                if (foregroundHwnd != null) {
                    if (foregroundHwnd != lastHwnd) {
                        lastHwnd = foregroundHwnd
                        val title = getWindowTitle(foregroundHwnd)

                        if (title.isNotBlank() && title != "Default IME" && title != "MSCTFIME UI") {
                            // Extract real icon in IO context
                            val icon = withContext(Dispatchers.IO) {
                                getAppIcon(foregroundHwnd)
                            }
                            emit(ActiveAppInfo(title, icon))
                        }
                    }
                }
            } catch (e: Exception) {
                // Ignore errors to keep the app alive
            }
            delay(500)
        }
    }

    private fun getWindowTitle(hwnd: WinDef.HWND): String {
        val length = user32.GetWindowTextLength(hwnd)
        if (length == 0) return ""
        val buffer = CharArray(length + 1)
        user32.GetWindowText(hwnd, buffer, length + 1)
        val fullTitle = String(buffer).trim { it <= ' ' }
        return if (fullTitle.contains("-")) {
            fullTitle.substringAfterLast("-").trim()
        } else {
            fullTitle
        }
    }

    private fun getAppIcon(hwnd: WinDef.HWND): ImageBitmap? {
        return try {
            val processId = IntByReference()
            user32.GetWindowThreadProcessId(hwnd, processId)

            // Open process with QUERY_INFORMATION and VM_READ permissions
            val processHandle = kernel32.OpenProcess(
                0x0400 or 0x0010, // PROCESS_QUERY_INFORMATION | PROCESS_VM_READ
                false,
                processId.value
            )

            if (processHandle != null) {
                val buffer = ByteArray(1024)
                val result = psapi.GetModuleFileNameExA(processHandle, null, buffer, buffer.size)
                kernel32.CloseHandle(processHandle)

                if (result > 0) {
                    // Clean up the path string
                    val path = String(buffer, 0, result).trim()
                    val file = File(path)

                    if (file.exists()) {
                        // Use Swing's FileSystemView to get the exact icon shown in Explorer
                        val icon = FileSystemView.getFileSystemView().getSystemIcon(file)

                        // Convert Icon to ImageBitmap
                        val bufferedImage = BufferedImage(
                            icon.iconWidth,
                            icon.iconHeight,
                            BufferedImage.TYPE_INT_ARGB
                        )
                        val g: Graphics2D = bufferedImage.createGraphics()
                        icon.paintIcon(null, g, 0, 0)
                        g.dispose()

                        return bufferedImage.toComposeImageBitmap()
                    }
                }
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}