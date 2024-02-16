package com.xtreamiptv
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context

@CloudstreamPlugin
class XtreamIPTVPlugin: Plugin() {
    override fun load(context: Context) {
        registerMainAPI(XtreamIPTV())
    }
}