package com.ifpb.linkbox_android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ifpb.linkbox_android.data.repository.FolderRepository
import com.ifpb.linkbox_android.data.repository.LinkRepository

class AppViewModelFactory(
    private val folderRepository: FolderRepository,
    private val linkRepository: LinkRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FolderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FolderViewModel(folderRepository) as T
        }
        if (modelClass.isAssignableFrom(LinkViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LinkViewModel(linkRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}