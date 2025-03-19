package com.dvt.alwaysonscreen

import android.app.AlertDialog
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle

import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import android.os.PowerManager
import androidx.core.content.ContextCompat

import com.dvt.alwaysonscreen.ui.theme.AlwaysOnScreenTheme
import java.util.UUID

class MainActivity : ComponentActivity() {

    private val TAG = "MainActivity"
    private val POST_NOTIFICATIONS_PERMISSION_REQUEST_CODE = 101
    private val CHANNEL_ID = "ALWAYS_ON_SCREEN"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel(this)
        checkOverlayPermission()
        requestIgnoreBatteryOptimizations()
        enableEdgeToEdge()
        setContent {
            AlwaysOnScreenTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkPostNotificationPermission()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun checkPostNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                showPostNotificationPermissionDialog()
            } else {
                requestPostNotificationPermission()
            }
        }
    }

    private fun showPostNotificationPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Request Post Notifications Permission")
            .setMessage("Please grant the app permission to send notifications.")
            .setPositiveButton("OK") { _, _ ->
                requestPostNotificationPermission()
            }
            .setNegativeButton("Cancel", null)
            .show()

    }

    private fun requestPostNotificationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
            POST_NOTIFICATIONS_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode == POST_NOTIFICATIONS_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "always-on-screen"
            val descriptionText = "always-on-screen"
            val importance = android.app.NotificationManager.IMPORTANCE_DEFAULT
            val channel = android.app.NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.screenmonitor)
            .setContentTitle("Always On Screen")
            .setContentText("Always On Screen service is running...")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, builder.build())
    }

    private fun checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            }
        }
    }
    private fun requestIgnoreBatteryOptimizations() {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent(
                    Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
            }
        }
    }
}


@Composable
fun Greeting(modifier: Modifier = Modifier) {
    var workId by remember { mutableStateOf(UUID.randomUUID()) }
    var text by remember { mutableStateOf("START") }
    val context = LocalContext.current
    val CHANNEL_ID = "ALWAYS_ON_SCREEN"

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            Image(
                bitmap = ImageBitmap.imageResource(id = R.drawable.screenmonitor),
                contentDescription = "Logo",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(100.dp)
            )
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                "Always On Screen",
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(10.dp))
            ElevatedButton(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = {
                if (text.equals("START")) {
                    context.startForegroundService(Intent(context, ScreenOnService::class.java))
                    text = "STOP"
                } else {
                    context.stopService(Intent(context, ScreenOnService::class.java))
                    text = "START"
                }

            }) {
                Text(text)
            }
        }
        Text(
            "vantung.dong@gmail.com",
            modifier = Modifier.align(Alignment.BottomCenter),
            fontSize = 10.sp,
            color = androidx.compose.ui.graphics.Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AlwaysOnScreenTheme {
        Greeting()
    }
}