========================
BUILD OUTPUT DESCRIPTION
========================
This program generates a partial Sudoku solution by populating a specified number of spaces the board with randomly selected (permissible) values.  The locations on the board are randomly generated and the number of spaces filled is equal to a specified seed number (default is 16).  This output is displayed (and will constitute the user's puzzle to solve once the UI functionality is done).  

Once the numbers are randomly generated and displayed, the program seeks to work out a permissible solution to the problem. before displaying that solution below the user-view board.  This is ultimately stored in the solution array for later, but the UI hasn't been done and this is a testing protocol only.

If the output solution is the same as the user-viewed puzzle, it means that no solution was possible, given the randomly-seeded board.  This is more likely with a higher seed number.  The time spent should not exceed a second for lower seed numbers, but it is possible the solution could take several seconds to complete.


TO USE:
1) Double-click the file, RunFile.bat.
2) Once the output is viewed, strike a key and press ENTER to close the window.


NOTE: If you want to change the value of the seed, you can do this manually:
1) Right-click on RunFile.bat.
2) Choose Edit to open the file.
3) Modify the numeric value in quotation marks at the end of the line (but keep the number in quotes).  NOTE: Error checking for the seed-values has not been done, but the higher the number, the more likely a hang will occur or a no-solution will be found.  Greater than 81 risks throwing unhandled exceptions.  Don't do it.

I STRONGLY RECOMMEND SEED NUMBERS NO GREATER THAN 24 TO AVOID HANGS, AND SEED NUMBERS 16 OR LOWER TO REDUCE LIKELIHOOD OF NO-SOLUTION OUTCOMES.