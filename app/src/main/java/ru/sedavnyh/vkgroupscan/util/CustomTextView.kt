package ru.sedavnyh.vkgroupscan.util

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import ru.sedavnyh.vkgroupscan.R

class CustomTextView : AppCompatTextView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun fixTextSelection() {
        setTextIsSelectable(false)
            post { setTextIsSelectable(true) }
    }
}