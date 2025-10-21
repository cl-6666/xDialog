package com.cl.xdialog

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Calendar
import java.util.Date

/**
 * 日期选择弹窗配置类
 * 用于配置日期选择弹窗的各种属性和行为
 */
data class DatePickerConfig(
    // 弹窗标题
    var title: String = "选择日期",
    
    // 初始年份
    var initialYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    
    // 初始月份 (0-11)
    var initialMonth: Int = Calendar.getInstance().get(Calendar.MONTH),
    
    // 初始日期
    var initialDay: Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
    
    // 最小年份
    var minYear: Int = 1950,
    
    // 最大年份
    var maxYear: Int = 2050,
    
    // 确认按钮文本
    var confirmText: String = "确定",
    
    // 取消按钮文本
    var cancelText: String = "取消",
    
    // 是否显示标题
    var showTitle: Boolean = true,
    
    // 日期格式
    var dateFormat: DateFormat = DateFormat.YEAR_MONTH_DAY,
    
    // 主题颜色
    var primaryColor: Int? = null,
    
    // 文本颜色
    var textColor: Int? = null,
    
    // 选中文本颜色
    var selectedTextColor: Int? = null
) {
    
    /**
     * 日期格式枚举
     */
    enum class DateFormat {
        YEAR_MONTH_DAY,     // 年-月-日
        MONTH_DAY,          // 月-日
        YEAR_MONTH          // 年-月
    }
    
    /**
     * 获取当前年份
     */
    fun getCurrentYear(): Int {
        return initialYear
    }
    
    /**
     * 获取当前月份（1-12）
     */
    fun getCurrentMonth(): Int {
        return initialMonth + 1
    }
    
    /**
     * 获取当前日期（1-31）
     */
    fun getCurrentDay(): Int {
        return initialDay
    }
    
    /**
     * 验证年份是否在有效范围内
     */
    fun isYearValid(year: Int): Boolean {
        return year in minYear..maxYear
    }
    
    /**
     * 获取指定年月的天数
     */
    fun getDaysInMonth(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1)
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }
}