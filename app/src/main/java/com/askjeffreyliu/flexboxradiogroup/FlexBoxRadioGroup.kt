package com.askjeffreyliu.flexboxradiogroup

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RadioButton
import com.google.android.flexbox.FlexboxLayout

class FlexBoxRadioGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FlexboxLayout(context, attrs, defStyleAttr) {

    interface OnCheckedChangeListener {
        fun onCheckedChanged(group: FlexBoxRadioGroup?, checkedId: Int)
    }

    private var checkedId: Int = View.NO_ID
    private var listener: OnCheckedChangeListener? = null
    private var protectFromCheckedChange = false

    fun setOnCheckedChangeListener(listener: OnCheckedChangeListener?) {
        this.listener = listener
    }

    val checkedRadioButtonId: Int get() = checkedId

    fun check(id: Int) {
        if (id == checkedId) return
        if (checkedId != View.NO_ID) {
            setCheckedStateForView(checkedId, false)
        }
        setCheckedStateForView(id, true)
        setCheckedId(id)
    }

    fun clearCheck() {
        check(View.NO_ID)
    }

    private fun setCheckedId(id: Int) {
        checkedId = id
        listener?.onCheckedChanged(this, checkedId)
    }

    private fun setCheckedStateForView(viewId: Int, checked: Boolean) {
        val view = findViewById<View>(viewId)
        if (view is RadioButton) {
            view.isChecked = checked
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        attachChildListeners()
    }

    private fun attachChildListeners() {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child is RadioButton) {
                child.setOnClickListener { onRadioButtonClicked(child) }
            }
        }
    }

    override fun addView(child: View?, index: Int, params: android.view.ViewGroup.LayoutParams?) {
        super.addView(child, index, params)
        if (child is RadioButton) {
            if (child.isChecked) {
                protectFromCheckedChange = true
                if (checkedId != View.NO_ID) {
                    setCheckedStateForView(checkedId, false)
                }
                protectFromCheckedChange = false
                setCheckedId(child.id)
            }
            child.setOnClickListener { onRadioButtonClicked(child) }
        }
    }

    private fun onRadioButtonClicked(radioButton: RadioButton) {
        if (protectFromCheckedChange) return
        protectFromCheckedChange = true
        if (checkedId != View.NO_ID && checkedId != radioButton.id) {
            setCheckedStateForView(checkedId, false)
        }
        protectFromCheckedChange = false
        setCheckedId(radioButton.id)
    }
}
