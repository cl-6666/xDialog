package com.cl.xdialog

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.cl.xdialog.choose.OnWheelChangedListener
import com.cl.xdialog.choose.Wheel3DView
import com.cl.xdialog.choose.WheelView
import java.util.*

/**
 * 日期选择弹窗
 * 基于XDialogOptimized架构，集成Wheel3DView组件
 * 
 * @author cl
 * @date 2024/10/21
 */
class XDatePickerDialog : XDialogOptimized() {
    
    private var datePickerConfig = DatePickerConfig()
    private var onDateSelectedListener: OnDateSelectedListener? = null
    
    // 滚轮组件
    private lateinit var wheelYear: Wheel3DView
    private lateinit var wheelMonth: Wheel3DView
    private lateinit var wheelDay: Wheel3DView
    
    // 当前选中的日期
    private var selectedYear: Int = 0
    private var selectedMonth: Int = 0
    private var selectedDay: Int = 0
    
    companion object {
        private const val KEY_DATE_PICKER_CONFIG = "date_picker_config"
        
        /**
         * 创建日期选择弹窗
         */
        @JvmStatic
        fun create(fragmentManager: FragmentManager): Builder {
            return Builder(fragmentManager)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 设置布局
        config.layoutRes = R.layout.x_dialog_date_picker
        
        // 恢复状态
        savedInstanceState?.let { bundle ->
            selectedYear = bundle.getInt("selected_year", datePickerConfig.initialYear)
            selectedMonth = bundle.getInt("selected_month", datePickerConfig.initialMonth)
            selectedDay = bundle.getInt("selected_day", datePickerConfig.initialDay)
        } ?: run {
            // 初始化选中日期
            selectedYear = datePickerConfig.initialYear
            selectedMonth = datePickerConfig.initialMonth
            selectedDay = datePickerConfig.initialDay
        }
        clampSelectedDate()
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initViews(view)
        setupWheels()
        setupButtons(view)
    }
    
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // 保存当前选择的日期
        outState.putInt("selected_year", selectedYear)
        outState.putInt("selected_month", selectedMonth)
        outState.putInt("selected_day", selectedDay)
    }
    
    /**
     * 初始化视图组件
     */
    private fun initViews(view: View) {
        // 标题
        val tvTitle = view.findViewById<TextView>(R.id.tv_title)
        tvTitle.text = datePickerConfig.title
        tvTitle.visibility = if (datePickerConfig.showTitle) View.VISIBLE else View.GONE
        
        // 滚轮组件
        wheelYear = view.findViewById(R.id.wheel_year)
        wheelMonth = view.findViewById(R.id.wheel_month)
        wheelDay = view.findViewById(R.id.wheel_day)
        
        // 应用主题颜色
        datePickerConfig.primaryColor?.let { color ->
            wheelYear.selectedTextColor = color
            wheelMonth.selectedTextColor = color
            wheelDay.selectedTextColor = color
        }
        
        datePickerConfig.textColor?.let { color ->
            wheelYear.textColor = color
            wheelMonth.textColor = color
            wheelDay.textColor = color
        }
    }
    
    /**
     * 设置滚轮数据和监听器
     */
    private fun setupWheels() {
        // 设置年份数据
        setupYearWheel()
        
        // 设置月份数据
        setupMonthWheel()
        
        // 设置日期数据
        setupDayWheel()
        
        // 设置监听器
        wheelYear.onWheelChangedListener = object : OnWheelChangedListener {
            override fun onChanged(view: WheelView?, oldIndex: Int, newIndex: Int) {
                selectedYear = datePickerConfig.minYear + newIndex
                updateMonthWheel()
            }
        }
        
        wheelMonth.onWheelChangedListener = object : OnWheelChangedListener {
            override fun onChanged(view: WheelView?, oldIndex: Int, newIndex: Int) {
                val minMonth = getMinMonthForYear(selectedYear)
                selectedMonth = minMonth + newIndex
                updateDayWheel()
            }
        }
        
        wheelDay.onWheelChangedListener = object : OnWheelChangedListener {
            override fun onChanged(view: WheelView?, oldIndex: Int, newIndex: Int) {
                val minDay = getMinDayFor(selectedYear, selectedMonth)
                selectedDay = minDay + newIndex
            }
        }
    }
    
