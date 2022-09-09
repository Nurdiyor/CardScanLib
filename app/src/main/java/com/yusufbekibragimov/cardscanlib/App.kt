package com.yusufbekibragimov.cardscanlib

import android.app.Application
import uz.scan_card.cardscan.base.ScanBaseActivity

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ScanBaseActivity.warmUp(this)
    }
}