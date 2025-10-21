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
                updateDayWheel()
            }
        }
        
        wheelMonth.onWheelChangedListener = object : OnWheelChangedListener {
            override fun onChanged(view: WheelView?, oldIndex: Int, newIndex: Int) {
                selectedMonth = newIndex + 1
                updateDayWheel()
            }
        }
        
        wheelDay.onWheelChangedListener = object : OnWheelChangedListener {
            override fun onChanged(view: WheelView?, oldIndex: Int, newIndex: Int) {
                selectedDay = newIndex + 1
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
        val months = (1..12).map { "${it}月" }.toTypedArray()
        wheelMonth.setEntries(*months)
        
        // 设置当前月份
        wheelMonth.setCurrentIndex(selectedMonth - 1, false)
    }
    
    /**
     * 设置日期滚轮
     */
    private fun setupDayWheel() {
        val daysInMonth = getDaysInMonth(selectedYear, selectedMonth)
        val days = (1..daysInMonth).map { "${it}日" }.toTypedArray()
        wheelDay.setEntries(*days)
        
        // 确保选中的日期不超过当月最大天数
        if (selectedDay > daysInMonth) {
            selectedDay = daysInMonth
        }
        
        // 设置当前日期
        wheelDay.setCurrentIndex(selectedDay - 1, false)
    }
    
    /**
     * 更新日期滚轮（当年份或月份改变时）
     */
    private fun updateDayWheel() {
        val daysInMonth = getDaysInMonth(selectedYear, selectedMonth)
        val days = (1..daysInMonth).map { "${it}日" }.toTypedArray()
        wheelDay.setEntries(*days)
        
        // 确保选中的日期不超过当月最大天数
        if (selectedDay > daysInMonth) {
            selectedDay = daysInMonth
            wheelDay.setCurrentIndex(selectedDay - 1, true)
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
            config.initialYear = year
            config.initialMonth = month
            config.initialDay = day
            return this
        }
        
        /**
         * 设置最小年份
         */
        fun minDate(year: Int, month: Int, day: Int): Builder {
            config.minYear = year
            return this
        }
        
        /**
         * 设置最大年份
         */
        fun maxDate(year: Int, month: Int, day: Int): Builder {
            config.maxYear = year
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
            dialog.show(fragmentManager, "XDatePickerDialog")
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