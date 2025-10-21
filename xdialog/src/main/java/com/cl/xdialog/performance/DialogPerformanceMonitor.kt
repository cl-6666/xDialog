package com.cl.xdialog.performance

import android.os.SystemClock
import android.util.Log
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

/**
 * Dialog性能监控器
 * 用于监控Dialog的创建、显示、关闭等操作的性能
 */
object DialogPerformanceMonitor {

    private const val TAG = "DialogPerformance"
    private var isEnabled = false
    
    // 性能统计数据
    private val createTimeMap = ConcurrentHashMap<String, Long>()
    private val showTimeMap = ConcurrentHashMap<String, Long>()
    private val bindTimeMap = ConcurrentHashMap<String, Long>()
    
    // 统计计数器
    private val totalCreateCount = AtomicLong(0)
    private val totalShowCount = AtomicLong(0)
    private val totalDismissCount = AtomicLong(0)
    
    // 性能阈值（毫秒）
    private var createTimeThreshold = 100L
    private var showTimeThreshold = 200L
    private var bindTimeThreshold = 50L

    /**
     * 启用性能监控
     */
    fun enable(enabled: Boolean = true) {
        isEnabled = enabled
        if (enabled) {
            log("Performance monitoring enabled")
        }
    }

    /**
     * 设置性能阈值
     */
    fun setThresholds(
        createThreshold: Long = 100L,
        showThreshold: Long = 200L,
        bindThreshold: Long = 50L
    ) {
        createTimeThreshold = createThreshold
        showTimeThreshold = showThreshold
        bindTimeThreshold = bindThreshold
    }

    /**
     * 开始监控Dialog创建
     */
    fun startCreate(tag: String) {
        if (!isEnabled) return
        createTimeMap[tag] = SystemClock.elapsedRealtime()
    }

    /**
     * 结束监控Dialog创建
     */
    fun endCreate(tag: String) {
        if (!isEnabled) return
        val startTime = createTimeMap.remove(tag) ?: return
        val duration = SystemClock.elapsedRealtime() - startTime
        totalCreateCount.incrementAndGet()
        
        if (duration > createTimeThreshold) {
            log("Dialog create time exceeded threshold: $tag took ${duration}ms")
        }
        
        logPerformance("CREATE", tag, duration)
    }

    /**
     * 开始监控Dialog显示
     */
    fun startShow(tag: String) {
        if (!isEnabled) return
        showTimeMap[tag] = SystemClock.elapsedRealtime()
    }

    /**
     * 结束监控Dialog显示
     */
    fun endShow(tag: String) {
        if (!isEnabled) return
        val startTime = showTimeMap.remove(tag) ?: return
        val duration = SystemClock.elapsedRealtime() - startTime
        totalShowCount.incrementAndGet()
        
        if (duration > showTimeThreshold) {
            log("Dialog show time exceeded threshold: $tag took ${duration}ms")
        }
        
        logPerformance("SHOW", tag, duration)
    }

    /**
     * 开始监控View绑定
     */
    fun startBind(tag: String) {
        if (!isEnabled) return
        bindTimeMap[tag] = SystemClock.elapsedRealtime()
    }

    /**
     * 结束监控View绑定
     */
    fun endBind(tag: String) {
        if (!isEnabled) return
        val startTime = bindTimeMap.remove(tag) ?: return
        val duration = SystemClock.elapsedRealtime() - startTime
        
        if (duration > bindTimeThreshold) {
            log("Dialog bind time exceeded threshold: $tag took ${duration}ms")
        }
        
        logPerformance("BIND", tag, duration)
    }

    /**
     * 记录Dialog关闭
     */
    fun recordDismiss(tag: String) {
        if (!isEnabled) return
        totalDismissCount.incrementAndGet()
        logPerformance("DISMISS", tag, 0)
    }

    /**
     * 记录内存使用情况
     */
    fun recordMemoryUsage(tag: String) {
        if (!isEnabled) return
        
        val runtime = Runtime.getRuntime()
        val usedMemory = runtime.totalMemory() - runtime.freeMemory()
        val maxMemory = runtime.maxMemory()
        val memoryPercent = (usedMemory * 100 / maxMemory).toInt()
        
        log("Memory usage for $tag: ${usedMemory / 1024 / 1024}MB / ${maxMemory / 1024 / 1024}MB ($memoryPercent%)")
        
        if (memoryPercent > 80) {
            log("WARNING: High memory usage detected: $memoryPercent%")
        }
    }

    /**
     * 获取性能统计报告
     */
    fun getPerformanceReport(): PerformanceReport {
        return PerformanceReport(
            totalCreateCount = totalCreateCount.get(),
            totalShowCount = totalShowCount.get(),
            totalDismissCount = totalDismissCount.get(),
            averageCreateTime = calculateAverageTime(createTimeMap),
            averageShowTime = calculateAverageTime(showTimeMap),
            averageBindTime = calculateAverageTime(bindTimeMap)
        )
    }

    /**
     * 重置统计数据
     */
    fun reset() {
        createTimeMap.clear()
        showTimeMap.clear()
        bindTimeMap.clear()
        totalCreateCount.set(0)
        totalShowCount.set(0)
        totalDismissCount.set(0)
        log("Performance statistics reset")
    }

    /**
     * 计算平均时间
     */
    private fun calculateAverageTime(timeMap: ConcurrentHashMap<String, Long>): Long {
        if (timeMap.isEmpty()) return 0
        val currentTime = SystemClock.elapsedRealtime()
        val totalTime = timeMap.values.sumOf { currentTime - it }
        return totalTime / timeMap.size
    }

    /**
     * 记录性能日志
     */
    private fun logPerformance(operation: String, tag: String, duration: Long) {
        if (duration > 0) {
            log("$operation: $tag completed in ${duration}ms")
        } else {
            log("$operation: $tag")
        }
    }

    /**
     * 输出日志
     */
    private fun log(message: String) {
        if (isEnabled) {
            Log.d(TAG, message)
        }
    }

    /**
     * 性能报告数据类
     */
    data class PerformanceReport(
        val totalCreateCount: Long,
        val totalShowCount: Long,
        val totalDismissCount: Long,
        val averageCreateTime: Long,
        val averageShowTime: Long,
        val averageBindTime: Long
    ) {
        override fun toString(): String {
            return """
                Dialog Performance Report:
                - Total Created: $totalCreateCount
                - Total Shown: $totalShowCount
                - Total Dismissed: $totalDismissCount
                - Average Create Time: ${averageCreateTime}ms
                - Average Show Time: ${averageShowTime}ms
                - Average Bind Time: ${averageBindTime}ms
            """.trimIndent()
        }
    }

    /**
     * 性能监控配置
     */
    object Config {
        /**
         * 是否在Debug模式下自动启用
         */
        var autoEnableInDebug = true

        /**
         * 是否记录详细的性能日志
         */
        var verboseLogging = false

        /**
         * 性能报告输出间隔（毫秒）
         */
        var reportInterval = 60000L // 1分钟
    }

    /**
     * 开始定期输出性能报告
     */
    fun startPeriodicReporting() {
        if (!isEnabled) return
        
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            val report = getPerformanceReport()
            log(report.toString())
            startPeriodicReporting() // 递归调用
        }, Config.reportInterval)
    }
}