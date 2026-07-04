package com.earthsheartbeat.monitor

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.RemoteViews
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Downloads each placed widget's selected image and pushes it into the RemoteViews. */
class WidgetUpdateWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result {
        val mgr = AppWidgetManager.getInstance(applicationContext)
        val ids = mgr.getAppWidgetIds(
            ComponentName(applicationContext, HeartbeatWidget::class.java)
        )
        if (ids.isEmpty()) return Result.success()

        val stamp = SimpleDateFormat("HH:mm 'UTC'", Locale.US).apply {
            timeZone = java.util.TimeZone.getTimeZone("UTC")
        }.format(Date())

        for (id in ids) {
            val feed = Feeds.byId(WidgetPrefs.getFeedId(applicationContext, id))
            val views = RemoteViews(applicationContext.packageName, R.layout.widget_layout)
            views.setTextViewText(R.id.widget_title, feed.label)
            views.setTextViewText(R.id.widget_source, feed.source)

            val bmp = fetchBitmap(Feeds.proxied(feed.url))
            if (bmp != null) {
                views.setImageViewBitmap(R.id.widget_image, bmp)
                views.setTextViewText(R.id.widget_updated, "synced $stamp")
            } else {
                views.setTextViewText(R.id.widget_updated, "signal lost · $stamp")
            }
            mgr.updateAppWidget(id, views)
        }
        return Result.success()
    }

    private fun fetchBitmap(url: String): Bitmap? {
        var conn: HttpURLConnection? = null
        return try {
            conn = (URL(url).openConnection() as HttpURLConnection).apply {
                connectTimeout = 15000
                readTimeout = 20000
                instanceFollowRedirects = true
                setRequestProperty("User-Agent", "EarthsHeartbeat/1.0 (Android)")
            }
            if (conn.responseCode in 200..399) {
                conn.inputStream.use { BitmapFactory.decodeStream(it) }
            } else null
        } catch (e: Exception) {
            null
        } finally {
            conn?.disconnect()
        }
    }

    companion object {
        fun enqueueNow(context: Context) {
            val req = OneTimeWorkRequestBuilder<WidgetUpdateWorker>().build()
            WorkManager.getInstance(context).enqueue(req)
        }
    }
}
