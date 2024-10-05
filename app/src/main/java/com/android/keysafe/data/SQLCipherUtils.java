//package com.android.keysafe.data;
//
//import android.content.Context;
//import android.text.Editable;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import net.sqlcipher.database.SQLiteDatabase;
//import net.sqlcipher.database.SQLiteStatement;
//
//
//public class SQLCipherUtils {
//
//    public enum State {
//        DOES_NOT_EXIST, UNENCRYPTED, ENCRYPTED
//    }
//
//    public static State getDatabaseState(Context ctxt, String dbName)
//    {
//        SQLiteDatabase.loadLibs(ctxt);
//
//        return (getDatabaseState(ctxt.getDatabasePath(dbName)));
//    }
//
//    public static State getDatabaseState(File dbPath)
//    {
//        if (dbPath.exists()) {
//
//            try (SQLiteDatabase db = SQLiteDatabase.openDatabase(
//                    dbPath.getAbsolutePath(), "",
//                    null, SQLiteDatabase.OPEN_READONLY
//            )) {
//
//                db.getVersion();
//
//                return (State.UNENCRYPTED);
//            } catch (Exception e) {
//                return (State.ENCRYPTED);
//            }
//        }
//
//        return (State.DOES_NOT_EXIST);
//    }
//
//    public static void encrypt(Context ctxt, String dbName, Editable editor)
//    throws IOException
//    {
//        char[] passphrase = new char[editor.length()];
//
//        editor.getChars(0, editor.length(), passphrase, 0);
//        encrypt(ctxt, dbName, passphrase);
//    }
//
//    public static void encrypt(Context ctxt, String dbName, char[] passphrase)
//    throws IOException
//    {
//        encrypt(ctxt, ctxt.getDatabasePath(dbName), SQLiteDatabase.getBytes(passphrase));
//    }
//
//    public static void encrypt(Context ctxt, String dbName, byte[] passphrase)
//    throws IOException
//    {
//        encrypt(ctxt, ctxt.getDatabasePath(dbName), passphrase);
//    }
//
//    public static void encrypt(Context ctxt, File originalFile, char[] passphrase)
//    throws IOException
//    {
//        encrypt(ctxt, originalFile, SQLiteDatabase.getBytes(passphrase));
//    }
//
//    public static void encrypt(Context ctxt, File originalFile, byte[] passphrase)
//    throws IOException
//    {
//        SQLiteDatabase.loadLibs(ctxt);
//
//        if (originalFile.exists()) {
//            File newFile = File . createTempFile ("sqlcipherutils", "tmp",
//            ctxt.getCacheDir());
//            SQLiteDatabase db =
//            SQLiteDatabase.openDatabase(
//                originalFile.getAbsolutePath(),
//                "", null, SQLiteDatabase.OPEN_READWRITE
//            );
//            int version = db . getVersion ();
//
//            db.close();
//
//            db = SQLiteDatabase.openDatabase(
//                newFile.getAbsolutePath(), passphrase,
//                null, SQLiteDatabase.OPEN_READWRITE, null, null
//            );
//
//            final SQLiteStatement st = db.compileStatement("ATTACH DATABASE ? AS plaintext KEY ''");
//
//            st.bindString(1, originalFile.getAbsolutePath());
//            st.execute();
//
//            db.rawExecSQL("SELECT sqlcipher_export('main', 'plaintext')");
//            db.rawExecSQL("DETACH DATABASE plaintext");
//            db.setVersion(version);
//            st.close();
//            db.close();
//
//            originalFile.delete();
//            newFile.renameTo(originalFile);
//        } else {
//            throw new FileNotFoundException (originalFile.getAbsolutePath() + " not found");
//        }
//    }
//
//    public static void decrypt(Context ctxt, File originalFile, char[] passphrase)
//    throws IOException
//    {
//        decrypt(ctxt, originalFile, SQLiteDatabase.getBytes(passphrase));
//    }
//
//    public static void decrypt(Context ctxt, File originalFile, byte[] passphrase)
//    throws IOException
//    {
//        SQLiteDatabase.loadLibs(ctxt);
//
//        if (originalFile.exists()) {
//            File newFile =
//            File.createTempFile(
//                "sqlcipherutils", "tmp",
//                ctxt.getCacheDir()
//            );
//            SQLiteDatabase db =
//            SQLiteDatabase.openDatabase(
//                originalFile.getAbsolutePath(),
//                passphrase, null, SQLiteDatabase.OPEN_READWRITE, null, null
//            );
//
//            final SQLiteStatement st = db.compileStatement("ATTACH DATABASE ? AS plaintext KEY ''");
//
//            st.bindString(1, newFile.getAbsolutePath());
//            st.execute();
//
//            db.rawExecSQL("SELECT sqlcipher_export('plaintext')");
//            db.rawExecSQL("DETACH DATABASE plaintext");
//
//            int version = db . getVersion ();
//
//            st.close();
//            db.close();
//
//            db = SQLiteDatabase.openDatabase(
//                newFile.getAbsolutePath(), "",
//                null, SQLiteDatabase.OPEN_READWRITE
//            );
//            db.setVersion(version);
//            db.close();
//
//            originalFile.delete();
//            newFile.renameTo(originalFile);
//        } else {
//            throw new FileNotFoundException (originalFile.getAbsolutePath() + " not found");
//        }
//    }
//}