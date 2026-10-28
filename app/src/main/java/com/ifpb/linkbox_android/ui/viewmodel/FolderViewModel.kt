package com.ifpb.linkbox_android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifpb.linkbox_android.data.model.Folder
import com.ifpb.linkbox_android.data.repository.FolderRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FolderViewModel(private val repository: FolderRepository) : ViewModel() {

    val allFolders: StateFlow<List<Folder>> = repository.allFolders
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun insert(folder: Folder) = viewModelScope.launch { repository.insert(folder) }

    fun update(folder: Folder) = viewModelScope.launch { repository.update(folder) }

    fun delete(folder: Folder) = viewModelScope.launch { repository.delete(folder) }
}