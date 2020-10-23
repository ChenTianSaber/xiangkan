package com.chentian.xiangkan

import com.chentian.xiangkan.db.RSSManagerInfo
import com.chentian.xiangkan.page.manager.EventListener

object MyEventBus {

    private var listeners: MutableList<EventListener> = ArrayList()

    fun register(eventListener: EventListener) {
        listeners.add(eventListener)
    }

    fun unregister(eventListener: EventListener) {
        listeners.remove(eventListener)
    }

    fun post(rssManagerInfo: RSSManagerInfo) {
        for (eventListener in listeners) {
            eventListener.addSuccess(rssManagerInfo)
        }
    }
}