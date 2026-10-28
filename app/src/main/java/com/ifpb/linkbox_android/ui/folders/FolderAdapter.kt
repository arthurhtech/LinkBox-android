package com.ifpb.linkbox_android.ui.folders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ifpb.linkbox_android.data.model.Folder
import com.ifpb.linkbox_android.databinding.ItemFolderBinding

class FolderAdapter(
    private val onFolderClick: (Folder) -> Unit,
    private val onMenuClick: (View, Folder) -> Unit
) : ListAdapter<Folder, FolderAdapter.FolderViewHolder>(FolderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val binding = ItemFolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FolderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FolderViewHolder(private val binding: ItemFolderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(folder: Folder) {
            binding.tvFolderName.text = folder.name

            // Clique no corpo do item (abrir pasta)
            binding.root.setOnClickListener {
                onFolderClick(folder)
            }

            // Clique no menu de 3 pontinhos (editar/excluir)
            binding.ivMenu.setOnClickListener {
                onMenuClick(it, folder)
            }
        }
    }

    class FolderDiffCallback : DiffUtil.ItemCallback<Folder>() {
        override fun areItemsTheSame(oldItem: Folder, newItem: Folder) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Folder, newItem: Folder) = oldItem == newItem
    }
}