package com.earthsheartbeat.monitor

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/** Shown when a widget is placed: pick which feed this widget displays. */
class WidgetConfigActivity : AppCompatActivity() {

    private var widgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // if the user backs out, leave the widget uncreated
        setResult(Activity.RESULT_CANCELED)

        setContentView(R.layout.activity_widget_config)

        widgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish(); return
        }

        val list = findViewById<ListView>(R.id.feed_list)
        val feeds = Feeds.ALL
        val adapter = object : ArrayAdapter<Feed>(
            this, 0, feeds
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = convertView ?: layoutInflater.inflate(
                    R.layout.widget_config_item, parent, false
                )
                val f = feeds[position]
                v.findViewById<TextView>(R.id.item_label).text = f.label
                v.findViewById<TextView>(R.id.item_source).text = f.source
                return v
            }
        }
        list.adapter = adapter
        list.setOnItemClickListener { _, _, position, _ ->
            choose(feeds[position].id)
        }
    }

    private fun choose(feedId: String) {
        WidgetPrefs.setFeedId(this, widgetId, feedId)

        // build the widget now and kick off an immediate refresh
        val mgr = AppWidgetManager.getInstance(this)
        HeartbeatWidget().onUpdate(this, mgr, intArrayOf(widgetId))
        WidgetUpdateWorker.enqueueNow(this)

        val result = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        setResult(Activity.RESULT_OK, result)
        finish()
    }
}
