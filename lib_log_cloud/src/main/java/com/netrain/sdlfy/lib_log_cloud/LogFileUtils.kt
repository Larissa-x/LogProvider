package com.netrain.sdlfy.lib_log_cloud

import android.content.Context
import android.util.Log
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


object LogFileUtils {

    private const val TAG = "LogFileUtils"
    private const val LOG_RETENTION_DAYS = 7
    private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    /**
     * 获取日志文件目录下所有文件,业务上传之后删除
     * @param context 上下文
     * @return 日志文件列表，如果目录不存在或没有文件则返回空列表
     */
    fun getLogFiles(context: Context): List<File> {
        val logDir = context.getExternalFilesDir(TimberProvider.LOG_FOLDER_NAME) ?: return emptyList()
        if (!logDir.exists() || !logDir.isDirectory) {
            return emptyList()
        }
        
        return logDir.listFiles()?.filter { it.isFile }?.toList() ?: emptyList()
    }
    
    /**
     * 获取日志文件目录
     * @param context 上下文
     * @return 日志文件目录，如果不存在则返回null
     */
    fun getLogDirectory(context: Context): File? {
        val logDir = context.getExternalFilesDir(TimberProvider.LOG_FOLDER_NAME)
        return if (logDir?.exists() == true && logDir.isDirectory) logDir else null
    }

    /**
     * 清理超过指定天数的日志文件
     * @param context 上下文
     * @param days 保留的天数，默认为7天
     * @return 被删除的文件数量
     */
    fun cleanupOldLogs(context: Context, days: Int = LOG_RETENTION_DAYS): Int {
        val logDir = getLogDirectory(context) ?: return 0
        val logFiles = getLogFiles(context)
        if (logFiles.isEmpty()) return 0
        
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -days) // 计算days天前的日期
        val cutoffDate = calendar.time
        
        var deletedCount = 0
        
        logFiles.forEach { file ->
            try {
                // 从文件名解析日期，文件名格式为：yyyy-MM-dd.log
                val fileName = file.name
                if (fileName.endsWith(".log")) {
                    val dateStr = fileName.substring(0, fileName.length - 4) // 去掉.log后缀
                    val fileDate = DATE_FORMAT.parse(dateStr)
                    
                    if (fileDate != null && fileDate.before(cutoffDate)) {
                        if (file.delete()) {
                            deletedCount++
                            Log.d(TAG, "已删除过期日志文件: ${file.name}")
                        } else {
                            Log.e(TAG, "删除日志文件失败: ${file.name}")
                        }
                    }
                }
            } catch (e: ParseException) {
                Log.e(TAG, "解析日志文件日期失败: ${file.name}", e)
            } catch (e: Exception) {
                Log.e(TAG, "处理日志文件时出错: ${file.name}", e)
            }
        }
        
        return deletedCount
    }

    /**
     * 删除指定名称的日志文件
     * @param context 上下文
     * @param fileName 要删除的文件名，如果不包含.log后缀，会自动添加
     * @return 是否成功删除文件
     */
    fun deleteLogFile(context: Context, fileName: String): Boolean {
        val logDir = getLogDirectory(context) ?: return false
        
        // 确保文件名有.log后缀
        val targetFileName = if (fileName.endsWith(".log")) fileName else "$fileName.log"
        
        val file = File(logDir, targetFileName)
        if (!file.exists() || !file.isFile) {
            Log.w(TAG, "要删除的文件不存在: $targetFileName")
            return false
        }
        
        return try {
            val result = file.delete()
            if (result) {
                Log.d(TAG, "成功删除日志文件: $targetFileName")
            } else {
                Log.e(TAG, "删除日志文件失败: $targetFileName")
            }
            result
        } catch (e: Exception) {
            Log.e(TAG, "删除日志文件时出错: $targetFileName", e)
            false
        }
    }

    /**
     * 删除多个指定名称的日志文件
     * @param context 上下文
     * @param fileNames 要删除的文件名列表
     * @return 成功删除的文件数量
     */
    fun deleteLogFiles(context: Context, fileNames: List<String>): Int {
        if (fileNames.isEmpty()) return 0
        
        var deletedCount = 0
        fileNames.forEach { fileName ->
            if (deleteLogFile(context, fileName)) {
                deletedCount++
            }
        }
        
        return deletedCount
    }
}