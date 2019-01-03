========================
BUILD OUTPUT DESCRIPTION
========================
This program generates a Sudoku puzzle for the user and displays a small portion of it to a JavaFX window for the user, who can then complete it.  The entire board is traversible with arrow keys and it provides minor assistance to the user by indicating if a Sudoku rule is broken (a number is duplicated in a row, column, or 3x3 grid) by turning the just entered value red.  A timer will show the puzzle-elapsed time, so the user can see how long s/he has been working this puzzle.  Two buttons exist: one will check the user's solution for accuracy and report on it; and the other allows the user to concede and quit.

This entire project was an exercise in trying to figure out how to generate Sudoku puzzles from scratch purely for my own edification.  The generator will randomly place sixteen numbers (which do not violate Sudoku rules) into an array at random locations and use a backtrack method to fill in the solution.  A backtrack method is only allowed to run for 5 seconds, after which time a pre-generated puzzle will be substituted/shown.  In development, this appeared to happen periodically - about once every 10-13 runs, as I recall.

In the code, I have several debug messages printed to the console.  These are commented out now, but they will display which step of the generation process the program is on.


TO USE:
1) Double-click the file, SudokuGame.jar.  Alternatively, to run from command line, RunFile.bat.
2) Once the output is viewed, play.  Good luck!


NOTE: If you want to change the value of the seed from the command line, you can do this manually:
1) Right-click on RunFile.bat.
2) Choose Edit to open the file.
3) Modify the numeric value in quotation marks at the end of the line (but keep the number in quotes).  NOTE: Error checking for the seed-values has not been done, but the higher the number, the more likely a hang will occur or a no-solution will be found, resulting in substitution of a pre-generated puzzle.  Greater than 81 risks throwing unhandled exceptions.  Please don't do it.

I STRONGLY RECOMMEND USING SEED NUMBERS NO GREATER THAN 24 TO AVOID HANGS, AND SEED NUMBERS 16 OR LOWER TO REDUCE LIKELIHOOD OF NO-SOLUTION OUTCOMES.