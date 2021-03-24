package com.sergigonzalez.buidem.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Machines::class, Zones::class, TypeMachines::class],
    version = 1,
    exportSchema = false
)
abstract class MachinesApplication : RoomDatabase() {

    abstract fun MachinesApplication(): MachinesDAO

    companion object {
        @Volatile
        private var INSTANCE: MachinesApplication? = null

        fun getDatabase(context: Context): MachinesApplication {
            val tempInstance = INSTANCE

            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MachinesApplication::class.java,
                    "Machines"
                ).build()

                INSTANCE = instance

                return instance
            }
        }
    }
}