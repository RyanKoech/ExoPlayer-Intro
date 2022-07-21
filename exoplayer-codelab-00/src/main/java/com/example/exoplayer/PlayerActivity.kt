/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
* limitations under the License.
 */
package com.example.exoplayer

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.example.exoplayer.databinding.ActivityPlayerBinding

private const val TAG = "PlayerActivity"
/**
 * A fullscreen activity to play audio or video streams.
 */
class PlayerActivity : AppCompatActivity() {

    private val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityPlayerBinding.inflate(layoutInflater)
    }
    private var player : ExoPlayer? = null
    private var playWhenReady = true
    private var currentItem = 0
    private var playbackPosition = 0L
    private val playbackStateListener : Player.Listener = playbackStateListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate is Called")
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

    }

    override fun onStart() {
        super.onStart()
        /*
        * Android API level 24 and higher supports multiple windows.
        * As your app can be visible, but not active in split window mode,
        * you need to initialize the player in onStart
        */
        if (Util.SDK_INT > 23){
            intializePlayer()
        }
        Log.i(TAG, "onStart is Called")
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        /*
        * Android API level 23 and lower requires you to wait
        * as long as possible until you grab resources,
        */
        if(Util.SDK_INT <= 23 || player == null){
            intializePlayer()
        }
        Log.i(TAG, "onResume is Called")
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, viewBinding.videoView).let{ controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    /*
    * With API Level 23 and lower, there is no guarantee of onStop being called,
    * so you have to release the player as early as possible
    * */
    override fun onPause() {
        super.onPause()
        if(Util.SDK_INT <= 23){
            releasePlayer()
        }
        Log.i(TAG, "onPause is Called")
    }

    /*
    * With API Level 24 and higher (which brought multi- and split-window mode),
    * onStop is guaranteed to be called. In the paused state, your activity is still visible,
    * so you wait to release the player until onStop
    * */
    override fun onStop() {
        super.onStop()
        if(Util.SDK_INT > 23){
            releasePlayer()
        }
        Log.i(TAG, "onStop is Called")
    }

    private fun releasePlayer() {
        player?.let{ exoPlayer ->
            playbackPosition = exoPlayer.currentPosition
            currentItem = exoPlayer.currentMediaItemIndex
            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.removeListener(playbackStateListener)
            exoPlayer.release()
        }
        player = null
    }

    //Initialize an player and binding to the videoView's player
    private fun intializePlayer() {
        val trackSelector = DefaultTrackSelector(this).apply {
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        }
        player = ExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .build()
            .also { exoPlayer ->
                viewBinding.videoView.player = exoPlayer
//                val mediaItem = MediaItem.Builder()
//                    .setUri(getString(R.string.media_url_dash))
//                    .setMimeType(MimeTypes.APPLICATION_MPD)
//                    .build()
                val mediaItem = MediaItem.fromUri(getString(R.string.media_url_live_radio))
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.seekTo(currentItem, playbackPosition)
                exoPlayer.addListener(playbackStateListener)
                exoPlayer.prepare()
            }

    }

    private fun playbackStateListener () = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            val stateString: String = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                else -> "UNKNOWN_STATE             -"
            }
            Log.d(TAG, "changed state to $stateString")
        }
    }
}