package com.netrain.sdlfy.lib_log_cloud

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import timber.log.Timber
import timber.log.Timber.DebugTree
import timber.log.Timber.Tree
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.Date


object TimberProvider {

    const val LOG_FOLDER_NAME = "logs"

    private val fqcnIgnore = listOf(
        Timber::class.java.name,
        Timber.Forest::class.java.name,
        Tree::class.java.name,
        DebugTree::class.java.name,
        TimberProvider::class.java.name,
        TimberProvider::class.java.name + "\$init\$1"
    )


    /**
     * 初始化Timber
     * 自动删除7天前的日志文件
     */
    @SuppressLint("SimpleDateFormat")
    @JvmStatic
    fun init(application: Application, debug: Boolean = false, deleteOldLog: Boolean = false) {
        val dirDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val fileDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

        if (deleteOldLog){
            // 清理过期日志文件
            LogFileUtils.cleanupOldLogs(application)
        }
        if (debug) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(object : Tree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    // 将日志生成文件，放入到指定文件夹
                    val logDir = application.getExternalFilesDir(LOG_FOLDER_NAME)
                    if (logDir == null) {
                        Log.e("FileLog", "Failed to get external files directory")
                        return
                    }
                    if (!logDir.exists()) {
                        logDir.mkdirs()
                    }

                    val date = Date(System.currentTimeMillis())

                    val fileName = "${dirDateFormat.format(date)}.log"

                    val file = File(logDir, fileName)

                    if (!file.exists()) {
                        file.createNewFile()
                    }

                    try {
                        val printWriter = PrintWriter(FileWriter(file, true)) // true 表示追加模式
                        val logDate = fileDateFormat.format(Date())
                        val priority = when (priority) {
                            Log.VERBOSE -> "V"
                            Log.DEBUG -> "D"
                            Log.INFO -> "I"
                            Log.WARN -> "W"
                            Log.ERROR -> "E"
                            Log.ASSERT -> "A"
                            else -> "?"
                        }
                        // 获取调用类的类名作为默认tag
                        val defaultTag = Throwable().stackTrace
                            .firstOrNull { it.className !in fqcnIgnore }
                            ?.className?.substringAfterLast('.')
                            ?: "Unknown"

                        Throwable().stackTrace.forEach {
                            Log.d("FileLog", "stackTrace: ${it.className}")
                        }

                        val logMessage = "$logDate $priority/${tag ?: defaultTag}: $message"
                        printWriter.println(logMessage);
                        printWriter.flush() // 确保数据被写入文件系统
                        printWriter.close()
                    } catch (e: IOException) {
                        Log.e("FileLog", "Error opening log file", e)
                    }
                }
            })
        }
    }
}