package com.ampro.evemu.emulation

import com.ampro.evemu.util.InternalLog

class SimpleEmulator(val name: String = SimpleEnvironment::class.java.simpleName,
                     val environment: Environment) : Runnable {

    val log = InternalLog(name)

    init {

    }

    override fun run() {
        log.logAndPrint("Initializing Emulator...")
    }

}
