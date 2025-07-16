package com.netrain.sdlfy.sharedemo

import android.Manifest
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import timber.log.Timber
import java.security.Permission
import java.security.Permissions

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestPermissions(arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ),300)
        findViewById<TextView>(R.id.tv_test).setOnClickListener {
            Timber.d("日志测试日志日志测试日志日志测试日志日志测试日志日志测试日志日志测试日志日志测试日志日志测试日志日志测试日志日志测试日志日志测试日志日志测试日志")
        }

    }
}