package com.bongdatv.update

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class GitHubRelease(
    val tag_name: String,
    val name: String? = null,
    val body: String? = null,
    val assets: List<GitHubAsset> = emptyList()
)

@Serializable
data class GitHubAsset(
    val name: String,
    val browser_download_url: String,
    val size: Long = 0
)

data class UpdateInfo(
    val version: String,
    val downloadUrl: String,
    val releaseNotes: String?
)

@Singleton
class UpdateChecker @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun checkForUpdate(
        currentVersion: String,
        owner: String,
        repo: String
    ): UpdateInfo? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("https://api.github.com/repos/$owner/$repo/releases/latest")
                .header("Accept", "application/vnd.github.v3+json")
                .header("User-Agent", "BongDaTV-Android")
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) return@withContext null

            val body = response.body?.string() ?: return@withContext null
            val release = json.decodeFromString<GitHubRelease>(body)

            val latestVersion = release.tag_name.removePrefix("v")
            if (!isNewerVersion(latestVersion, currentVersion)) return@withContext null

            val apkAsset = release.assets.firstOrNull { it.name.endsWith(".apk") }
                ?: return@withContext null

            UpdateInfo(
                version = latestVersion,
                downloadUrl = apkAsset.browser_download_url,
                releaseNotes = release.body
            )
        } catch (e: Exception) {
            null
        }
    }

    fun downloadAndInstall(context: Context, updateInfo: UpdateInfo) {
        val fileName = "bongdatv-${updateInfo.version}.apk"
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(updateInfo.downloadUrl))
            .setTitle("BongDa TV ${updateInfo.version}")
            .setDescription("Đang tải bản cập nhật...")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        val downloadId = downloadManager.enqueue(request)

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id != downloadId) return
                ctx.unregisterReceiver(this)

                val query = DownloadManager.Query().setFilterById(downloadId)
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val statusCol = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    if (statusCol >= 0 && cursor.getInt(statusCol) == DownloadManager.STATUS_SUCCESSFUL) {
                        val uriCol = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                        if (uriCol >= 0) {
                            val localUri = Uri.parse(cursor.getString(uriCol))
                            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                                setDataAndType(localUri, "application/vnd.android.package-archive")
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            ctx.startActivity(installIntent)
                        }
                    }
                }
                cursor.close()
            }
        }
        context.registerReceiver(
            receiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }

    internal fun isNewerVersion(latest: String, current: String): Boolean {
        val latestParts = latest.split(".").mapNotNull { it.toIntOrNull() }
        val currentParts = current.split(".").mapNotNull { it.toIntOrNull() }

        for (i in 0 until maxOf(latestParts.size, currentParts.size)) {
            val l = latestParts.getOrElse(i) { 0 }
            val c = currentParts.getOrElse(i) { 0 }
            if (l > c) return true
            if (l < c) return false
        }
        return false
    }

    companion object {
        const val PREF_KEY_DISMISS_TIME = "update_dismiss_time"
        const val DISMISS_DURATION_MS = 24 * 60 * 60 * 1000L
    }
}
