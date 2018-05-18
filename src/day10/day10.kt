package day10

import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFails

class Day10 {
    fun run() {
        println(" --- Running Day 10 --- ")
        val input: String = getInput()
        println("Part 1: " + solution1(input))
        println("Part 2: " + solution2(input))
    }

    private fun solution2(input: String): Any? {
        val list: MutableList<Byte> = input.toByteArray().toMutableList()
        list.addAll("17,31,73,47,23".split(",").map { s -> s.toByte() })

        var pos = 0
        var skip = 0
        var sparseHash: List<Int> = (0..255).toList()
        for (round in (0 until 64)) { // Run 64 times
            for (len in list) {
                sparseHash = rotateLeft(sparseHash, pos)
                sparseHash = reverse(sparseHash, len.toInt())
                sparseHash = rotateRight(sparseHash, pos)

                pos += len + skip++
                pos %= sparseHash.size
            }
        }

        val denseHash: MutableList<Int> = mutableListOf()
        for (i in (0 until 16)) {
            denseHash.add(sparseHash.subList(16 * i + 1, 16 * (i + 1))
                    .foldRight(sparseHash[16 * i]) { acc, elem -> acc.xor(elem) })
        }

        return denseHash.fold("") { s, i -> s + java.lang.Integer.toHexString(i) }
    }

    private fun solution1(input: String): Int {
        var pos = 0
        val list: List<Int> = input.split(",").map { s -> s.toInt() }
        var numbers: List<Int> = (0..255).toList()

        for ((skip, len) in list.withIndex()) {
            numbers = rotateLeft(numbers, pos)
            numbers = reverse(numbers, len)
            numbers = rotateRight(numbers, pos)

            pos += len + skip
            pos %= numbers.size
        }
        return numbers[0] * numbers[1]
    }

    private fun getInput(): String {
        return File("src/day10/input.txt")
                .inputStream()
                .bufferedReader()
                .use { it.readText() }
    }

    private fun reverse(list: List<Int>, len: Int): List<Int> {
        if (list.isEmpty()) return emptyList()

        var tempList: MutableList<Int> = mutableListOf()
        for (i in 0 until len) {
            tempList.add(list[(i) % list.size])
        }
        tempList = tempList.asReversed()
        tempList.addAll(list.subList(len, list.size))
        return tempList.toList()
    }

    private fun <T> rotateLeft(list: List<T>, n: Int): List<T> {
        val newList: MutableList<T> = mutableListOf()
        for (i in (0 until list.size)) {
            newList.add(list[(i + n) % list.size])
        }
        return newList.toList()
    }

    private fun <T> rotateRight(list: List<T>, n: Int): List<T> {
        return rotateLeft(list, list.size - n)
    }

    @Nested
    inner class Day10Test {
        @Test
        fun `basic reverse`() {
            assertEquals(listOf(2, 1, 0, 3), reverse(listOf(0, 1, 2, 3), 3))
        }

        @Test
        fun `empty reverse returns empty list`() {
            assertEquals(emptyList(), reverse(emptyList(), 3))
        }

        @Test
        fun `basic rotate left`() {
            assertEquals(listOf(2, 3, 0, 1), rotateLeft(listOf(0, 1, 2, 3), 2))
        }

        @Test
        fun `full rotate left`() {
            assertEquals(listOf(0, 1, 2, 3), rotateLeft(listOf(0, 1, 2, 3), 4))
        }

        @Test
        fun `basic rotate right`() {
            assertEquals(listOf(0, 1, 2, 3), rotateRight(listOf(2, 3, 0, 1), 2))
        }

        @Nested
        inner class Errors {
            @Test
            fun `overlapping reverse throws error`() {
                assertFails { reverse(listOf(0, 1, 2, 3), 6) }
            }
        }

    }
}



