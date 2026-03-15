package com.docvault.core.designsystem.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.docvault.core.designsystem.databinding.ViewToolbarBinding

class DocVaultToolbar
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : ConstraintLayout(context, attrs, defStyleAttr) {
        private val binding = ViewToolbarBinding.inflate(LayoutInflater.from(context), this, true)

        var title: String = ""
            set(value) {
                field = value
                binding.toolbar.title = value
            }

        var onNavigationClick: (() -> Unit)? = null
            set(value) {
                field = value
                binding.toolbar.setNavigationOnClickListener { value?.invoke() }
            }

        @SuppressLint("UseCompatLoadingForDrawables")
        fun showNavigationIcon(show: Boolean) {
            binding.toolbar.navigationIcon =
                if (show) {
                    context.getDrawable(com.docvault.core.designsystem.R.drawable.ic_arrow_back)
                } else {
                    null
                }
        }
    }
