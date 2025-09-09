package com.example.sugarrecorder

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavController

@Composable
fun BottomNavBar(navController: NavController)
{
    val DestinationList=listOf(
        Home,settings
    )

    val selectedIndex= rememberSaveable{ mutableIntStateOf(0) }
    NavigationBar {
        DestinationList.forEachIndexed{index,destination ->
            NavigationBarItem(
                label = {Text(destination.title) },
                icon =  { Icon(imageVector = destination.icon , contentDescription = destination.title) } ,
                selected = index==selectedIndex.intValue ,
                onClick = {
                    selectedIndex.intValue = index
                    navController.navigate(destination.route)
                    {
                        popUpTo(Home.route)
                        launchSingleTop = true
                    }
                }


            )
        }
    }
}