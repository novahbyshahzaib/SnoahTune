package com.snoahtune.app.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.snoahtune.app.MainActivity
import com.snoahtune.app.R
import com.snoahtune.app.service.MusicService

class MusicWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { id ->
            updateWidget(context, appWidgetManager, id)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            ACTION_PLAY_PAUSE, ACTION_NEXT, ACTION_PREV -> {
                val serviceIntent = Intent(context, MusicService::class.java).apply {
                    action = intent.action
                }
                context.startService(serviceIntent)
            }
        }
    }

    companion object {
        const val ACTION_PLAY_PAUSE = "com.snoahtune.app.WIDGET_PLAY_PAUSE"
        const val ACTION_NEXT       = "com.snoahtune.app.WIDGET_NEXT"
        const val ACTION_PREV       = "com.snoahtune.app.WIDGET_PREV"

        fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int,
            title: String = "SnoahTune",
            artist: String = "Tap to open",
            isPlaying: Boolean = false
        ) {
            val views = RemoteViews(context.packageName, R.layout.music_widget_layout)

            views.setTextViewText(R.id.widget_title, title)
            views.setTextViewText(R.id.widget_artist, artist)
            views.setTextViewText(
                R.id.widget_play_pause,
                if (isPlaying) "⏸" else "▶"
            )

            // Tap widget title area to open app
            val openIntent = Intent(context, MainActivity::class.java)
            val openPending = PendingIntent.getActivity(
                context, 0, openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_container, openPending)

            // Play/Pause button
            val playPausePending = PendingIntent.getBroadcast(
                context, 1,
                Intent(context, MusicWidgetProvider::class.java).apply { action = ACTION_PLAY_PAUSE },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_play_pause, playPausePending)

            // Previous button
            val prevPending = PendingIntent.getBroadcast(
                context, 2,
                Intent(context, MusicWidgetProvider::class.java).apply { action = ACTION_PREV },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_prev, prevPending)

            // Next button
            val nextPending = PendingIntent.getBroadcast(
                context, 3,
                Intent(context, MusicWidgetProvider::class.java).apply { action = ACTION_NEXT },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_next, nextPending)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
