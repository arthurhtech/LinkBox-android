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

// O ViewModel sobrevive a mudanças de configuração (como girar a tela).
// Ele recebe o Repository no construtor para não acessar o DAO diretamente (desacoplamento).
class LinkViewModel(private val repository: LinkRepository) : ViewModel() {

    // Guarda o ID da pasta atual. Sempre que esse ID mudar, a lista de links é refeita automaticamente.
    private val _currentFolderId = MutableStateFlow(-1L)
    // flatMapLatest: Fica escutando o ID da pasta. Se o usuário trocar de pasta rapidamente,
    // ele cancela a busca da pasta antiga e foca só na nova.
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
        // stateIn converte o Flow do Room para StateFlow.
        // O WhileSubscribed(5000) é uma otimização: a busca no banco é pausada
        // 5 segundos depois que o app vai para o background, economizando bateria.
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

    // Usamos viewModelScope.launch para rodar a inserção de forma assíncrona.
    // Assim o app não congela enquanto salva o link no disco.
    fun insert(link: Link) = viewModelScope.launch { repository.insert(link) }

    fun update(link: Link) = viewModelScope.launch { repository.update(link) }

    fun delete(link: Link) = viewModelScope.launch { repository.delete(link) }
}