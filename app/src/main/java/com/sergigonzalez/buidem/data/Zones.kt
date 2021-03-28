package com.sergigonzalez.buidem.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "Zones",indices = [Index(value = [ "_id"], unique = true)]
)
data class Zones(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var _id: Int = 0,
    @ColumnInfo(name = "nameZone")
    var nameZone: String = "nameZone",
) : Serializable