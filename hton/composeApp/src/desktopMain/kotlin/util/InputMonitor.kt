package util

import com.sun.jna.platform.win32.User32
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object InputMonitor {
    private val user32 = User32.INSTANCE

    // VK Codes
    private const val VK_F8 = 0x77
    private const val VK_F9 = 0x78

    fun typingStateFlow(): Flow<Boolean> = flow {
        while (true) {
            var isTyping = false

            // Check F8 -> Trigger Shield (Mock)
            if (user32.GetAsyncKeyState(VK_F8).toInt() < 0) {
                LogitechService.forceTrigger("action.hero_shield")
            }

            // Check F9 -> Trigger Attack (Mock)
            if (user32.GetAsyncKeyState(VK_F9).toInt() < 0) {
                LogitechService.forceTrigger("action.hero_attack")
            }

            // General Typing Detection (A-Z, 0-9)
            for (i in 0x30..0x5A) {
                if (user32.GetAsyncKeyState(i).toInt() < 0) {
                    isTyping = true
                    break
                }
            }
            emit(isTyping)
            delay(50)
        }
    }
}