package com.docvault.core.designsystem.components

import android.content.Context
import android.util.AttributeSet
import com.docvault.core.designsystem.R
import com.google.android.material.button.MaterialButton

class DocVaultButton
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = com.google.android.material.R.attr.materialButtonStyle,
    ) : MaterialButton(context, attrs, defStyleAttr) {
        init {
            minHeight = resources.getDimensionPixelSize(R.dimen.button_height)
            isAllCaps = false
        }
    }
