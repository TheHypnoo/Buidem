package com.sergigonzalez.buidem.data

import androidx.room.*
import java.io.Serializable

@Entity(
    tableName = "Machines",
    foreignKeys = [ForeignKey(
        entity = Zones::class,
        parentColumns = arrayOf("_id"),
        childColumns = arrayOf("zone"),
        onDelete = ForeignKey.CASCADE
    ),
        ForeignKey(
            entity = TypeMachines::class,
            parentColumns = arrayOf("_id"),
            childColumns = arrayOf("typeMachine"),
            onDelete = ForeignKey.CASCADE
        )],
    indices = [Index("_id", unique = true),
        Index("serialNumberMachine", unique = true)]
)

data class Machines(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var _id: Int = 0,
    @ColumnInfo(name = "nameClient")
    var nameClient: String = "nameClient",
    @ColumnInfo(name = "addressMachine")
    var addressMachine: String = "addressMachine",
    @ColumnInfo(name = "postalCodeMachine")
    var postalCodeMachine: String = "postalCodeMachine",
    @ColumnInfo(name = "townMachine")
    var townMachine: String = "townMachine",
    @ColumnInfo(name = "phoneContact")
    var phoneContact: String = "phoneContact",
    @ColumnInfo(name = "emailContact")
    var emailContact: String = "emailContact",
    @ColumnInfo(name = "serialNumberMachine")
    var serialNumberMachine: String = "serialNumberMachine",
    @ColumnInfo(name = "lastRevisionDateMachine")
    var lastRevisionDateMachine: String = "lastRevisionDateMachine",
    //ForeignKey
    @ColumnInfo(name = "typeMachine")
    var typeMachine: Int = 0,
    @ColumnInfo(name = "zone")
    var zone: Int = 0,
) : Serializable