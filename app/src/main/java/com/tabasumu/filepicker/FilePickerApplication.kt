package com.tabasumu.filepicker

import android.app.Application
import timber.log.Timber

/**
 * FilePicker
 * @author Victor Oyando
 * @email oyandovic@gmail.com
 *
 * Created 27/06/2022 at 15:33
 */
class FilePickerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}