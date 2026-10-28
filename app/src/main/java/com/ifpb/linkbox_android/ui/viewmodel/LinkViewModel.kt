package com.ifpb.linkbox_android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifpb.linkbox_android.data.model.Link
import com.ifpb.linkbox_android.data.repository.LinkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LinkViewModel(private val repository: LinkRepository) : ViewModel() {

    private val _currentFolderId = MutableStateFlow(-1L)
    private val _searchQuery = MutableStateFlow("")

    // Combina o ID da pasta e a pesquisa para emitir a lista de links correspondente
    val links: StateFlow<List<Link>> = kotlinx.coroutines.flow.combine(
        _currentFolderId, _searchQuery
    ) { folderId, query ->
        Pair(folderId, query)
    }.flatMapLatest { (folderId, query) ->
        if (query.isBlank()) {
            repository.getLinksByFolder(folderId)
        } else {
            repository.searchLinks(folderId, query)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setFolderId(folderId: Long) {
        _currentFolderId.value = folderId
    }

    fun search(query: String) {
        _searchQuery.value = query
    }

    fun insert(link: Link) = viewModelScope.launch { repository.insert(link) }

    fun update(link: Link) = viewModelScope.launch { repository.update(link) }

    fun delete(link: Link) = viewModelScope.launch { repository.delete(link) }
}