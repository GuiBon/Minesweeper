package com.example.guillaume.minesweeper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;
import java.util.Random;

public class CustomView extends View {

    private int squareSize;
    private Paint black;
    private Paint grey;
    private Paint red;
    private Paint yellow;
    private Paint text;
    private Rect square;
    private Cell[][] boardGame;
    private int xPosDown;
    private int yPosDown;
    private boolean gameFinish;
    private boolean isMarkingMode;
    private int nbMarkedMines;
    private int cellRemaining;
    private Observer obs;

    // Default constructor
    public CustomView(Context c) {
        super(c);
        init();
    }

    // Constructor that takes a list of attributes from XML
    public CustomView(Context c, AttributeSet as) {
        super(c, as);
        init();
    }

    // Constructor that takes a list of attributes and a default style
    public CustomView(Context c, AttributeSet as, int default_style) {
        super(c, as, default_style);
        init();
    }

    // Init method
    private void init() {
        black = new Paint(Paint.ANTI_ALIAS_FLAG);
        grey = new Paint(Paint.ANTI_ALIAS_FLAG);
        red = new Paint(Paint.ANTI_ALIAS_FLAG);
        yellow = new Paint(Paint.ANTI_ALIAS_FLAG);
        text = new Paint(Paint.ANTI_ALIAS_FLAG);
        black.setColor(Color.BLACK);
        grey.setColor(Color.GRAY);
        red.setColor(Color.RED);
        yellow.setColor(Color.YELLOW);
        text.setColor(Color.BLACK);
        text.setStyle(Paint.Style.FILL);
        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        square = null;
        gameFinish = false;
        isMarkingMode = false;
        nbMarkedMines = 0;
        cellRemaining = 80;
        obs = null;

        boardGame = new Cell[10][10];
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                boardGame[y][x] = new Cell();
            }
        }
        placeMines();
    }

    // Set the observer to communicate with the callback function of MainActivity
    public void setObserver(Observer observer) {
        obs = observer;
    }

    // Call the callback of MainActivity
    private void editMainActivityText() {
        if (obs != null)
            obs.callback();
    }

    public boolean isMarkingModeActive() {
        return (isMarkingMode);
    }

    public void setMarkingMode(boolean active) {
        isMarkingMode = active;
    }

    // Return the number of cell that are actually marked
    public int getMarkedMines() {
        return (nbMarkedMines);
    }

    // Reset the game
    public void reset() {
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                boardGame[y][x].reset();
            }
        }
        gameFinish = false;
        isMarkingMode = false;
        nbMarkedMines = 0;
        cellRemaining = 80;
        placeMines();
        invalidate();
    }

    // Uncover all the cells when the game is loose
    public void gameOver() {
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                boardGame[y][x].setState(Cell.cellState.Uncover);
            }
        }
        invalidate();
    }

    // Check if a win occurs
    public void checkWin() {
        if (cellRemaining == 0) {
            Toast.makeText(getContext(), "You Win !", Toast.LENGTH_SHORT).show();
            gameFinish = true;
        }
    }

    // When a cell with 0 mine close to it is clicked, this function will uncover
    // the area of cell with 0 mine close to it
    public void discoverNeighbour(int y, int x) {
        for (int xPos = x - 1; xPos <= x + 1; xPos++) {
            int yPos = y - 1;
            if (yPos >= 0 && xPos >= 0 && xPos < 10) {
                if (boardGame[yPos][xPos].getNbMinesTouching() == 0 && boardGame[yPos][xPos].getState() == Cell.cellState.Cover) {
                    boardGame[yPos][xPos].setState(Cell.cellState.Uncover);
                    cellRemaining--;
                    discoverNeighbour(yPos, xPos);
                } else if (boardGame[yPos][xPos].getState() == Cell.cellState.Cover) {
                    boardGame[yPos][xPos].setState(Cell.cellState.Uncover);
                    cellRemaining--;
                }
            }
        }
        if (x - 1 >= 0) {
            if (boardGame[y][x - 1].getNbMinesTouching() == 0 && boardGame[y][x - 1].getState() == Cell.cellState.Cover) {
                boardGame[y][x - 1].setState(Cell.cellState.Uncover);
                cellRemaining--;
                discoverNeighbour(y, x - 1);
            } else if (boardGame[y][x - 1].getState() == Cell.cellState.Cover) {
                boardGame[y][x - 1].setState(Cell.cellState.Uncover);
                cellRemaining--;
            }
        }
        if (x + 1 < 10) {
            if (boardGame[y][x + 1].getNbMinesTouching() == 0 && boardGame[y][x + 1].getState() == Cell.cellState.Cover) {
                boardGame[y][x + 1].setState(Cell.cellState.Uncover);
                cellRemaining--;
                discoverNeighbour(y, x + 1);
            } else if (boardGame[y][x + 1].getState() == Cell.cellState.Cover) {
                boardGame[y][x + 1].setState(Cell.cellState.Uncover);
                cellRemaining--;
            }
        }
        for (int xPos = x - 1; xPos <= x + 1; xPos++) {
            int yPos = y + 1;
            if (yPos < 10 && xPos >= 0 && xPos < 10) {
                if (boardGame[yPos][xPos].getNbMinesTouching() == 0 && boardGame[yPos][xPos].getState() == Cell.cellState.Cover) {
                    boardGame[yPos][xPos].setState(Cell.cellState.Uncover);
                    cellRemaining--;
                    discoverNeighbour(yPos, xPos);
                } else if (boardGame[yPos][xPos].getState() == Cell.cellState.Cover) {
                    boardGame[yPos][xPos].setState(Cell.cellState.Uncover);
                    cellRemaining--;
                }
            }
        }
    }

    // Place mines randomly in our 10x10 board
    public void placeMines() {
        Random r = new Random();
        int xPos;
        int yPos;
        for (int i = 20; i > 0; i--) {
            xPos = r.nextInt(10);
            yPos = r.nextInt(10);
            if (!boardGame[yPos][xPos].isAMine())
                boardGame[yPos][xPos].setMine();
            else
                i++;
        }
        countNeighboursMines();
    }

    // Count the number of mines touching the cells
    public void countNeighboursMines() {
        int mines = 0;

        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                mines += (y - 1 >= 0 && x - 1 >= 0 && boardGame[y - 1][x - 1].isAMine()) ? 1 : 0;
                mines += (y - 1 >= 0 && boardGame[y - 1][x].isAMine()) ? 1 : 0;
                mines += (y - 1 >= 0 && x + 1 < 10 && boardGame[y - 1][x + 1].isAMine()) ? 1 : 0;
                mines += (x - 1 >= 0 && boardGame[y][x - 1].isAMine()) ? 1 : 0;
                mines += (x + 1 < 10 && boardGame[y][x + 1].isAMine()) ? 1 : 0;
                mines += (y + 1 < 10 && x - 1 >= 0 && boardGame[y + 1][x - 1].isAMine()) ? 1 : 0;
                mines += (y + 1 < 10 && boardGame[y + 1][x].isAMine()) ? 1 : 0;
                mines += (y + 1 < 10 && x + 1 < 10 && boardGame[y + 1][x + 1].isAMine()) ? 1 : 0;
                boardGame[y][x].setMinesTouching(mines);
                mines = 0;
            }
        }
    }

    // Resize the custom view to a square
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = getMeasuredWidth();

        setMeasuredDimension(size, size);
    }

    // Draw the contents of a widget
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int xpos;

        canvas.save();
        int squarePadding = 2;
        if (square == null) {
            squareSize = (getWidth() / 10) - squarePadding;
            square = new Rect(0, 0, squareSize, squareSize);
            text.setTextSize(squareSize);
        }
        for (int y = 0; y < 10; y++) {
            xpos = 0;
            for (int x = 0; x < 10; x++) {
                if (boardGame[y][x].getState() == Cell.cellState.Cover)
                    canvas.drawRect(square, black);
                else if (boardGame[y][x].getState() == Cell.cellState.Marked)
                    canvas.drawRect(square, yellow);
                else if (boardGame[y][x].getState() == Cell.cellState.Uncover) {
                    if (boardGame[y][x].isAMine()) {
                        canvas.drawRect(square, red);
                        text.setColor(Color.BLACK);
                        canvas.drawText("M", (squareSize / 12), squareSize - (squareSize / 10), text);
                    }
                    else {
                        canvas.drawRect(square, grey);
                        if (boardGame[y][x].getNbMinesTouching() > 0) {
                            if (boardGame[y][x].getNbMinesTouching() == 1)
                                text.setColor(Color.BLUE);
                            else if (boardGame[y][x].getNbMinesTouching() == 2)
                                text.setColor(Color.GREEN);
                            else if (boardGame[y][x].getNbMinesTouching() == 3)
                                text.setColor(Color.YELLOW);
                            else if (boardGame[y][x].getNbMinesTouching() >= 4)
                                text.setColor(Color.RED);
                            canvas.drawText(String.valueOf(boardGame[y][x].getNbMinesTouching()), (squareSize / 6), squareSize - (squareSize / 10), text);
                        }
                    }
                }
                xpos += squareSize + squarePadding;
                canvas.translate(squareSize + squarePadding, 0);
            }
            canvas.translate(-xpos, squareSize + squarePadding);
        }
        canvas.restore();
    }

    // Handle the touches from a user
    public boolean onTouchEvent(MotionEvent event) {
        if (!gameFinish && event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            xPosDown = (int) (event.getX() / squareSize);
            yPosDown = (int) (event.getY() / squareSize);
            invalidate();
            return (true);
        }
        else if (!gameFinish && event.getActionMasked() == MotionEvent.ACTION_UP) {
            int xPos;
            int yPos;

            xPos = (int) (event.getX() / squareSize);
            yPos = (int) (event.getY() / squareSize);
            if (xPos == xPosDown && yPos == yPosDown && yPos >= 0 && yPos < 10 && xPos >= 0 && xPos < 10) {
                if (boardGame[yPos][xPos].getState() == Cell.cellState.Cover && !isMarkingMode) {
                    boardGame[yPos][xPos].setState(Cell.cellState.Uncover);
                    cellRemaining--;
                    if (boardGame[yPos][xPos].isAMine()) {
                        gameFinish = true;
                        gameOver();
                        Toast.makeText(getContext(), "Game Over", Toast.LENGTH_SHORT).show();
                    }
                    else if (boardGame[yPos][xPos].getNbMinesTouching() == 0) {
                        discoverNeighbour(yPos, xPos);
                        checkWin();
                    }
                    else
                        checkWin();
                }
                else if (boardGame[yPos][xPos].getState() == Cell.cellState.Cover && isMarkingMode) {
                    boardGame[yPos][xPos].setState(Cell.cellState.Marked);
                    nbMarkedMines++;
                    editMainActivityText();
                }
                else if (boardGame[yPos][xPos].getState() == Cell.cellState.Marked && isMarkingMode) {
                    boardGame[yPos][xPos].setState(Cell.cellState.Cover);
                    nbMarkedMines--;
                    editMainActivityText();
                }
            }
            invalidate();
            return (true);
        }
        return (super.onTouchEvent(event));
    }
}
