package com.gcgenome.lims.analysis

import java.nio.file.Files
import java.nio.file.Path

interface FileReader {
    fun chkFormat(file: Path): Boolean
    fun exec(file: Path)
    fun read(path: Path): List<MutableMap<String, String?>> {
        val headers = Files.lines(path, Charsets.UTF_8).limit(1).flatMap { line -> line.split("\t").stream() }
            .map { word -> word.trim().lowercase() }.toList()
        return Files.readAllLines(path, Charsets.UTF_8)
            .drop(1)
            .filter { line -> line.trim().isNotBlank() }
            .map { line ->
                val map: MutableMap<String, String?> = mutableMapOf()
                line.split("\t").map { word -> word.trim() }.withIndex()
                    .associateTo(map) {
                        val header = headers[it.index].replace(" ", "_")
                        header to it.value
                    }
            }
    }
}