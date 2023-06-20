package com.tabasumu.filepicker

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class FilePicker internal constructor(builder: Builder) :
    BottomSheetDialogFragment() {

    class GetFiles : ActivityResultContracts.GetMultipleContents() {
        override fun createIntent(context: Context, input: String): Intent {
            return super.createIntent(context, "*/*").apply {
                putExtra(
                    Intent.EXTRA_MIME_TYPES,
                    input.split(";").filter { it.isNotBlank() }.toTypedArray(),
                )
            }
        }
    }

    private val callback: ((result: List<Pair<Uri, File>>) -> Unit)? = builder.callback
    private val singleCallback: ((uri: Uri, file: File) -> Unit)? = builder.singleFileCallback
    private val inputType: String = builder.input

    private val filePickerLauncher =
        registerForActivityResult(GetFiles()) {
            it?.let { uriList ->
                // Map result to list of pairs containing uri and file
                val result = uriList.map { mUri ->
                    val file = when (mUri.scheme) {
                        "content" -> mUri.getFile(requireContext())
                        else -> mUri.toFile()
                    }
                    Pair(mUri, file)
                }

                if (result.size == 1) {
                    singleCallback?.invoke(result.first().first, result.first().second)
                } else {
                    callback?.invoke(result)
                }
                this.dismiss()
            }
        }

    class Builder constructor(private val fragmentActivity: FragmentActivity) {

        @get:JvmSynthetic
        @set: JvmSynthetic
        internal var callback: ((result: List<Pair<Uri, File>>) -> Unit)? = null

        @get:JvmSynthetic
        @set: JvmSynthetic
        internal var singleFileCallback: ((uri: Uri, file: File) -> Unit)? = null

        @get:JvmSynthetic
        @set: JvmSynthetic
        internal var input: String = "*/*"

        fun inputType(type: String) = apply {
            this.input = type
        }

        fun pick(callback: (result: List<Pair<Uri, File>>) -> Unit) {
            this.callback = callback
            FilePicker(this).show(fragmentActivity)
        }

        fun pickSingle(callback: (uri: Uri, file: File) -> Unit) {
            this.singleFileCallback = callback
            FilePicker(this).show(fragmentActivity)
        }

        private fun BottomSheetDialogFragment?.show(activity: FragmentActivity) {
            if ((this?.isVisible) == false) {
                this.show(activity.supportFragmentManager, activity.localClassName)
                activity.supportFragmentManager.executePendingTransactions()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        filePickerLauncher.launch(inputType)
    }

    private fun Uri.getFile(context: Context): File {
        val destinationFilename =
            File(context.filesDir.path + File.separatorChar + this.queryName(context))
        try {
            context.contentResolver.openInputStream(this).use { ins ->
                createFileFromStream(
                    ins!!,
                    destinationFilename,
                )
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return destinationFilename
    }

    private fun createFileFromStream(ins: InputStream, destination: File?) {
        try {
            FileOutputStream(destination).use { os ->
                val buffer = ByteArray(4096)
                var length: Int
                while (ins.read(buffer).also { length = it } > 0) {
                    os.write(buffer, 0, length)
                }
                os.flush()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun Uri.queryName(context: Context): String? {
        val returnCursor = context.contentResolver.query(this, null, null, null, null)
        return if (returnCursor != null) {
            val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            val name = returnCursor.getString(nameIndex)
            returnCursor.close()
            name
        } else {
            null
        }
    }
}
