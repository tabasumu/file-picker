package com.tabasumu.filepicker

import android.content.Context
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

    private val callback: ((result: List<Pair<Uri, File>>) -> Unit)? = builder.callback
    private val inputType: String = builder.input

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) {
            it?.let { uriList ->
                //Map result to list of pairs containing uri and file
                val result = uriList.map { mUri ->
                    val file = when (mUri.scheme) {
                        "content" -> mUri.getFile(requireContext())
                        else -> mUri.toFile()
                    }
                    Pair(mUri, file)
                }

                callback?.invoke(result)
                this.dismiss()
            }
        }


    class Builder constructor(private val fragmentActivity: FragmentActivity) {


        @get:JvmSynthetic
        @set: JvmSynthetic
        internal var callback: ((result: List<Pair<Uri, File>>) -> Unit)? = null

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
                    destinationFilename
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
        } else null
    }


}


