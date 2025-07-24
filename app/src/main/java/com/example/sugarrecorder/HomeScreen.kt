package com.example.sugarrecorder

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    innerPadding: PaddingValues,
    db: RecordsDatabase,
    lifecycleScope: LifecycleCoroutineScope,
){
    //checking if its users first time launching the app
    FirstLaunchChecker()
    val context = LocalContext.current
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    var selectedDate by remember { mutableStateOf<LocalDate>(LocalDate.now()) }

    //setting up notifications
    val eatTimes=EatTimesViewModel.eatTimes
    LaunchedEffect (eatTimes){  eatTimes.forEach { (key, value) ->
        val triggerTime = if (value[0] != null && value[1] != null) {
            getInitialTriggerTime(value[0]!!, value[1]!!)
        } else null

        if (triggerTime != null) {
            scheduleNotification(context, triggerTime - (60 * 60 * 1000), "It's time to measure your Before $key")
            scheduleNotification(context, triggerTime + (60 * 60 * 1000), "It's time to measure your After $key")
        }
    } }



    val datePicker = DatePickerDialog(
        context,
        { _: DatePicker, y: Int, m: Int, d: Int ->              //these are values selected by user
            selectedDate = LocalDate.of(y, m + 1, d)             //values assigned to variable
        }, selectedDate.year, selectedDate.monthValue-1, selectedDate.dayOfMonth    //these are initial values
    )
    datePicker.datePicker.maxDate = System.currentTimeMillis()
    Column (Modifier.fillMaxSize().padding(innerPadding).padding(start = 16.dp)){
        Row{
            Text("Date : " , fontSize = 20.sp)
            Text(text = selectedDate.format(formatter) , fontSize = 20.sp, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { datePicker.show() })
        }

        val records by db.RecordsDao().getReadings(selectedDate.toString()).observeAsState(emptyList())
        showRecords(records,db,lifecycleScope,selectedDate)

    }

}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun showRecords(
    records: List<Record>,
    db: RecordsDatabase,
    lifecycleScope: LifecycleCoroutineScope,
    selectedDate: LocalDate
) {
    var showDialog by remember { mutableStateOf(false) }
    var reading by remember { mutableStateOf("") }
    var record by remember { mutableStateOf<Record?>(null) }
    var selectedTimeType by remember{ mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    LazyColumn (modifier = Modifier.padding(horizontal = 1.dp, vertical = 12.dp)){
        items(TimeTypes){
            TimeType ->
            Row (Modifier.padding(8.dp)){
                val currentRecord = records.find { it.time == TimeType.displayName }

                val displayReading = currentRecord?.reading?.toString() ?: "Data not entered"
                Row {
                    Text(TimeType.displayName + " : " + displayReading + " - " , modifier = Modifier.clickable {
                        record=currentRecord
                        selectedTimeType=TimeType.displayName
                        reading = record?.reading?.toString() ?: ""
                        note= record?.note ?: ""
                        showDialog=true })
                    Text(currentRecord?.note ?: "" , modifier = Modifier.clickable {
                        record=currentRecord
                        selectedTimeType=TimeType.displayName
                        reading = record?.reading?.toString() ?: ""
                        note= record?.note ?: ""
                        showDialog=true })
                }

            }

        }
    }
    if (showDialog){
        BasicAlertDialog(
            onDismissRequest = {showDialog =false
                reading = record?.reading?.toString() ?: ""
                note= record?.note ?: ""},
        ){
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 6.dp
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Reading for $selectedTimeType",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))


                    Text("Enter Reading")
                    OutlinedTextField(
                        value =reading,
                        onValueChange = {reading =it},
                        label = {Text("Enter Sugar value")},
                        modifier = Modifier.padding(10.dp).fillMaxWidth(),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )

                    Text("Note:")
                    OutlinedTextField(
                        value =note,
                        onValueChange = {note=it},
                        label = {Text("Enter Note")},
                        modifier = Modifier.padding(10.dp).fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showDialog = false
                            reading = record?.reading?.toString() ?: ""
                            note= record?.note ?: ""
                        }) {
                            //reverting changes on cancel
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = {
                            // Handle OK action
                            showDialog = false
                            if (reading!="") addReading(record, selectedTimeType,reading,note,db,lifecycleScope,selectedDate)
                        }) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun addReading(
    record: Record?,
    selectedTimeType: String,
    reading: String,
    note: String,
    db: RecordsDatabase,
    lifecycleScope: LifecycleCoroutineScope,
    selectedDate: LocalDate
) {
    if (record != null) {
        lifecycleScope.launch {
            withContext(IO) {db.RecordsDao().update(record.id,reading.toInt(),note) }
        }

    }
    else{
        val newRecord= Record(reading = reading.toInt(),date =selectedDate.toString() , time =  selectedTimeType, note =note)
        lifecycleScope.launch {
            withContext(IO) {db.RecordsDao().saveRecord(newRecord) }
        }
    }
}

fun isFirstLaunch(context: Context): Boolean {
    val file = File(context.filesDir, "first_launch_done.txt")
    return !file.exists()
}

fun markFirstLaunchDone(context: Context) {
    val file = File(context.filesDir, "first_launch_done.txt")
    file.writeText("done") // Or anything — just so the file exists
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstLaunchChecker() {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var eatTimes=EatTimesViewModel.eatTimes
    LaunchedEffect(Unit) {
        if (isFirstLaunch(context)) {
            showDialog = true
            markFirstLaunchDone(context)
        }
    }
    if (showDialog) {
        BasicAlertDialog(
            onDismissRequest = {showDialog=false
            EatTimesViewModel.reset()}
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 6.dp
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Welcome, enter your Eating times (in 24hr format) ",
                        style = MaterialTheme.typography.titleLarge
                    )


                    Spacer(modifier = Modifier.height(16.dp))

                    for ((key,value) in eatTimes){
                        inputTimes(key,value)
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showDialog = false
                            EatTimesViewModel.reset()
                        }) {
                            //reverting changes on cancel
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = {
                            // Handle OK action
                            showDialog = false }) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun inputTimes(
    key: String,
    value: MutableList<Int?>
) {
    var hour by remember { mutableStateOf(value[0]?.toString() ?: "") }
    var minute by remember { mutableStateOf(value[1]?.toString() ?: "") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = key,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically // Optional, ensures fields are level
        ) {
            OutlinedTextField(
                value = hour,
                onValueChange = {
                    val number = it.toIntOrNull()
                    if (number == null || number in 0..23) hour = it
                },
                label = { Text("Hour") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = minute,
                onValueChange = {
                    val number = it.toIntOrNull()
                    if (number == null || number in 0..59) minute = it
                },
                label = { Text("Minute") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
        }
    }
    EatTimesViewModel.updateTime(key,hour.toInt(),minute.toInt())
}

fun getInitialTriggerTime(hour: Int, minute: Int): Long {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)

        // If the time has already passed today, schedule for tomorrow
        if (timeInMillis <= System.currentTimeMillis()) {
            add(Calendar.DAY_OF_YEAR, 1)
        }
    }
    return calendar.timeInMillis
}