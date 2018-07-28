package com.ampro.evemu.util

import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.newSingleThreadContext
import java.io.PrintStream


fun slog(any: Any = "") = println("[${Thread.currentThread().name}] $any")
fun elog(any: Any = "") = System.err.println("[${Thread.currentThread().name}] $any")

/**
 * A Logger that keeps all messages sent to it in an internal list.
 * The logger does not print a message passed to it unless the
 * InternalLog#logAndPrint() function is used.
 *
 * @author Jonathan Augustine
 * @since 3.0
 */
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

    fun logAndPrint(any: Any, err: Boolean = false) {
        log(any, err)
        if (!err) println("[$name] $any")
        else System.err.print("[$name] $any")
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
            if (!it.err) errStream.println("[$name] $it")
            else printStream.println("[$name] $it")
        }
    }
}
