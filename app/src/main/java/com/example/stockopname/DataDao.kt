package com.example.stockopname

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DataDao {
    @Insert
    suspend fun insert(data:DataEntity)

    @Query("SELECT * FROM data")
    suspend fun fetch():List<DataEntity>

    @Query("DELETE FROM data")
    suspend fun deleteAll()
}