    /**
     * 设置年份滚轮
     */
    private fun setupYearWheel() {
        val years = (datePickerConfig.minYear..datePickerConfig.maxYear).map { "${it}年" }.toTypedArray()
        wheelYear.setEntries(*years)
        
        // 设置当前年份
        val currentYearIndex = selectedYear - datePickerConfig.minYear
        wheelYear.setCurrentIndex(currentYearIndex, false)
    }
    
    /**
     * 设置月份滚轮
     */
    private fun setupMonthWheel() {
        val minMonth = getMinMonthForYear(selectedYear)
        val maxMonth = getMaxMonthForYear(selectedYear)
        val months = (minMonth..maxMonth).map { "${it}月" }.toTypedArray()
        wheelMonth.setEntries(*months)
        
        // 设置当前月份
        wheelMonth.setCurrentIndex(selectedMonth - minMonth, false)
    }
    
    /**
     * 设置日期滚轮
     */
    private fun setupDayWheel() {
        val minDay = getMinDayFor(selectedYear, selectedMonth)
        val maxDay = getMaxDayFor(selectedYear, selectedMonth)
        val days = (minDay..maxDay).map { "${it}日" }.toTypedArray()
        wheelDay.setEntries(*days)
        
        // 确保选中的日期不超过当月最大天数
        if (selectedDay < minDay) {
            selectedDay = minDay
        } else if (selectedDay > maxDay) {
            selectedDay = maxDay
        }
        
        // 设置当前日期
        wheelDay.setCurrentIndex(selectedDay - minDay, false)
    }
    
    /**
     * 更新日期滚轮（当年份或月份改变时）
     */
    private fun updateDayWheel() {
        val minDay = getMinDayFor(selectedYear, selectedMonth)
        val maxDay = getMaxDayFor(selectedYear, selectedMonth)
        val days = (minDay..maxDay).map { "${it}日" }.toTypedArray()
        wheelDay.setEntries(*days)
        
        // 确保选中的日期不超过当月最大天数
        if (selectedDay < minDay) {
            selectedDay = minDay
            wheelDay.setCurrentIndex(selectedDay - minDay, true)
        } else if (selectedDay > maxDay) {
            selectedDay = maxDay
            wheelDay.setCurrentIndex(selectedDay - minDay, true)
        }
    }
    
    /**
     * 获取指定年月的天数
     */
    private fun getDaysInMonth(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1)
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    private fun updateMonthWheel() {
        val minMonth = getMinMonthForYear(selectedYear)
        val maxMonth = getMaxMonthForYear(selectedYear)
        if (selectedMonth < minMonth) {
            selectedMonth = minMonth
        } else if (selectedMonth > maxMonth) {
            selectedMonth = maxMonth
        }
        val months = (minMonth..maxMonth).map { "${it}月" }.toTypedArray()
        wheelMonth.setEntries(*months)
        wheelMonth.setCurrentIndex(selectedMonth - minMonth, false)
        updateDayWheel()
    }

    private fun getMinMonthForYear(year: Int): Int {
        return if (year == datePickerConfig.minYear) {
            datePickerConfig.minMonth
        } else {
            1
        }
    }

    private fun getMaxMonthForYear(year: Int): Int {
        return if (year == datePickerConfig.maxYear) {
            datePickerConfig.maxMonth
        } else {
            12
        }
    }

    private fun getMinDayFor(year: Int, month: Int): Int {
        return if (year == datePickerConfig.minYear && month == datePickerConfig.minMonth) {
            datePickerConfig.minDay
        } else {
            1
        }
    }

    private fun getMaxDayFor(year: Int, month: Int): Int {
        val maxDay = getDaysInMonth(year, month)
        return if (year == datePickerConfig.maxYear && month == datePickerConfig.maxMonth) {
            minOf(datePickerConfig.maxDay, maxDay)
        } else {
            maxDay
        }
    }

    private fun clampSelectedDate() {
        selectedYear = selectedYear.coerceIn(datePickerConfig.minYear, datePickerConfig.maxYear)
        val minMonth = getMinMonthForYear(selectedYear)
        val maxMonth = getMaxMonthForYear(selectedYear)
        selectedMonth = selectedMonth.coerceIn(minMonth, maxMonth)
        val minDay = getMinDayFor(selectedYear, selectedMonth)
        val maxDay = getMaxDayFor(selectedYear, selectedMonth)
        selectedDay = selectedDay.coerceIn(minDay, maxDay)
    }
    
