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

    // Em vez de herdar do RecyclerView.Adapter clássico, usamos o ListAdapter junto com DiffUtil.
    // A vantagem técnica é enorme: quando adicionamos uma pasta nova, ele não recarrega a lista inteira,
    // ele calcula a diferença em background e faz a animação apenas no item que entrou ou saiu.
    class FolderDiffCallback : DiffUtil.ItemCallback<Folder>() {

        // Verifica se é o mesmo objeto (pela chave primária)
        override fun areItemsTheSame(oldItem: Folder, newItem: Folder) = oldItem.id == newItem.id

        // Verifica se o conteúdo do objeto mudou (ex: se o usuário renomeou a pasta)
        override fun areContentsTheSame(oldItem: Folder, newItem: Folder) = oldItem == newItem
    }
}