package com.example.lifegamewatchface

class Shape {
    private val digit8 = arrayOf<IntArray>(
        intArrayOf(0, 1, 1, 1, 1, 1, 0),
        intArrayOf(1, 1, 1, 1, 1, 1, 1),
        intArrayOf(1, 1, 0, 0, 0, 1, 1),
        intArrayOf(1, 1, 0, 0, 0, 1, 1),
        intArrayOf(1, 1, 0, 0, 0, 1, 1),
        intArrayOf(0, 1, 1, 1, 1, 1, 0),
        intArrayOf(0, 1, 1, 1, 1, 1, 0),
        intArrayOf(1, 1, 0, 0, 0, 1, 1),
        intArrayOf(1, 1, 0, 0, 0, 1, 1),
        intArrayOf(1, 1, 0, 0, 0, 1, 1),
        intArrayOf(1, 1, 1, 1, 1, 1, 1),
        intArrayOf(0, 1, 1, 1, 1, 1, 0),
    )
    private val digit0 = arrayOf<IntArray>(
        intArrayOf(0, 1, 1, 1, 1, 1, 0),
        intArrayOf(1, 1, 1, 1, 1, 1, 1),
        intArrayOf(1, 1, 0, 0, 0, 1, 1),
        intArrayOf(1, 1, 0, 0, 0, 1, 1),
        intArrayOf(1, 1, 0, 0, 0, 1, 1),
        intArrayOf(1, 1, 0, 0, 0, 1, 1),
        intArrayOf(1, 1, 0, 0, 0, 1, 1),
        intArrayOf(1, 1, 0, 0, 0, 1, 1),
        intArrayOf(1, 1, 0, 0, 0, 1, 1),
        intArrayOf(1, 1, 0, 0, 0, 1, 1),
        intArrayOf(1, 1, 1, 1, 1, 1, 1),
        intArrayOf(0, 1, 1, 1, 1, 1, 0),
    )
    private val digit1 = arrayOf<IntArray>(
        intArrayOf(0, 0, 0, 0, 0, 1, 1),
        intArrayOf(0, 0, 0, 0, 1, 1, 1),
        intArrayOf(0, 0, 0, 1, 1, 1, 1),
        intArrayOf(0, 0, 1, 1, 0, 1, 1),
        intArrayOf(0, 0, 1, 0, 0, 1, 1),
        intArrayOf(0, 0, 0, 0, 0, 1, 1),
        intArrayOf(0, 0, 0, 0, 0, 1, 1),
        intArrayOf(0, 0, 0, 0, 0, 1, 1),
        intArrayOf(0, 0, 0, 0, 0, 1, 1),
        intArrayOf(0, 0, 0, 0, 0, 1, 1),
        intArrayOf(0, 0, 0, 0, 0, 1, 1),
        intArrayOf(0, 0, 0, 0, 0, 1, 1),
    )
    private val digit7 = arrayOf<IntArray>(
        intArrayOf(1, 1, 1, 1, 1, 1, 1),
        intArrayOf(1, 1, 1, 1, 1, 1, 1),
        intArrayOf(0, 0, 0, 0, 0, 1, 1),
        intArrayOf(0, 0, 0, 0, 0, 1, 1),
        intArrayOf(0, 0, 0, 0, 0, 1, 1),
        intArrayOf(0, 0, 0, 0, 0, 1, 1),
        intArrayOf(0, 0, 0, 0, 1, 1, 1),
        intArrayOf(0, 0, 0, 1, 1, 1, 0),
        intArrayOf(0, 0, 1, 1, 1, 0, 0),
        intArrayOf(0, 1, 1, 1, 0, 0, 0),
        intArrayOf(1, 1, 1, 0, 0, 0, 0),
        intArrayOf(1, 1, 0, 0, 0, 0, 0),
    )
    private val digit2 = arrayOf<IntArray>(
        intArrayOf(0, 1, 1, 1, 1, 1, 0),
        intArrayOf(1, 1, 1, 1, 1, 1, 1),
        intArrayOf(1, 1, 0, 0, 0, 1, 1),
        intArrayOf(0, 0, 0, 0, 0, 1, 1),
        intArrayOf(0, 0, 0, 0, 0, 1, 1),
        intArrayOf(0, 1, 1, 1, 1, 1, 1),
        intArrayOf(1, 1, 1, 1, 1, 1, 0),
        intArrayOf(1, 1, 0, 0, 0, 0, 0),
        intArrayOf(1, 1, 0, 0, 0, 0, 0),
        intArrayOf(1, 1, 0, 0, 0, 0, 0),
        intArrayOf(1, 1, 1, 1, 1, 1, 1),
        intArrayOf(1, 1, 1, 1, 1, 1, 1),
    )
    private val digit5 = arrayOf<IntArray>(
        intArrayOf(1, 1, 1, 1, 1, 1, 1),
        intArrayOf(1, 1, 1, 1, 1, 1, 1),
        intArrayOf(1, 1, 0, 0, 0, 0, 0),
        intArrayOf(1, 1, 0, 0, 0, 0, 0),
        intArrayOf(1, 1, 0, 0, 0, 0, 0),
        intArrayOf(1, 1, 1, 1, 1, 1, 0),
        intArrayOf(1, 1, 1, 1, 1, 1, 1),
        intArrayOf(0, 0, 0, 0, 0, 1, 1),
        intArrayOf(0, 0, 0, 0, 0, 1, 1),
        intArrayOf(1, 1, 0, 0, 0, 1, 1),
        intArrayOf(1, 1, 1, 1, 1, 1, 1),
        intArrayOf(0, 1, 1, 1, 1, 1, 0),
    )
    private val digit6 = arrayOf<IntArray>(
        intArrayOf(0, 1, 1, 1, 1, 1, 0),
        intArrayOf(1, 1, 1, 1, 1, 1, 1),
        intArrayOf(1, 1, 0, 0, 0, 0, 0),
        intArrayOf(1, 1, 0, 0, 0, 0, 0),
        intArrayOf(1, 1, 0, 0, 0, 0, 0),
        intArrayOf(1, 1, 1, 1, 1, 1, 0),
        intArrayOf(1, 1, 1, 1, 1, 1, 1),
        intArrayOf(1, 1, 0, 0, 0, 1, 1),
        intArrayOf(1, 1, 0, 0, 0, 1, 1),
        intArrayOf(1, 1, 0, 0, 0, 1, 1),
        intArrayOf(1, 1, 1, 1, 1, 1, 1),
        intArrayOf(0, 1, 1, 1, 1, 1, 0),
    )
    private val digit9 = arrayOf<IntArray>(
        intArrayOf(0, 1, 1, 1, 1, 1, 0),
        intArrayOf(1, 1, 1, 1, 1, 1, 1),
        intArrayOf(1, 1, 0, 0, 0, 1, 1),
        intArrayOf(1, 1, 0, 0, 0, 1, 1),
        intArrayOf(1, 1, 0, 0, 0, 1, 1),
        intArrayOf(1, 1, 1, 1, 1, 1, 1),
        intArrayOf(0, 1, 1, 1, 1, 1, 1),
        intArrayOf(0, 0, 0, 0, 0, 1, 1),
        intArrayOf(0, 0, 0, 0, 0, 1, 1),
        intArrayOf(0, 0, 0, 0, 0, 1, 1),
        intArrayOf(1, 1, 1, 1, 1, 1, 1),
        intArrayOf(0, 1, 1, 1, 1, 1, 0),
    )
    private val digit3 = arrayOf<IntArray>(
        intArrayOf(0, 1, 1, 1, 1, 1, 0),
        intArrayOf(1, 1, 1, 1, 1, 1, 1),
        intArrayOf(0, 0, 0, 0, 0, 1, 1),
        intArrayOf(0, 0, 0, 0, 0, 1, 1),
        intArrayOf(0, 0, 0, 0, 0, 1, 1),
        intArrayOf(1, 1, 1, 1, 1, 1, 1),
        intArrayOf(1, 1, 1, 1, 1, 1, 1),
        intArrayOf(0, 0, 0, 0, 0, 1, 1),
        intArrayOf(0, 0, 0, 0, 0, 1, 1),
        intArrayOf(0, 0, 0, 0, 0, 1, 1),
        intArrayOf(1, 1, 1, 1, 1, 1, 1),
        intArrayOf(0, 1, 1, 1, 1, 1, 0),
    )
    private val digit4 = arrayOf<IntArray>(
        intArrayOf(1, 1, 0, 0, 0, 1, 1),
        intArrayOf(1, 1, 0, 0, 0, 1, 1),
        intArrayOf(1, 1, 0, 0, 0, 1, 1),
        intArrayOf(1, 1, 0, 0, 0, 1, 1),
        intArrayOf(1, 1, 0, 0, 0, 1, 1),
        intArrayOf(1, 1, 1, 1, 1, 1, 1),
        intArrayOf(0, 1, 1, 1, 1, 1, 1),
        intArrayOf(0, 0, 0, 0, 0, 1, 1),
        intArrayOf(0, 0, 0, 0, 0, 1, 1),
        intArrayOf(0, 0, 0, 0, 0, 1, 1),
        intArrayOf(0, 0, 0, 0, 0, 1, 1),
        intArrayOf(0, 0, 0, 0, 0, 1, 1),
    )
    private val unknownSymbol = arrayOf<IntArray>(
        intArrayOf(0, 1, 1, 1, 0),
        intArrayOf(1, 0, 0, 0, 1),
        intArrayOf(1, 0, 0, 0, 1),
        intArrayOf(0, 0, 0, 0, 1),
        intArrayOf(0, 0, 0, 1, 0),
        intArrayOf(0, 0, 1, 0, 0),
        intArrayOf(0, 0, 1, 0, 0),
        intArrayOf(0, 0, 0, 0, 0),
        intArrayOf(0, 0, 1, 0, 0),
    )
    var symbolsMap = LinkedHashMap<Int, Array<IntArray>>()
    constructor()
    {
        symbolsMap[0] = digit0
        symbolsMap[1] = digit1
        symbolsMap[2] = digit2
        symbolsMap[3] = digit3
        symbolsMap[4] = digit4
        symbolsMap[5] = digit5
        symbolsMap[6] = digit6
        symbolsMap[7] = digit7
        symbolsMap[9] = digit9
        symbolsMap[8] = digit8
        symbolsMap[-1] = unknownSymbol
    }




}