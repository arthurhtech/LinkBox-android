package com.ifpb.linkbox_android.data.dao

import androidx.room.*
import com.ifpb.linkbox_android.data.model.Link
import kotlinx.coroutines.flow.Flow

@Dao
interface LinkDao {
    @Query("SELECT * FROM links WHERE folderId = :folderId ORDER BY id DESC")
    fun getLinksByFolder(folderId: Long): Flow<List<Link>>

    @Query("SELECT * FROM links WHERE folderId = :folderId AND title LIKE '%' || :query || '%' ORDER BY id DESC")
    fun searchLinksByFolder(folderId: Long, query: String): Flow<List<Link>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLink(link: Link): Long

    @Update
    suspend fun updateLink(link: Link)

    @Delete
    suspend fun deleteLink(link: Link)
}