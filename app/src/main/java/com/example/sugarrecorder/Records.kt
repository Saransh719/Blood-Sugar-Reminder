package com.example.sugarrecorder

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase

@Entity
data class Record(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val reading : Int? = null,
    val date : String,
    val time : String,
    val note : String? = null
)

@Dao
interface RecordsDao {
    @Query("Select * from Record where date=:date")
    fun getReadings(date: String) : LiveData<List<Record>>

    @Insert
    fun saveRecord(record : Record)

    @Delete
    fun deleteRecord(record: Record)

    @Query("update record set reading=:reading,note=:note where id=:id")
    fun update(id: Int, reading: Int?,note: String?)
}

@Database(entities = [Record::class], version = 1, exportSchema = false)
abstract class RecordsDatabase : RoomDatabase(){
    abstract fun RecordsDao() : RecordsDao
}

enum class TimeType(val displayName: String) {
    BEFORE_BREAKFAST("Before Breakfast"),
    AFTER_BREAKFAST("After Breakfast"),
    BEFORE_LUNCH("Before Lunch"),
    AFTER_LUNCH("After Lunch"),
    BEFORE_DINNER("Before Dinner"),
    AFTER_DINNER("After Dinner")
}
val TimeTypes= TimeType.entries.toTypedArray()