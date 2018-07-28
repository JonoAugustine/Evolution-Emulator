package com.ampro.evemu.util

import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.newSingleThreadContext
import java.io.PrintStream


fun slog(any: Any = "") = println("[${Thread.currentThread().name}] $any")
fun elog(any: Any = "") = System.err.println("[${Thread.currentThread().name}] $any")

class InternalLog(val name: String = "log", initSize: Int = 100_000) {
    /** A log message that can be an err or a normal message */
    data class Message(val any: Any, val err: Boolean = false) {
        override fun toString(): String = "[${if (err) "err" else "info"}] $any"
    }
    val logThread = newSingleThreadContext("LOG")
    val log = ArrayList<Message>(initSize)
    /** Add a Message to the log */
    fun log(any: Any, err: Boolean = false) = async(logThread) {
        synchronized(log) {
            log.add(Message(any, err))
        }
    }
    /**
     * Print each Message on it's own line.
     *
     * @param printStream The PrintStream for non-err messages (default = System.out)
     * @param errStream The PrintStream for err messages (default = System.err)
     */
    fun print(printStream: PrintStream = System.out,
              errStream: PrintStream = System.err) {
        log.forEach {
            if (!it.err) errStream.println(it)
            else printStream.println(it)
        }
    }
}
