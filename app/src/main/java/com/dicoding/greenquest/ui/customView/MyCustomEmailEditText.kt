package com.dicoding.greenquest.ui.customView

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class MyCustomEmailEditText  @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
            setError("Masukkan email yang valid", null)
        } else {
            error = null
        }
    }

}