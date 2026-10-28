package com.ifpb.linkbox_android.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// A anotação @Entity transforma esta classe Kotlin em uma tabela SQLite.
// Defini a chave estrangeira (ForeignKey) para criar a relação 1:N entre Pasta e Link.
@Entity(
    tableName = "links",
    foreignKeys = [
        ForeignKey(
            entity = Folder::class,
            parentColumns = ["id"],
            childColumns = ["folderId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    // O Index na chave estrangeira otimiza a velocidade das consultas no banco.
    indices = [Index(value = ["folderId"])]
)
data class Link(
    // O autoGenerate = true delega ao SQLite a responsabilidade de incrementar o ID.
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val url: String,
    // O uso de 'String?' (anulável) mapeia para uma coluna que aceita valores NULL no banco,
    // já que a descrição é um campo opcional no nosso formulário.
    val description: String? = null,
    val folderId: Long
)