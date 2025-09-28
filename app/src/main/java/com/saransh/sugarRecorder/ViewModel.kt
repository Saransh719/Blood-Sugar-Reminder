package com.saransh.sugarRecorder

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

object EatTimesViewModel : ViewModel() {
    val defaultEatTimes= mutableMapOf("Breakfast" to mutableListOf<Int?>(8,0),"Lunch" to mutableListOf<Int?>(13,0),"Dinner" to mutableListOf<Int?>(20,0))
    var eatTimes by mutableStateOf(
        defaultEatTimes.toMutableMap()
    )
        private set     //private setter

    fun updateTime(meal: String, hour: Int?, minute: Int?) {
        eatTimes[meal]?.set(0, hour)
        eatTimes[meal]?.set(1, minute)
        eatTimes = eatTimes.toMutableMap()
    }

    fun reset(){
        eatTimes = defaultEatTimes.mapValues { it.value.toMutableList() }.toMutableMap()
    }
}
