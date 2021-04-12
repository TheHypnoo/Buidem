package com.sergigonzalez.buidem.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MachinesDAO {

    @Query("SELECT * FROM Machines")
    fun getAllMachines(): LiveData<List<Machines>>

    @Query("SELECT * FROM TypeMachines")
    fun getAllTypeMachines(): LiveData<List<TypeMachines>>

    @Query("SELECT * FROM Zones")
    fun getAllZones(): LiveData<List<Zones>>

    @Query("SELECT * FROM Machines ORDER BY CASE WHEN :order = 1 THEN nameClient END DESC, CASE WHEN :order = 2 THEN nameClient END ASC, CASE WHEN :order = 3 THEN zone END DESC, CASE WHEN :order = 4 THEN zone END ASC, CASE WHEN :order = 5 THEN townMachine END DESC, CASE WHEN :order = 6 THEN townMachine END ASC, CASE WHEN :order = 7 THEN addressMachine END DESC,  CASE WHEN :order = 8 THEN addressMachine END ASC,  CASE WHEN :order = 9 THEN lastRevisionDateMachine END DESC,  CASE WHEN :order = 10 THEN lastRevisionDateMachine END ASC")
    fun getAllMachinesOrder(order: Int): LiveData<List<Machines>>

    @Query("SELECT * FROM Machines WHERE zone = :id")
    fun searchZoneinMachine(id: Int): Boolean

    @Query("SELECT * FROM Machines WHERE TypeMachine = :id")
    fun searchTypeMachineinMachine(id: Int): Boolean

    @Query("SELECT * FROM Machines WHERE serialNumberMachine LIKE '%' || :name || '%'")
    fun searchSerialNumberMachine(name: String): LiveData<List<Machines>>

    @Query("SELECT * FROM TypeMachines WHERE _id = :id")
    fun searchColorTypeMachine(id: Int): TypeMachines

    @Query("SELECT * FROM Machines WHERE zone = :id")
    fun searchMachinesbyIDZone(id: Int): List<Machines>

    @Query("SELECT * FROM TypeMachines WHERE _id = :id")
    fun searchTypeMachinebyID(id: Int): TypeMachines

    @Query("SELECT * FROM Zones WHERE _id = :id")
    fun searchZonebyID(id: Int): Zones

    @Insert
    fun insertMachine(Machines: Machines)

    @Update
    fun updateMachine(Machines: Machines)

    @Insert
    fun insertTypeMachine(TypeMachines: TypeMachines)

    @Update
    fun updateTypeMachine(TypeMachines: TypeMachines)

    @Insert
    fun insertZones(Zones: Zones)

    @Update
    fun updateZone(Zones: Zones)

    @Delete
    fun deleteMachine(Machines: Machines)

    @Delete
    fun deleteZone(Zones: Zones)

    @Delete
    fun deleteTypeMachine(TypeMachines: TypeMachines)

}