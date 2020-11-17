package com.ekoapp.ekosdk.uikit.common

import android.content.ContentResolver
import android.content.Context
import android.database.CursorIndexOutOfBoundsException
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import com.ekoapp.ekosdk.uikit.R
import org.apache.commons.io.FilenameUtils
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class FileUtils {
    companion object {


        fun getFileName(uri: Uri, context: Context): String {
            var result: String? = null
            if (uri.scheme == "content") {
                val cursor =
                    context.contentResolver.query(uri, null, null, null, null)
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        result =
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                } finally {
                    cursor!!.close()
                }
            }
            if (result == null) {
                result = uri.path
                val cut = result!!.lastIndexOf('/')
                if (cut != -1) {
                    result = result.substring(cut + 1)
                }
            }
            return result
        }

        fun getPathFromUri(
            context: Context,
            uri: Uri
        ): String? {
            return if (isFile(uri)) {
                getPathFromFile(context, uri)
            } else if (isDocument(context, uri)) {
                getPathFromDocument(context, uri)
            } else {
                try {
                    getPathFromContent(context, uri)
                } catch (e: CursorIndexOutOfBoundsException) {
                    uri.toString()
                }
            }
        }

        private fun isFile(uri: Uri): Boolean {
            return (uri.scheme == null
                    || ContentResolver.SCHEME_FILE == uri.scheme)
        }

        private fun getPathFromFile(
            context: Context,
            uri: Uri
        ): String? {
            return uri.path
        }

        private fun isDocument(
            context: Context,
            uri: Uri
        ): Boolean {
            return DocumentsContract.isDocumentUri(context, uri)
        }

        private fun getPathFromContent(
            context: Context,
            uri: Uri
        ): String? {
            val projection =
                arrayOf(MediaStore.Images.Media.DATA)
            val cursor =
                context.contentResolver.query(uri, projection, null, null, null)
            val index = cursor!!.getColumnIndex(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            val path = cursor.getString(index)
            cursor.close()
            return path
        }

        private fun getPathFromDocument(
            context: Context,
            uri: Uri
        ): String? {
            val fileName = getName(context.contentResolver, uri)
            var inputStream: InputStream? = null
            var output: FileOutputStream? = null
            try {
                inputStream = context.contentResolver.openInputStream(uri)
                val file = File(context.cacheDir, fileName)
                output = FileOutputStream(file)
                val bufferSize = 1024
                val buffer = ByteArray(bufferSize)
                var len: Int
                while (inputStream!!.read(buffer).also { len = it } != -1) {
                    output.write(buffer, 0, len)
                }
                output.flush()
                return file.absolutePath
            } catch (e: OutOfMemoryError) {
            } catch (e: Exception) {
            } finally {
                try {
                    inputStream?.close()
                    output?.close()
                } catch (e: Exception) {
                }
            }
            return ""
        }

        fun getName(contentResolver: ContentResolver, uri: Uri): String? {
            if (uri.scheme == null || ContentResolver.SCHEME_FILE == uri.scheme) {
                val file = File(uri.path)
                return file.name
            }
            val cursor =
                contentResolver.query(uri, null, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
            return null
        }

        fun getMimeType(contentResolver: ContentResolver, uri: Uri): String? {
            if (uri.scheme == null || ContentResolver.SCHEME_FILE == uri.scheme) {
                val extension = FilenameUtils.getExtension(uri.path)
                return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            }
            return contentResolver.getType(uri)
        }

        fun getSize(contentResolver: ContentResolver, uri: Uri): Int {
            if (uri.scheme == null || ContentResolver.SCHEME_FILE == uri.scheme) {
                val file = File(uri.path)
                return file.length().toInt()
            }
            val cursor =
                contentResolver.query(uri, null, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    return if (!cursor.isNull(sizeIndex)) {
                        cursor.getInt(sizeIndex)
                    } else {
                        0
                    }
                }
            } finally {
                cursor?.close()
            }
            return 0
        }

        fun humanReadableByteCount(bytes: Long, si: Boolean): String? {
            val unit = if (si) 1000 else 1024
            if (bytes < unit) {
                return "$bytes B"
            }
            val exp =
                (Math.log(bytes.toDouble()) / Math.log(unit.toDouble())).toInt()
            val pre =
                (if (si) "kMGTPE" else "KMGTPE")[exp - 1].toString() + if (si) "" else "i"
            return String.format(
                "%.2f %sB",
                bytes / Math.pow(unit.toDouble(), exp.toDouble()),
                pre
            )
        }

        fun isFileTypeDoc(fileExtension: String): Boolean {
            return fileExtension.equals("doc", ignoreCase = true) || fileExtension.equals(
                "docx",
                ignoreCase = true
            )
        }

        fun isFileTypePdf(fileExtension: String): Boolean {
            return fileExtension.equals("pdf", ignoreCase = true)
        }

        fun getFileIcon(mimeType: String):Int{
            return (when(mimeType) {
                "application/x-msdos-program", 
                "application/vnd.microsoft.portable-executable" ,
                "application/octet-stream"-> R.drawable.ic_uikit_exe_large
                "application/rar" -> R.drawable.ic_uikit_rar_large
                "application/pdf" -> R.drawable.ic_uikit_pdf_large
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> R.drawable.ic_uikit_doc_large
                 "application/vnd.ms-excel",
                 "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"  -> R.drawable.ic_xls_large
                "text/html" -> R.drawable.ic_uikit_html_large
                "video/mp4" -> R.drawable.ic_uikit_mp4_large
                "video/quicktime" -> R.drawable.ic_uikit_mov_large
                "application/vnd.ms-powerpoint" -> R.drawable.ic_uikit_ppt_large
                "application/vnd.openxmlformats-officedocument.presentationml.presentation" -> R.drawable.ic_uikit_ppx_large
                "application/zip" -> R.drawable.ic_zip_large
                "audio/mpeg" -> R.drawable.ic_uikit_mp3_large
                "text/plain" -> R.drawable.ic_uikit_txt_large
                "text/comma-separated-values" -> R.drawable.ic_uikit_csv_large
                "video/mpeg" -> R.drawable.ic_mpeg_large
                "video/x-msvideo" -> R.drawable.ic_uikit_avi_large
                else -> {
                    if(mimeType.startsWith("audio")) R.drawable.ic_uikit_audio_large
                    else if(mimeType.startsWith("image")) R.drawable.ic_uikit_img_large
                    else R.drawable.ic_uikit_file_type_unknown
                }
            })

        }
    }
}