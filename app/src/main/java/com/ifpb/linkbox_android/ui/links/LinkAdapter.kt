package com.ifpb.linkbox_android.ui.links

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ifpb.linkbox_android.data.model.Link
import com.ifpb.linkbox_android.databinding.ItemLinkBinding

class LinkAdapter(
    private val onLinkClick: (Link) -> Unit,
    private val onMenuClick: (View, Link) -> Unit
) : ListAdapter<Link, LinkAdapter.LinkViewHolder>(LinkDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinkViewHolder {
        val binding = ItemLinkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LinkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LinkViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class LinkViewHolder(private val binding: ItemLinkBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(link: Link) {
            binding.tvTitle.text = link.title
            binding.tvUrl.text = link.url

            if (!link.description.isNullOrBlank()) {
                binding.tvDescription.visibility = View.VISIBLE
                binding.tvDescription.text = link.description
            } else {
                binding.tvDescription.visibility = View.GONE
            }

            binding.root.setOnClickListener { onLinkClick(link) }
            binding.ivMenu.setOnClickListener { onMenuClick(it, link) }
        }
    }

    class LinkDiffCallback : DiffUtil.ItemCallback<Link>() {
        override fun areItemsTheSame(oldItem: Link, newItem: Link) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Link, newItem: Link) = oldItem == newItem
    }
}