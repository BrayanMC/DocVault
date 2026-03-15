package com.docvault.feature.detail.presentation.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.docvault.core.common.extensions.toFormattedDate
import com.docvault.domain.model.AccessLog
import com.docvault.feature.detail.databinding.ItemAccessLogBinding

class AccessLogAdapter : ListAdapter<AccessLog, AccessLogAdapter.AccessLogViewHolder>(
    AccessLogDiffCallback(),
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): AccessLogViewHolder {
        val binding =
            ItemAccessLogBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return AccessLogViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: AccessLogViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    inner class AccessLogViewHolder(
        private val binding: ItemAccessLogBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(accessLog: AccessLog) {
            binding.tvAccessDate.text = accessLog.accessedAt.toFormattedDate()
        }
    }

    class AccessLogDiffCallback : DiffUtil.ItemCallback<AccessLog>() {
        override fun areItemsTheSame(
            oldItem: AccessLog,
            newItem: AccessLog,
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: AccessLog,
            newItem: AccessLog,
        ): Boolean = oldItem == newItem
    }
}
