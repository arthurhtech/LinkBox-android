package com.ifpb.linkbox_android

import android.app.Application
import com.ifpb.linkbox_android.data.local.AppDatabase
import com.ifpb.linkbox_android.data.repository.FolderRepository
import com.ifpb.linkbox_android.data.repository.LinkRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

// Esta classe roda antes de qualquer tela do app aparecer.
class LinkBoxApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    // O uso do 'by lazy' é para a performance do app.
    // O banco de dados e os repositórios não são carregados na memória
    // no instante em que o usuário clica no ícone do app, mas sim apenas quando a primeira
    // tela realmente solicitar esses dados. Isso reduz o tempo de inicialização do aplicativo (Cold Start).
    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val folderRepository by lazy { FolderRepository(database.folderDao()) }
    val linkRepository by lazy { LinkRepository(database.linkDao()) }
}