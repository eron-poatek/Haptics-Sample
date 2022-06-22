package com.example.haptics

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.os.VibrationEffect.*
import android.provider.Settings
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import androidx.core.view.isVisible
import com.example.haptics.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding!!

    private var hapticsEnabled: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater).apply { setContentView(root) }

        setListeners()
    }

    override fun onResume() {
        super.onResume()
        checkIfDeviceCapableOfHaptics()
    }

    private fun checkIfDeviceCapableOfHaptics() {
        val hasVibrator = getVibrator().hasVibrator()
        hapticsEnabled = if (hasVibrator) {
            Settings.System.getInt(contentResolver, Settings.System.HAPTIC_FEEDBACK_ENABLED, 0) == 1
        } else {
            false
        }
        binding.warningVibration.isVisible = !hapticsEnabled
        if (!hapticsEnabled) {
            binding.warningVibration.setOnClickListener {
                startActivity(Intent(Settings.ACTION_SETTINGS))
            }
        }
    }

    private fun getVibrator(): Vibrator {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
    }

    /**
     * Flag to ignore device haptic setting: HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setListeners() {
        binding.hapticsClockTickButton.setOnTouchListener { view, _ ->
            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
            view.performClick()
        }

        binding.hapticsKeyboardTapButton.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }

        binding.hapticsDoubleClickButton.setOnClickListener {
            val effect = createWaveform(longArrayOf(0, 30, 100, 30), -1)
            val vibrator = getVibrator()
            vibrator.cancel()
            vibrator.vibrate(effect)
        }

        binding.hapticsLongVibrationButton.setOnClickListener {
            val effect = createOneShot(2000, 255)
            val vibrator = getVibrator()
            vibrator.cancel()
            vibrator.vibrate(effect)
        }
    }
}
