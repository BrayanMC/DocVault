package com.docvault.feature.documents.presentation.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.docvault.domain.model.Document
import com.docvault.domain.model.DocumentType
import com.docvault.feature.documents.databinding.ItemDocumentBinding

class DocumentsAdapter(
    private val onDocumentClick: (Document) -> Unit,
    private val onDocumentLongClick: (Document) -> Unit,
) : ListAdapter<Document, DocumentsAdapter.DocumentViewHolder>(DocumentDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): DocumentViewHolder {
        val binding =
            ItemDocumentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            )
        return DocumentViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: DocumentViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    inner class DocumentViewHolder(
        private val binding: ItemDocumentBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(document: Document) {
            binding.tvDocumentName.text = document.name
            binding.tvDocumentType.text = document.type.name
            binding.ivDocumentIcon.setImageResource(
                when (document.type) {
                    DocumentType.PDF -> android.R.drawable.ic_menu_agenda
                    DocumentType.IMAGE -> android.R.drawable.ic_menu_gallery
                },
            )
            binding.root.setOnClickListener { onDocumentClick(document) }
            binding.root.setOnLongClickListener {
                onDocumentLongClick(document)
                true
            }
        }
    }

    class DocumentDiffCallback : DiffUtil.ItemCallback<Document>() {
        override fun areItemsTheSame(
            oldItem: Document,
            newItem: Document,
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: Document,
            newItem: Document,
        ): Boolean = oldItem == newItem
    }
}
