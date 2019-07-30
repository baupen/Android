package io.mangel.issuemanager.services

import android.content.Context
import androidx.core.content.contentValuesOf
import java.io.File
import java.io.InputStreamReader
import java.util.ArrayList

public class FileService(private val folder: File){
    fun exists(fileName: String): Boolean {
        return File(folder, fileName).exists()
    }

    fun save(fileName: String, bytes: ByteArray) {
        File(folder, fileName).writeBytes(bytes)
    }

    fun read(fileName: String): File {
        return File(folder, fileName)
    }

    fun deleteOthers(whiteList: Set<String>) {
        val files = folder.listFiles() ?: return

        for (file in files) {
            if (!whiteList.contains(file.name)) {
                file.delete()
            }
        }
    }
}