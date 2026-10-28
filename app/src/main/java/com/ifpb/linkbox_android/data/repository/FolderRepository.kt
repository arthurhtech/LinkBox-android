package com.ifpb.linkbox_android.data.repository

import com.ifpb.linkbox_android.data.dao.FolderDao
import com.ifpb.linkbox_android.data.model.Folder
import kotlinx.coroutines.flow.Flow

// O uso do padrão Repository é uma boa prática da Clean Architecture recomendada pelo Google.
// Ele serve como um mediador. A interface gráfica não precisa saber se os dados
// vêm do SQLite (Room) ou de uma API na web. O Repositório abstrai essa complexidade.
class FolderRepository(private val folderDao: FolderDao) {

    // Expõe a fonte de dados (Single Source of Truth) para o ViewModel.
    val allFolders: Flow<List<Folder>> = folderDao.getAllFolders()

    // Encapsula as chamadas do DAO. Se no futuro o app precisar sincronizar com a nuvem,
    // a lógica de salvar local e enviar para a API ficaria isolada aqui, sem quebrar as outras telas.
    suspend fun insert(folder: Folder) = folderDao.insertFolder(folder)

    suspend fun update(folder: Folder) = folderDao.updateFolder(folder)

    suspend fun delete(folder: Folder) = folderDao.deleteFolder(folder)
}