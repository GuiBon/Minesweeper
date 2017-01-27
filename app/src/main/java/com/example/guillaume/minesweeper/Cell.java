package com.example.guillaume.minesweeper;

public class Cell {

    public enum cellState { Cover, Marked, Uncover }
    private cellState state;
    private boolean mine;
    private int nbMinesTouching;

    public Cell() {
        state = cellState.Cover;
        mine = false;
        nbMinesTouching = 0;
    }

    // Reset the cell to its default value
    public void reset() {
        state = cellState.Cover;
        mine = false;
        nbMinesTouching = 0;
    }

    // Getter and setter

    public void setState(cellState newState) {
        this.state = newState;
    }

    public cellState getState() {
        return (this.state);
    }

    public void setMine() {
        this.mine = true;
    }

    public boolean isAMine() {
        return (this.mine);
    }

    public void setMinesTouching(int mines) {
        this.nbMinesTouching = mines;
    }

    public int getNbMinesTouching() {
        return (this.nbMinesTouching);
    }
}
