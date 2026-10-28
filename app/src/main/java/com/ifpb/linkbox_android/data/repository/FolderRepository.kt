package com.ifpb.linkbox_android.data.repository

import com.ifpb.linkbox_android.data.dao.FolderDao
import com.ifpb.linkbox_android.data.model.Folder
import kotlinx.coroutines.flow.Flow

class FolderRepository(private val folderDao: FolderDao) {

    val allFolders: Flow<List<Folder>> = folderDao.getAllFolders()

    suspend fun insert(folder: Folder) = folderDao.insertFolder(folder)

    suspend fun update(folder: Folder) = folderDao.updateFolder(folder)

    suspend fun delete(folder: Folder) = folderDao.deleteFolder(folder)
}