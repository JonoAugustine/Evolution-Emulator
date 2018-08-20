package com.ampro.evemu.util

import com.ampro.evemu.util.io.DIR_LOGS
import com.ampro.evemu.util.io.toFile
import jdk.nashorn.internal.ir.annotations.Ignore
import kotlinx.coroutines.experimental.newSingleThreadContext
import java.io.PrintStream

val threadName: String get() = Thread.currentThread().name

fun slog(any: Any = "") = println("[$threadName] $any")
fun elog(any: Any = "") = System.err.println("[$threadName] $any")

data class Slogger(val name: String = "") {
    fun slog(any: Any = "") = println("[$name] [$threadName] $any")
    fun elog(any: Any = "") = System.err.print("[$name] [$threadName] $any")
}

/**
 * A Logger that keeps all messages sent to it in an internal list.
 * The logger does not print a message passed to it unless the
 * InternalLog#logAndPrint() function is used.
 *
 * @author Jonathan Augustine
 * @since 3.0
 */
class InternalLog(val name: String = "log", initSize: Int = 100_000,
                  var showName: Boolean = false, var showThread: Boolean = false) {

    /** A log message that can be an err or a normal message */
    data class Message(val any: Any, val err: Boolean = false,
                       val logName: String = "", val thread: String = "") {
        override fun toString(): String
                = "${if (logName.isBlank()) "" else "[$logName]"} " +
                "${if (thread.isBlank()) "" else "[$thread]"} " +
                "[${if (err) "err" else "info"}] $any"
    }

    @Ignore
    val logThread = newSingleThreadContext("LOG")
    val log = ArrayList<Message>(initSize)

    /** Add a Message to the log */
    fun log(any: Any, err: Boolean = false) = synchronized(log) {
        val logname = if (showName) name else ""
        val thread = if (showThread) Thread.currentThread().name else ""
        val message = Message(any, err, logname, thread)
        log.add(message)
        return@synchronized message
    }

    /** Add a line to the log and print to the print stream */
    fun logAndPrint(any: Any, err: Boolean = false) {
        if (!err) println(log(any, err))
        else System.err.println(log(any, err))
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

    /**
     * Saves the log to a file named after the log
     *
     * @return The created File
     */
    fun toFile() = log.toFile("${DIR_LOGS.name}/${name}_$NOW_FILE.log")

    /** Add all lines from an InternalLog into this log */
    fun ingest(src: InternalLog) = synchronized(log) { this.log.addAll(src.log) }

    /** Add all lines from an InternalLog into this log and print them */
    fun ingestAndPrint(src: InternalLog) = synchronized(log) {
        this.log.addAll(src.log)
        src.log.forEach {
            if (!it.err) println(it)
            else System.err.println(it)
        }
    }
}
