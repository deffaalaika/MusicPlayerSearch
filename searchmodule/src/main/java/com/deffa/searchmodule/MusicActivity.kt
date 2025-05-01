package com.deffa.searchmodule

import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.Visualizer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.deffa.searchmodule.databinding.ActivityMusicBinding

class MusicActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMusicBinding
    private var player: MediaPlayer? = null
    private var visualizer: Visualizer? = null
    private val handler = Handler(Looper.getMainLooper())

    private var tracks: List<Track> = emptyList()
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tracks = intent.getParcelableArrayListExtra(EXTRA_TRACKS) ?: emptyList()
        currentIndex = intent.getIntExtra(EXTRA_INDEX, 0)

        binding.btnPrev.setOnClickListener { playAt(currentIndex - 1) }
        binding.btnPlayPause.setOnClickListener { togglePlayPause() }
        binding.btnNext.setOnClickListener { playAt(currentIndex + 1) }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar, pos: Int, fromUser: Boolean) {
                if (fromUser) player?.seekTo(pos)
            }

            override fun onStartTrackingTouch(sb: SeekBar) {}
            override fun onStopTrackingTouch(sb: SeekBar) {}
        })

        playAt(currentIndex)
    }

    private fun playAt(index: Int) {
        if (index !in tracks.indices) return

        player?.release()
        visualizer?.release()
        handler.removeCallbacks(updateSeekbar)

        player = MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            setDataSource(tracks[index].previewUrl)
            setOnPreparedListener {
                binding.seekBar.max = it.duration
                it.start()
                setupVisualizer(it.audioSessionId)
                handler.post(updateSeekbar)
                binding.btnPlayPause.setImageResource(android.R.drawable.ic_media_pause)
            }
            setOnCompletionListener {
                binding.btnPlayPause.setImageResource(android.R.drawable.ic_media_play)
            }
            prepareAsync()
        }

        binding.tvTitle.text = tracks[index].title
        binding.tvArtist.text = tracks[index].artist
        binding.imgArtwork.load(tracks[index].artworkUrl)

        currentIndex = index
        updateControls()
    }

    private fun togglePlayPause() {
        player?.let {
            if (it.isPlaying) {
                it.pause()
                binding.btnPlayPause.setImageResource(android.R.drawable.ic_media_play)
            } else {
                it.start()
                binding.btnPlayPause.setImageResource(android.R.drawable.ic_media_pause)
            }
        }
    }

    private fun updateControls() {
        binding.btnPrev.isEnabled = currentIndex > 0
        binding.btnNext.isEnabled = currentIndex < tracks.lastIndex
        binding.btnPlayPause.isEnabled = player != null
    }

    private val updateSeekbar = object : Runnable {
        override fun run() {
            player?.let {
                binding.seekBar.progress = it.currentPosition
                handler.postDelayed(this, 500)
            }
        }
    }


    private fun setupVisualizer(audioSessionId: Int) {
        visualizer = Visualizer(audioSessionId).apply {
            captureSize = Visualizer.getCaptureSizeRange()[1]
            setDataCaptureListener(object : Visualizer.OnDataCaptureListener {
                override fun onWaveFormDataCapture(
                    viz: Visualizer?, waveform: ByteArray?, samplingRate: Int
                ) {
                    waveform?.let { binding.visualizer.updateVisualizer(it) }
                }

                override fun onFftDataCapture(viz: Visualizer?, fft: ByteArray?, sr: Int) {}
            }, Visualizer.getMaxCaptureRate() / 2, true, false)
            enabled = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        visualizer?.release()
        handler.removeCallbacks(updateSeekbar)
    }

    companion object {
        const val EXTRA_TRACKS = "EXTRA_TRACKS"
        const val EXTRA_INDEX = "EXTRA_INDEX"
    }
}