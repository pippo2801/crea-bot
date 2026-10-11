package com.pippo2801.universalgamebot.games

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SudokuGridParserTest {
    @Test
    fun parsesZerosDotsAndWhitespace() {
        val input = listOf(
            "5 3 . . 7 . . . .",
            "6 . . 1 9 5 . . .",
            ". 9 8 . . . . 6 .",
            "8 . . . 6 . . . 3",
            "4 . . 8 . 3 . . 1",
            "7 . . . 2 . . . 6",
            ". 6 . . . . 2 8 .",
            ". . . 4 1 9 . . 5",
            ". . . . 8 . . 7 9"
        ).joinToString("\n")
        val result = SudokuGridParser.parse(input)
        assertTrue(result.isValid)
        assertEquals(9, result.grid!!.size)
        assertEquals(0, result.grid!![0][2])
        assertEquals(5, result.grid!![0][0])
    }

    @Test
    fun rejectsWrongNumberOfRowsAndWrongRowLength() {
        assertEquals(
            SudokuGridParser.Error.INVALID_ROW_COUNT,
            SudokuGridParser.parse("123456789").error
        )
        val shortRow = (1..9).joinToString("\n") { if (it == 1) "12345678" else "000000000" }
        assertEquals(SudokuGridParser.Error.INVALID_ROW_LENGTH, SudokuGridParser.parse(shortRow).error)
    }

    @Test
    fun doesNotSilentlyIgnoreLettersOrPunctuation() {
        val invalid = List(9) { "000000000" }.toMutableList()
        invalid[0] = "00000x000"
        assertEquals(
            SudokuGridParser.Error.INVALID_CHARACTER,
            SudokuGridParser.parse(invalid.joinToString("\n")).error
        )
    }

    @Test
    fun rejectsValuesOutsideSingleDigitRange() {
        val invalid = List(9) { "000000000" }.toMutableList()
        invalid[0] = "1234567890"
        assertEquals(SudokuGridParser.Error.INVALID_ROW_LENGTH, SudokuGridParser.parse(invalid.joinToString("\n")).error)
    }
}
