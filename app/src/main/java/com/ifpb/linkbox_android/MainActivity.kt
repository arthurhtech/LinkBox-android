package com.ifpb.linkbox_android

import android.app.Application
import com.ifpb.linkbox_android.data.local.AppDatabase
import com.ifpb.linkbox_android.data.repository.FolderRepository
import com.ifpb.linkbox_android.data.repository.LinkRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MainActivity : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    // Inicialização preguiçosa (lazy) garante que o banco só seja criado quando necessário
    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val folderRepository by lazy { FolderRepository(database.folderDao()) }
    val linkRepository by lazy { LinkRepository(database.linkDao()) }
}