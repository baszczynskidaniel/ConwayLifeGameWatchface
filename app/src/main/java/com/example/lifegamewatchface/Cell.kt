package com.example.lifegamewatchface

import java.util.Stack

class Cell{
    var state: CellState = CellState.DEAD
    var lifetime: Int = 0
    private set(value) {
        field = value
    }

    constructor(state: CellState){
        this.state = state
    }

    fun incLifetime(): Unit{
        lifetime++
    }
    fun resetLifetime(): Unit{
        lifetime = 0
    }
    fun toggleState(): Unit{
        if (state == CellState.DEAD)
            state = CellState.ALIVE
        else
            state = CellState.DEAD
    }
    fun update(numberOfNeighbours: Int): Unit
    {

        if (state == CellState.DEAD)
        {
            if(numberOfNeighbours == 3)
            {
                state = CellState.ALIVE
                lifetime = 1
            }
        }
        else
        {
            if(numberOfNeighbours == 2 || numberOfNeighbours == 3)
            {
                lifetime++
            }
            else
            {
                state = CellState.DEAD
                lifetime = 0
            }

        }



    }
}