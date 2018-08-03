package com.ampro.evemu.util.io

import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import java.io.*
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files

val DIR_ROOT  = File("evemu")
val DIR_CONST = File(DIR_ROOT, "constants")
val DIR_ENVIR = File(DIR_ROOT, "environments")
val DIR_LOGS  = File(DIR_ROOT, "logs")

val GSON = GsonBuilder().enableComplexMapKeySerialization()
    .setExclusionStrategies().setPrettyPrinting().create()

/**
 * Read a file's text.
 * @param file The file to read
 * @return A String Array, each index representing a line in the file
 * @throws IOException
 */
@Throws(IOException::class)
fun readFile(file: File): List<String> = Files.readAllLines(file.toPath())

fun BufferedWriter.writeLn(line: Any) {
    this.write(line.toString())
    this.newLine()
}

/**
 * Print the List to file, each index its own line.
 *
 * @param name The name of the file.
 * @return The [File] made or `null` if unable to create.
 * @throws FileAlreadyExistsException
 */
@Throws(FileAlreadyExistsException::class)
fun List<Any>.toFile(name: String = "file") : File {
    val file = File(DIR_ROOT, name)
    //Leave if the file already exists
    if (file.exists()) {
        throw FileAlreadyExistsException("File '$name' already exists.")
    } else {
        file.createNewFile()
        val bw = file.bufferedWriter()
        this.forEach { bw.writeLn(it) }
        bw.close()
    }
    return file
}

/**
 * Save an object to a json file
 *
 * @param file The file to save to
 * @return 0 if the file was saved.
 *        -1 if an IO error occurred.
 *        -2 if a Gson exception was thrown
 */
@Synchronized
fun Any.saveJson(file: File): Int {
    try {
        val fw = FileWriter(file)
        GSON.toJson(this, fw)
        fw.close()
    } catch (e: FileNotFoundException) {
        System.err.println("File not found while writing gson to file.")
        return -1
    } catch (e: IOException) {
        System.err.println("IOException while writing gson to file.")
        return -1
    }
    return 0
}

/**
 * Load the given file form JSON.
 *
 * @param file The file to load from
 * @param objClass The object type
 * @return The parsed object or null if it was not found or an exception was thrown.
 */
@Synchronized
inline fun <reified T> loadJson(file: File): T? {
    try {
        FileReader(file).use { reader -> return GSON.fromJson(reader, T::class.java) }
    } catch (e: FileNotFoundException) {
        System.err.println("Unable to locate file '${file.name}'")
        return null
    } catch (e: IOException) {
        System.err.println("IOException while reading json '${file.name}'.")
        return null
    }
}

/**
 * @return `false` if gson fails to read the file.
 */
@Synchronized
internal fun corruptJsonTest(file: File, objcClass: Class<*>): Boolean {
    try {
        FileReader(file).use { reader -> GSON.fromJson(reader, objcClass) }
    } catch (e: JsonSyntaxException) {
        e.printStackTrace()
        return false
    } catch (e: IOException) {
        return true
    }
    return true
}

