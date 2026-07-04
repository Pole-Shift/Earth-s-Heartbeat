package com.earthsheartbeat.monitor

import android.content.Context

/** Stores which feed each widget instance shows. */
object WidgetPrefs {
    private const val FILE = "heartbeat_widget_prefs"
    private const val KEY = "feed_"

    fun setFeedId(context: Context, widgetId: Int, feedId: String) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit().putString(KEY + widgetId, feedId).apply()
    }

    fun getFeedId(context: Context, widgetId: Int): String? =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .getString(KEY + widgetId, null)

    fun clear(context: Context, widgetId: Int) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit().remove(KEY + widgetId).apply()
    }
}
