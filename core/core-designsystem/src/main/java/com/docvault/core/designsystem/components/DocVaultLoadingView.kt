package com.docvault.core.designsystem.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.docvault.core.designsystem.databinding.ViewLoadingBinding

class DocVaultLoadingView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : ConstraintLayout(context, attrs, defStyleAttr) {
        init {
            ViewLoadingBinding.inflate(LayoutInflater.from(context), this, true)
        }
    }
