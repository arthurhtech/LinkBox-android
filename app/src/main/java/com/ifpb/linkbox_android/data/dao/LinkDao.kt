package com.ifpb.linkbox_android.data.dao

import androidx.room.*
import com.ifpb.linkbox_android.data.model.Link
import kotlinx.coroutines.flow.Flow

@Dao
interface LinkDao {
    // Retornar um 'Flow' é o que torna o nosso app reativo.
    // Em vez de retornar uma lista estática, o Room cria um canal de comunicação.
    // Se um novo link for inserido nesta pasta, o banco avisa o Flow, que avisa a tela para se atualizar sozinha.
    @Query("SELECT * FROM links WHERE folderId = :folderId ORDER BY id DESC")
    fun getLinksByFolder(folderId: Long): Flow<List<Link>>

    // Operações de escrita (Insert, Update, Delete) usam o modificador 'suspend'.
    // Isso garante que essas funções só possam ser chamadas dentro de uma Corrotina.
    // É uma trava de segurança da arquitetura para impedir que o banco de dados trave a interface do usuário.
    @Query("SELECT * FROM links WHERE folderId = :folderId AND title LIKE '%' || :query || '%' ORDER BY id DESC")
    fun searchLinksByFolder(folderId: Long, query: String): Flow<List<Link>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLink(link: Link): Long

    @Update
    suspend fun updateLink(link: Link)

    @Delete
    suspend fun deleteLink(link: Link)
}