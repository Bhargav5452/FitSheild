package com.example.fitsheild

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitsheild.ui.theme.FitSheildTheme

class MainActivity : ComponentActivity() {

    private lateinit var requestPermissionsLauncher: ActivityResultLauncher<Array<String>>
    private var permissionsGranted by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the permission request launcher
        requestPermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissionsGranted = permissions.all { it.value }
            if (permissionsGranted) {
                // Navigate to the emergency contact screen
                setContent {
                    FitSheildTheme {
                        EmergencyContactScreen()
                    }
                }
            } else {
                handleDeniedPermissions(permissions)
            }
        }

        // Request necessary permissions
        requestPermissions()
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_SMS
        )
        requestPermissionsLauncher.launch(permissions)
    }

    private fun handleDeniedPermissions(permissions: Map<String, Boolean>) {
        val deniedPermissions = permissions.filter { !it.value }

        if (deniedPermissions.isNotEmpty()) {
            showPermissionDeniedDialog()
        }
    }

    private fun showPermissionDeniedDialog() {
        val dialog = BuildAlertDialog(this)
        dialog.create(
            title = "Permissions Required",
            message = "Some permissions were denied. Please enable them in the app settings to continue using all features.",
            positiveButtonText = "Go to Settings",
            negativeButtonText = "Cancel",
            onPositiveClick = {
                // Open the app's settings page
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            },
            onNegativeClick = {
                // Optionally, handle the case where the user cancels
            }
        )
    }
}

@Composable
fun EmergencyContactScreen() {
    var contact1 by remember { mutableStateOf("") }
    var contact2 by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Enter Emergency Contact Numbers", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = contact1,
            onValueChange = { contact1 = it },
            label = { Text("Emergency Contact 1") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = contact2,
            onValueChange = { contact2 = it },
            label = { Text("Emergency Contact 2") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { /* Handle the save or proceed action */ }) {
            Text("Save Contacts")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EmergencyContactScreenPreview() {
    FitSheildTheme {
        EmergencyContactScreen()
    }
}
