package com.example.sugarrecorder

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun settings(innerPadding: PaddingValues) {
    val eatTimes=EatTimesViewModel.eatTimes
    Column (modifier = Modifier.padding(innerPadding).padding(horizontal = 8.dp)) {
        for ((key,value) in eatTimes){
        inputTimes(key,value)
        }
    }

}