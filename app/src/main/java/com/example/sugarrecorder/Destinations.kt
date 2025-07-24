package com.example.sugarrecorder

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

interface Destinations {
    val route : String
    val icon : ImageVector
    val title : String
}

object Home : Destinations{
    override val route = "home"
    override val icon = Icons.Default.Home
    override val title = "Home"
}

object settings : Destinations{
    override val route = "settings"
    override val icon = Icons.Default.Settings
    override val title = "Settings"
}