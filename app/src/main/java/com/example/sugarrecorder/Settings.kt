package com.example.sugarrecorder

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun settings(innerPadding: PaddingValues) {
    val eatTimes=EatTimesViewModel.eatTimes
    val context = LocalContext.current
    Column (modifier = Modifier.padding(innerPadding).padding(horizontal = 8.dp)) {
        val localEatTimes = eatTimes
        for ((key,value) in localEatTimes){
        inputTimes(key,value)
        }
        Button(onClick = {
            for ((key, value) in localEatTimes) {
                val hour = value[0]
                val minute = value[1]
                if (hour != null && minute != null) {
                    EatTimesViewModel.updateTime(key, hour, minute)
                }
            }
            Toast.makeText(context, "Saved Successfully!", Toast.LENGTH_SHORT).show()
        }, modifier = Modifier.fillMaxWidth().padding(top = 20.dp, start = 10.dp, end = 10.dp) ) {
            Text("Save")
        }

    }

}