    /**
     * 设置按钮事件
     */
    private fun setupButtons(view: View) {
        val btnCancel = view.findViewById<TextView>(R.id.btn_cancel)
        val btnConfirm = view.findViewById<TextView>(R.id.btn_confirm)
        
        btnCancel.text = datePickerConfig.cancelText
        btnConfirm.text = datePickerConfig.confirmText
        
        btnCancel.setOnClickListener {
            dismiss()
        }
        
        btnConfirm.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.set(selectedYear, selectedMonth - 1, selectedDay)
            val selectedDate = calendar.time
            
            onDateSelectedListener?.onDateSelected(selectedDate, selectedYear, selectedMonth, selectedDay)
            dismiss()
        }
    }
    
    /**
     * 日期选择监听器
     */
    interface OnDateSelectedListener {
        fun onDateSelected(date: Date, year: Int, month: Int, day: Int)
    }
    
    /**
     * Builder类，提供链式调用API
     */
    class Builder(private val fragmentManager: FragmentManager) {
        private val config = DatePickerConfig()
        private var onDateSelectedListener: OnDateSelectedListener? = null
        
        /**
         * 设置标题
         */
        fun title(title: String): Builder {
            config.title = title
            return this
        }
        
        /**
         * 设置初始日期
         */
        fun initialDate(year: Int, month: Int, day: Int): Builder {
            val normalized = normalizeDate(year, month, day)
            config.initialYear = normalized[0]
            config.initialMonth = normalized[1]
            config.initialDay = normalized[2]
            return this
        }
        
        /**
         * 设置最小年份
         */
        fun minDate(year: Int, month: Int, day: Int): Builder {
            val normalized = normalizeDate(year, month, day)
            config.minYear = normalized[0]
            config.minMonth = normalized[1]
            config.minDay = normalized[2]
            return this
        }
        
        /**
         * 设置最大年份
         */
        fun maxDate(year: Int, month: Int, day: Int): Builder {
            val normalized = normalizeDate(year, month, day)
            config.maxYear = normalized[0]
            config.maxMonth = normalized[1]
            config.maxDay = normalized[2]
            return this
        }
        
        /**
         * 设置年份范围
         */
        fun yearRange(startYear: Int, endYear: Int): Builder {
            config.minYear = startYear
            config.maxYear = endYear
            return this
        }

        private fun normalizeDate(year: Int, month: Int, day: Int): IntArray {
            val safeMonth = month.coerceIn(1, 12)
            val calendar = Calendar.getInstance()
            calendar.set(year, safeMonth - 1, 1)
            val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            val safeDay = day.coerceIn(1, maxDay)
            return intArrayOf(year, safeMonth, safeDay)
        }
        
        /**
         * 设置确定按钮文本
         */
        fun confirmText(text: String): Builder {
            config.confirmText = text
            return this
        }
        
        /**
         * 设置取消按钮文本
         */
        fun cancelText(text: String): Builder {
            config.cancelText = text
            return this
        }
        
        /**
         * 设置是否显示标题
         */
        fun showTitle(show: Boolean): Builder {
            config.showTitle = show
            return this
        }
        
        /**
         * 设置主题颜色
         */
        fun primaryColor(color: Int): Builder {
            config.primaryColor = color
            return this
        }
        
        /**
         * 设置文本颜色
         */
        fun textColor(color: Int): Builder {
            config.textColor = color
            return this
        }
        
        /**
         * 设置选中文本颜色
         */
        fun selectedTextColor(color: Int): Builder {
            config.selectedTextColor = color
            return this
        }
        
        /**
         * 设置日期选择监听器
         */
        fun onDateSelected(listener: OnDateSelectedListener): Builder {
            onDateSelectedListener = listener
            return this
        }
        
        /**
         * 设置日期选择监听器（简化版本）
         */
        fun onDateSelected(listener: (Date, Int, Int, Int) -> Unit): Builder {
            onDateSelectedListener = object : OnDateSelectedListener {
                override fun onDateSelected(date: Date, year: Int, month: Int, day: Int) {
                    listener(date, year, month, day)
                }
            }
            return this
        }
        
        /**
         * 显示弹窗
         */
        fun show(): XDatePickerDialog {
            val dialog = build()
            XDialogOptimized.showAuto(fragmentManager, dialog, "XDatePickerDialog")
            return dialog
        }
        
        /**
         * 构建弹窗实例
         */
        fun build(): XDatePickerDialog {
            val dialog = XDatePickerDialog()
            dialog.datePickerConfig = config
            dialog.onDateSelectedListener = onDateSelectedListener
            return dialog
        }
    }
}
