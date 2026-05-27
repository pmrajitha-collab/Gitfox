package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "workspaces")
data class Workspace(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String, // "frontend" (React Dashboard), "notebook" (Python Telemetry), "backend" (Rust CLI Tool)
    val repoUrl: String,
    val status: String, // "ready", "provisioning", "idle"
    val containerIp: String,
    val description: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "workspace_files",
    foreignKeys = [
        ForeignKey(
            entity = Workspace::class,
            parentColumns = ["id"],
            childColumns = ["workspaceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workspaceId")]
)
data class WorkspaceFile(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workspaceId: Long,
    val name: String,
    val path: String,
    val content: String,
    val isNotebook: Boolean = false
)

@Entity(
    tableName = "notebook_cells",
    foreignKeys = [
        ForeignKey(
            entity = WorkspaceFile::class,
            parentColumns = ["id"],
            childColumns = ["fileId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("fileId")]
)
data class NotebookCell(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fileId: Long,
    val cellIndex: Int,
    val type: String, // "markdown" or "code"
    val inputCode: String,
    val outputText: String = "",
    val outputJson: String = "", // can store configuration/data points for custom metrics
    val isRunning: Boolean = false,
    val executionTimeMs: Long = 0
)

@Dao
interface GitFoxDao {
    @Query("SELECT * FROM workspaces ORDER BY createdAt DESC")
    fun getAllWorkspaces(): Flow<List<Workspace>>

    @Query("SELECT * FROM workspaces WHERE id = :id")
    suspend fun getWorkspaceById(id: Long): Workspace?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkspace(workspace: Workspace): Long

    @Update
    suspend fun updateWorkspace(workspace: Workspace)

    @Delete
    suspend fun deleteWorkspace(workspace: Workspace)

    @Query("SELECT * FROM workspace_files WHERE workspaceId = :workspaceId")
    fun getFilesForWorkspace(workspaceId: Long): Flow<List<WorkspaceFile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: WorkspaceFile): Long

    @Update
    suspend fun updateFile(file: WorkspaceFile)

    @Query("SELECT * FROM notebook_cells WHERE fileId = :fileId ORDER BY cellIndex ASC")
    fun getCellsForFile(fileId: Long): Flow<List<NotebookCell>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCell(cell: NotebookCell): Long

    @Update
    suspend fun updateCell(cell: NotebookCell)

    @Query("DELETE FROM notebook_cells WHERE fileId = :fileId")
    suspend fun deleteCellsForFile(fileId: Long)
}

@Database(entities = [Workspace::class, WorkspaceFile::class, NotebookCell::class], version = 1, exportSchema = false)
abstract class GitFoxDatabase : RoomDatabase() {
    abstract fun gitFoxDao(): GitFoxDao

    companion object {
        @Volatile
        private var INSTANCE: GitFoxDatabase? = null

        fun getDatabase(context: Context): GitFoxDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GitFoxDatabase::class.java,
                    "gitfox_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
