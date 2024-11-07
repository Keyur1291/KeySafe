import android.content.Context
import android.database.sqlite.SQLiteException
import android.util.Log
import net.sqlcipher.database.SQLiteDatabase
import java.io.File

object SQLCipherUtils {

    enum class State {
        DOES_NOT_EXIST, UNENCRYPTED, ENCRYPTED
    }

    fun getDatabaseState(context: Context, dbName: String?): State {
        SQLiteDatabase.loadLibs(context)
        return getDatabaseState(context.getDatabasePath(dbName))
    }

    private fun getDatabaseState(dbPath: File): State {
        if (!dbPath.exists()) return State.DOES_NOT_EXIST

        return try {
            SQLiteDatabase.openDatabase(
                dbPath.absolutePath, "", null,
                SQLiteDatabase.OPEN_READONLY, null, null
            ).use { db ->
                db.version // Accessing version to check if the database is encrypted
                State.UNENCRYPTED
            }
        } catch (e: SQLiteException) {
            State.ENCRYPTED
        }
    }

    fun migrateToEncryptedDatabase(
        dataBaseName: String, context: Context, password: String
    ) {
        // Obtain the paths for the original and temporary databases
        val databasePath = context.getDatabasePath(dataBaseName).path
        val temporaryDatabasePath = context.getDatabasePath("${dataBaseName}temp").absolutePath
        val originalFile = File(databasePath)
        // Check if the original database file exists
        if (originalFile.exists()) {
            // Create a reference to the temporary database file
            val newFile = File(temporaryDatabasePath)
            // Open the original database and execute SQL commands to encrypt it
            var database = getCurrentSqliteDatabase(databasePath)
            runCatching {
                database.rawExecSQL("ATTACH DATABASE '$temporaryDatabasePath' AS encrypted KEY '$password';")
                database.rawExecSQL("SELECT sqlcipher_export('encrypted')")
                database.rawExecSQL("DETACH DATABASE encrypted;")
                // Retrieve the database version
                val version = database.version
                // Close the database connection
                database.close()
                // Open the encrypted database and set its version
                database = SQLiteDatabase.openDatabase(
                    temporaryDatabasePath, password, null, SQLiteDatabase.OPEN_READWRITE
                )
                database.version = version
                // Close the database connection
                database.close()
                // Delete the original unencrypted database file
                originalFile.delete()
                // Rename the temporary file to the original file name
                newFile.renameTo(originalFile)
            }.onFailure {
                Log.e("DatabaseMigration", "Error migrating database", it)
            }
        }
    }

    @Throws(SQLiteException::class)
    private fun getCurrentSqliteDatabase(
        databasePath: String,
        password: String? = null
    ): SQLiteDatabase {
        // Set the passphrase to an empty string if not provided
        val passPhrase = password ?: ""
        // Open the SQLiteDatabase with the specified parameters
        return SQLiteDatabase.openDatabase(
            databasePath,
            passPhrase,
            null,
            SQLiteDatabase.CREATE_IF_NECESSARY
        )
    }

}