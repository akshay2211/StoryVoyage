package io.ak1.demo.data.util

import com.pspdfkit.document.providers.InputStreamDataProvider
import java.io.File
import java.io.InputStream

open class FileDataProvider(val file: File) : InputStreamDataProvider() {
    override fun getSize(): Long {
        return file.length()
    }

    override fun getUid(): String {
        return file.canonicalPath
    }

    override fun openInputStream(): InputStream {
        return file.inputStream()
    }

    override fun getTitle(): String? {
        return file.nameWithoutExtension
    }
}