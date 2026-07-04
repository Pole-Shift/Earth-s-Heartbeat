package com.earthsheartbeat.monitor

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Home-screen widget. Each instance remembers which feed it shows
 * (chosen in WidgetConfigActivity). A single hourly WorkManager job
 * refreshes every placed widget.
 */
class HeartbeatWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (id in appWidgetIds) {
            pushPlaceholder(context, appWidgetManager, id)
        }
        // trigger an immediate fetch + schedule the hourly job
        WidgetUpdateWorker.enqueueNow(context)
        scheduleHourly(context)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        for (id in appWidgetIds) WidgetPrefs.clear(context, id)
    }

    override fun onDisabled(context: Context) {
        // last widget removed -> stop the periodic job
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    private fun pushPlaceholder(
        context: Context,
        mgr: AppWidgetManager,
        id: Int
    ) {
        val feed = Feeds.byId(WidgetPrefs.getFeedId(context, id))
        val views = RemoteViews(context.packageName, R.layout.widget_layout)
        views.setTextViewText(R.id.widget_title, feed.label)
        views.setTextViewText(R.id.widget_source, feed.source)
        views.setTextViewText(R.id.widget_updated, "loading…")

        // tap widget -> open the full dashboard
        val open = Intent(context, SplashActivity::class.java)
        val pi = PendingIntent.getActivity(
            context, id, open,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_root, pi)
        mgr.updateAppWidget(id, views)
    }

    companion object {
        const val WORK_NAME = "heartbeat_widget_hourly"

        fun scheduleHourly(context: Context) {
            val req = PeriodicWorkRequestBuilder<WidgetUpdateWorker>(1, TimeUnit.HOURS)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                req
            )
        }
    }
}
