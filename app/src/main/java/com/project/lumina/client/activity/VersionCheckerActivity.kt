package com.project.lumina.client.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.lumina.client.model.VersionConfig
import com.project.lumina.client.ui.theme.LuminaClientTheme
import com.project.lumina.client.essentials.API
import com.project.lumina.client.essentials.TrackUtil
import com.project.lumina.client.util.HashCat
import com.project.lumina.client.util.UpdateCheck
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

class VersionCheckerViewModel : ViewModel() {
    private val _versionConfig = MutableStateFlow<VersionConfig?>(null)
    val versionConfig: StateFlow<VersionConfig?> = _versionConfig.asStateFlow()

    fun loadVersionConfig(context: android.content.Context, configUrl: String) {
        viewModelScope.launch {
            _versionConfig.value = try {



                val configFile = File(context.filesDir, "version_config.json")
                if (configFile.exists()) {
                    configFile.delete()
                    println("Deleted existing version_config.json")
                }

                
                val jsonString = withContext(Dispatchers.IO) {
                    val client = OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .build()
                    val request = Request.Builder()
                        .url(configUrl)
                        .build()
                    val response = client.newCall(request).execute()
                    if (!response.isSuccessful) {
                        println("HTTP Error: ${response.code} ${response.message}")
                        throw IOException("Unexpected code ${response.code}")
                    }
                    val body = response.body?.string() ?: throw IOException("Empty response body")
                    println("Downloaded JSON: $body")
                    body
                }

                
                withContext(Dispatchers.IO) {
                    configFile.writeText(jsonString)
                    println("Saved JSON to ${configFile.absolutePath}")
                }

                
                val jsonFromFile = withContext(Dispatchers.IO) {
                    if (!configFile.exists()) {
                        throw IOException("Config file not found after saving")
                    }
                    configFile.readText()
                }
                println("Read JSON from file: $jsonFromFile")

                val jsonObject = JSONObject(jsonFromFile)
                val supportedVersionsJson = jsonObject.optJSONArray("supportedVersions") ?: JSONArray()
                val supportedVersions = mutableListOf<String>()
                for (i in 0 until supportedVersionsJson.length()) {
                    supportedVersions.add(supportedVersionsJson.getString(i))
                }

                VersionConfig(
                    minimumVersion = jsonObject.optString("minimumVersion", "-1"),
                    recommendedVersion = jsonObject.optString("recommendedVersion", "-1"),
                    supportedVersions = supportedVersions,
                    versionMessage = jsonObject.optString("versionMessage", "Lumina requires Minecraft version %s or later to function properly.")
                )
            } catch (e: Exception) {
                println("Error loading version config: ${e.message}")
                e.printStackTrace()
                VersionConfig(
                    minimumVersion = "Dead API",
                    recommendedVersion = "API Is Offline",
                    supportedVersions = listOf("-1"),
                    versionMessage = "Api Config Failed."
                )
            }
        }
    }
}

class VersionCheckerActivity : ComponentActivity() {
    private val minecraftPackage = "com.mojang.minecraftpe"
    private val configUrl = API.FILES_VERSION_CONFIG_JSON



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val viewModel: VersionCheckerViewModel by viewModels()
        viewModel.loadVersionConfig(this, configUrl)
        val updateCheck = UpdateCheck()
        updateCheck.initiateHandshake(this)
        setContent {
            LuminaClientTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val versionConfig by viewModel.versionConfig.collectAsState()
                    when {
                        versionConfig == null -> {
                            val kson = HashCat.getInstance()
                            val matchJson = kson.LintHashInit(this)
                            if (matchJson) { }
                            Text(
                                text = "Loading configuration...",
                                modifier = Modifier.fillMaxSize(),
                                textAlign = TextAlign.Center
                            )
                        }
                        else -> {
                            val kson = HashCat.getInstance()
                            val matchJson = kson.LintHashInit(this)
                            if (matchJson) { }
                            val installedVersion = getInstalledMinecraftVersion()
                            if (isCompatibleVersion(installedVersion, versionConfig!!)) {
                                startMainActivity()
                            } else {
                                IncompatibleVersionScreen(
                                    installedVersion = installedVersion ?: "Unknown",
                                    requiredVersion = versionConfig!!.minimumVersion,
                                    versionMessage = String.format(
                                        versionConfig!!.versionMessage,
                                        versionConfig!!.minimumVersion
                                    ),
                                    onUpdateClick = { openPlayStore() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getInstalledMinecraftVersion(): String? {
        return try {
            val packageInfo = packageManager.getPackageInfo(minecraftPackage, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

    private fun isCompatibleVersion(version: String?, config: VersionConfig): Boolean {
        if (version == null) return false
        return config.supportedVersions.contains(version)
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun openPlayStore() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/apps/details?id=$minecraftPackage&hl=en&pli=1")
            setPackage("com.android.vending")
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            val webIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=$minecraftPackage&hl=en&pli=1")
            }
            startActivity(webIntent)
        }
    }
}

@Composable
fun IncompatibleVersionScreen(
    installedVersion: String,
    requiredVersion: String,
    versionMessage: String,
    onUpdateClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Incompatible Minecraft Version",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your Minecraft version ($installedVersion) is not compatible with Lumina.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = versionMessage,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onUpdateClick) {
            Text(text = "Update Minecraft")
        }
    }
}