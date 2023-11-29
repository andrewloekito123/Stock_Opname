package com.example.stockopname

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data")
data class DataEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val Tanggal: String,
    val KodeBarang: String,
    val Merk: String,
    val Nama: String,
    val StokOpname: Int,
    val Kemasan: String
)
