package com.sergigonzalez.buidem.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket

object InternetCheck {
    fun isNetworkAvailable(): Boolean {
        var verification = false
        CoroutineScope(Dispatchers.Default).launch {
            try {
                val sock = Socket()
                val socketAdress = InetSocketAddress("8.8.8.8", 53)
                sock.connect(socketAdress, 2000)
                sock.close()
                verification = true
            } catch (e: IOException) {
                false
            }
        }
        return verification
    }
}