package com.ifpb.linkbox_android.data.repository

import com.ifpb.linkbox_android.data.dao.LinkDao
import com.ifpb.linkbox_android.data.model.Link
import kotlinx.coroutines.flow.Flow

class LinkRepository(private val linkDao: LinkDao) {

    fun getLinksByFolder(folderId: Long): Flow<List<Link>> =
        linkDao.getLinksByFolder(folderId)

    fun searchLinks(folderId: Long, query: String): Flow<List<Link>> =
        linkDao.searchLinksByFolder(folderId, query)

    suspend fun insert(link: Link) = linkDao.insertLink(link)

    suspend fun update(link: Link) = linkDao.updateLink(link)

    suspend fun delete(link: Link) = linkDao.deleteLink(link)
}