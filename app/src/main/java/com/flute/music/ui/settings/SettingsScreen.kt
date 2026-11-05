package com.flute.music.ui.settings

import android.content.Intent
import android.media.audiofx.AudioEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Equalizer
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flute.music.ui.theme.BackgroundDark
import com.flute.music.ui.theme.DividerColor
import com.flute.music.ui.theme.FlutePurple
import com.flute.music.ui.theme.SurfaceDark
import com.flute.music.ui.theme.TextPrimary
import com.flute.music.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showSleepTimerDialog by remember { mutableStateOf(false) }
    var selectedSleepTimer by remember { mutableStateOf("Off") }

    val timerOptions = listOf("Off", "15 minutes", "30 minutes", "45 minutes", "60 minutes")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                SettingsCategoryTitle("Playback")
            }

            item {
                SettingsItem(
                    icon = Icons.Rounded.Timer,
                    title = "Sleep Timer",
                    subtitle = selectedSleepTimer,
                    onClick = { showSleepTimerDialog = true }
                )
            }

            item {
                SettingsItem(
                    icon = Icons.Rounded.Equalizer,
                    title = "Equalizer",
                    subtitle = "Open system audio effects",
                    onClick = {
                        try {
                            val intent = Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // System equalizer might not be installed on emulator/certain ROMs
                        }
                    }
                )
            }

            item {
                Divider(color = DividerColor, modifier = Modifier.padding(vertical = 8.dp))
                SettingsCategoryTitle("Appearance")
            }

            item {
                SettingsItem(
                    icon = Icons.Rounded.DarkMode,
                    title = "Theme",
                    subtitle = "Always Dark (OLED Friendly)",
                    onClick = {}
                )
            }

            item {
                Divider(color = DividerColor, modifier = Modifier.padding(vertical = 8.dp))
                SettingsCategoryTitle("About Flute")
            }

            item {
                SettingsItem(
                    icon = Icons.Rounded.Block,
                    title = "100% Ad-Free",
                    subtitle = "No trackers, no subscriptions, no ads ever.",
                    onClick = {}
                )
            }

            item {
                SettingsItem(
                    icon = Icons.Rounded.MusicNote,
                    title = "Music Sources",
                    subtitle = "Local files & Jamendo Free Music (Creative Commons)",
                    onClick = {}
                )
            }

            item {
                SettingsItem(
                    icon = Icons.Rounded.Info,
                    title = "App Version",
                    subtitle = "Flute v1.0.0 (Supports Android 5.0+)",
                    onClick = {}
                )
            }
        }
    }

    if (showSleepTimerDialog) {
        AlertDialog(
            onDismissRequest = { showSleepTimerDialog = false },
            title = { Text("Sleep Timer", color = TextPrimary) },
            text = {
                Column {
                    timerOptions.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedSleepTimer = option
                                    showSleepTimerDialog = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedSleepTimer == option,
                                onClick = {
                                    selectedSleepTimer = option
                                    showSleepTimerDialog = false
                                },
                                colors = RadioButtonDefaults.colors(selectedColor = FlutePurple)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = option, color = TextPrimary)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSleepTimerDialog = false }) {
                    Text("Cancel", color = FlutePurple)
                }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
private fun SettingsCategoryTitle(text: String) {
    Text(
        text = text,
        color = FlutePurple,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 13.sp
            )
        }
    }
}
