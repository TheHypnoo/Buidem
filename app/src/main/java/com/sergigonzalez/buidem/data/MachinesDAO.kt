package com.sergigonzalez.buidem.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import java.time.ZoneId

@Dao
interface MachinesDAO {

    @Query("SELECT * FROM Machines")
    fun getAllMachines(): LiveData<List<Machines>>

    @Query("SELECT * FROM TypeMachines")
    fun getAllTypeMachines(): LiveData<List<TypeMachines>>

    @Query("SELECT * FROM Zones")
    fun getAllZones(): LiveData<List<Zones>>

    @Query("SELECT * FROM Machines WHERE zone = :id")
    fun searchZoneinMachine(id: Int): Boolean

    @Query("SELECT * FROM Machines WHERE TypeMachine = :id")
    fun searchTypeMachineinMachine(id: Int): Boolean

    @Insert
    fun insertMachine(Machines: Machines)

    @Insert
    fun insertTypeMachine(TypeMachines: TypeMachines)

    @Insert
    fun insertZones(Zones: Zones)

    @Delete
    fun deleteMachine(Machines: Machines)

    @Delete
    fun deleteZone(Zones: Zones)

    @Delete
    fun deleteTypeMachine(TypeMachines: TypeMachines)

}