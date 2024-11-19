package edu.farmingdale.threadsexample.countdowntimer

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.farmingdale.threadsexample.R
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {
    private var timerJob: Job? = null
    private var mediaPlayer: MediaPlayer? = null

    // Values selected in time picker
    var selectedHour by mutableIntStateOf(0)
        private set
    var selectedMinute by mutableIntStateOf(0)
        private set
    var selectedSecond by mutableIntStateOf(0)
        private set

    // Total milliseconds when timer starts
    var totalMillis by mutableLongStateOf(0L)
        private set

    // Time that remains
    var remainingMillis by mutableLongStateOf(0L)
        private set

    // Timer's running status
    var isRunning by mutableStateOf(false)
        private set

    // Store last selected time for reset functionality
    private var lastSelectedTime = Triple(0, 0, 0)

    fun initializeMediaPlayer(context: Context) {
        mediaPlayer = MediaPlayer.create(context, R.raw.timer_finish)
    }

    fun selectTime(hour: Int, min: Int, sec: Int) {
        selectedHour = hour
        selectedMinute = min
        selectedSecond = sec
        lastSelectedTime = Triple(hour, min, sec)
    }

    fun startTimer() {
        totalMillis = (selectedHour * 60 * 60 + selectedMinute * 60 + selectedSecond) * 1000L

        if (totalMillis > 0) {
            isRunning = true
            remainingMillis = totalMillis

            timerJob = viewModelScope.launch {
                while (remainingMillis > 0) {
                    delay(1000)
                    remainingMillis -= 1000
                }

                isRunning = false
                playTimerFinishSound()
            }
        }
    }

    private fun playTimerFinishSound() {
        mediaPlayer?.apply {
            if (!isPlaying) {
                seekTo(0)
                start()
            }
        }
    }

    fun cancelTimer() {
        if (isRunning) {
            timerJob?.cancel()
            isRunning = false
            remainingMillis = 0
            mediaPlayer?.stop()
            mediaPlayer?.prepare()
        }
    }

    fun resetTimer() {
        selectedHour = lastSelectedTime.first
        selectedMinute = lastSelectedTime.second
        selectedSecond = lastSelectedTime.third
        remainingMillis = 0
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}