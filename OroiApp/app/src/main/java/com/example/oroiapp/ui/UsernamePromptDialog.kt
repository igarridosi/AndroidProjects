// En /ui/UsernamePromptDialog.kt
package com.example.oroiapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun UsernamePromptDialog(
    currentInput: String,
    onInputChange: (String) -> Unit,
    onSave: () -> Unit,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors()
) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text("Ongi Etorri!", color = Color.Black) },
        text = {
            Column {
                Text("Zure izena jakin nahiko genuke.", color = Color.Black)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = currentInput,
                    onValueChange = onInputChange,
                    label = { Text("Zure izena") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                enabled = currentInput.isNotBlank()
            ) {
                Text("Gorde", color = Color.White)
            }
        },
        dismissButton = null
    )
}