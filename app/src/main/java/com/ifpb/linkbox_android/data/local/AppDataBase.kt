package com.ifpb.linkbox_android.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ifpb.linkbox_android.data.dao.FolderDao
import com.ifpb.linkbox_android.data.dao.LinkDao
import com.ifpb.linkbox_android.data.model.Folder
import com.ifpb.linkbox_android.data.model.Link
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Folder::class, Link::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun folderDao(): FolderDao
    abstract fun linkDao(): LinkDao

    companion object {
        // @Volatile garante que qualquer alteração nesta variável seja vista imediatamente por outras threads.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            // Padrão Singleton: Impede que o app abra múltiplas instâncias do banco de dados,
            // o que causaria vazamento de memória e travamentos. Se já existe, retorna; se não, cria.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "linkbox_database"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Callback disparado no momento exato em que o banco é criado pela primeira vez no celular.
        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    // Usamos uma Coroutine (Dispatchers.IO) para inserir a pasta padrão em background,
                    // já que operações de banco de dados não podem rodar na thread principal (UI Thread).
                    scope.launch(Dispatchers.IO) {
                        database.folderDao().insertFolder(Folder(id = 1, name = "Geral"))
                    }
                }
            }
        }
    }
}