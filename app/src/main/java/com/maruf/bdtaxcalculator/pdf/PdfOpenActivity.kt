package com.maruf.bdtaxcalculator.pdf

import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.activity.ComponentActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Dedicated notification target; never navigates to the calculator/home screen. */
class PdfOpenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bangla = intent.getBooleanExtra(ExtraBangla, false)
        val uri = intent.data
        if (uri == null || uri.scheme != "content") {
            showError(bangla, missingFile = true)
            return
        }
        lifecycleScope.launch {
            val readable = withContext(Dispatchers.IO) {
                runCatching {
                    contentResolver.openFileDescriptor(uri, "r")?.use { true } ?: false
                }.getOrDefault(false)
            }
            if (!readable) {
                showError(bangla, missingFile = true)
                return@launch
            }
            val view = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                clipData = ClipData.newRawUri("PDF", uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            try {
                // Use the default PDF viewer, or Android's resolver if none is selected.
                startActivity(view)
                finish()
            } catch (_: ActivityNotFoundException) {
                showError(bangla, missingFile = false)
            } catch (_: SecurityException) {
                showError(bangla, missingFile = true)
            }
        }
    }

    private fun showError(bangla: Boolean, missingFile: Boolean) {
        val message = if (missingFile) {
            if (bangla) "ফাইলটি পাওয়া যাচ্ছে না বা পড়ার অনুমতি নেই। Downloads থেকে খুলুন অথবা আবার PDF সংরক্ষণ করুন।"
            else "The file is unavailable or access was lost. Open it from Downloads or save the PDF again."
        } else {
            if (bangla) "PDF খোলার কোনো অ্যাপ পাওয়া যায়নি। একটি PDF viewer ইনস্টল করে Downloads থেকে রিপোর্ট খুলুন।"
            else "No PDF viewer is installed. Install a PDF viewer and open the report from Downloads."
        }
        AlertDialog.Builder(this)
            .setTitle(if (bangla) "PDF খোলা যায়নি" else "Could not open PDF")
            .setMessage(message)
            .setPositiveButton(android.R.string.ok) { _, _ -> finish() }
            .setOnCancelListener { finish() }
            .show()
    }

    companion object {
        const val ExtraBangla = "pdf_is_bangla"
    }
}
