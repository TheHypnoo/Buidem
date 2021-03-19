package com.sergigonzalez.buidem.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "TypeMachines"
)
class TypeMachines(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var _id: Int = 0,
    @ColumnInfo(name = "typeMachine")
    var typeMachine: String = "typeMachine",
    @ColumnInfo(name = "descriptionTypeMachine")
    var descriptionTypeMachine: String = "descriptionTypeMachine",
    @ColumnInfo(name = "colorTypeMachine")
    var colorTypeMachine: String = "colorTypeMachine"
) : Serializable