package com.docvault.core.designsystem.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.docvault.core.designsystem.databinding.ViewEmptyStateBinding

class DocVaultEmptyStateView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
    ) : ConstraintLayout(context, attrs, defStyleAttr) {
        private val binding = ViewEmptyStateBinding.inflate(LayoutInflater.from(context), this, true)

        var title: String = ""
            set(value) {
                field = value
                binding.tvEmptyTitle.text = value
            }

        var message: String = ""
            set(value) {
                field = value
                binding.tvEmptyMessage.text = value
            }

        fun setIcon(resId: Int) {
            binding.ivEmptyIcon.setImageResource(resId)
        }
    }
