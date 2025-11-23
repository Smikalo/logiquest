import androidx.compose.ui.window.*
import androidx.compose.ui.unit.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import ui.MainWindow
import java.awt.Toolkit
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

fun main() {
    try {
        realMain()
    } catch (e: Throwable) {
        val logFile = File(System.getProperty("user.home"), "demo_crash.txt")
        val sw = StringWriter()
        e.printStackTrace(PrintWriter(sw))
        logFile.writeText("CRASH REPORT:\n$sw")
    }
}

fun realMain() = application {
    var visible by remember { mutableStateOf(true) }

    // Get Screen Size to position bottom right
    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val windowWidth = 400
    val windowHeight = 280

    // Calculate position: Screen Width - Window Width - Padding
    val posX = screenSize.width - windowWidth - 20
    val posY = screenSize.height - windowHeight - 60 // -60 for Taskbar approximation

    if (visible) {
        Window(
            onCloseRequest = { exitApplication() }, // Exit app when closed
            title = "LogiQuest Battle",
            undecorated = true,
            resizable = false,
            transparent = true, // Required for non-rectangular shapes if we used them
            alwaysOnTop = true, // Keep the battle visible!
            state = WindowState(
                width = windowWidth.dp,
                height = windowHeight.dp,
                position = WindowPosition(posX.dp, posY.dp)
            )
        ) {
            MainWindow(
                onClose = { exitApplication() }
            )
        }
    }
}