package com.docvault.core.designsystem.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.docvault.core.designsystem.databinding.ViewErrorBinding

class DocVaultErrorView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : ConstraintLayout(context, attrs, defStyleAttr) {
        private val binding = ViewErrorBinding.inflate(LayoutInflater.from(context), this, true)

        var message: String = ""
            set(value) {
                field = value
                binding.tvErrorMessage.text = value
            }

        var onRetryClick: (() -> Unit)? = null
            set(value) {
                field = value
                binding.btnRetry.setOnClickListener { value?.invoke() }
            }
    }
