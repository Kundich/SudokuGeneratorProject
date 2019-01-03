/*
 * Programmer: Aaron Myers
 * Date: 2018/12/27
 * Purpose: This was an exercise in learning to generate complete Sudoku puzzles.
 *          The generator seeds the puzzle with a specified number of numbers
 *          each placed at a random location not already used on the Sudoku 
 *          solution board.  From there, it employs a backtrack method to
 *          find the complete solution given the seeding.  If the process lasts 
 *          more than about 5 seconds, a pre-generated puzzle is assigned instead
 *          and displayed to the user.  This appears to happen about once every 
 *          12-15 runs because the random seeding, while technically not breaking
 *          any established Sudoku rules, still results in puzzle that cannot
 *          be solved.
 * 
 *          The user will see only a portion of the puzzle, naturally.  The 
 *          number of squares visible to the user is specified at puzzle-gen
 *          time.  
 */
package sdg;

import java.awt.Desktop;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;

// Debug imports.  Remove before final...
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import sui.SudokuUI;

public class SudokuGenerator {

    public static final int MAX_VALUE = 9;
    private static final int MAX_INDEX = MAX_VALUE - 1;
    private static boolean isMagicUnwindVal = false;
    private static boolean isManuallyLoadingPuzzle = false;

    private int[][] puzzleSolution;
    private int[][] puzzleDisplay;
    private int[] availableForRow;
    private int[] availableForBlock;

    // Sets the order to generate each of the blocks.
    private int[] orderToGenerateBlocks;

    private int seed;
    private int hint;

    /**
     * Creates the generator object, which is responsible for generating the
     * puzzle and providing basic logic for it.
     *
     * @param seed The number of numbers placed before the solution is
     * generated.
     * @param hint The number of shown digits when displayed on the UI.
     */
    public SudokuGenerator(int seed, int hint) {
        puzzleSolution = new int[9][9];
        puzzleDisplay = new int[9][9];
        availableForRow = new int[9];
        availableForBlock = new int[9];

        orderToGenerateBlocks = new int[9];

        // Deprecated
        //regenerateAvailableNumbersForRow();
        //regenerateAvailableNumbersForBlock();
        this.seed = seed;
        this.hint = hint;
    }

    /**
     * Test-bench purposes constructor.
     *
     * @param array An array fed to the object. Testing purposes only.
     * @param hints The number of display squares to show.
     */
    public SudokuGenerator(int[][] array, int hints) {
        puzzleSolution = new int[9][9];
        puzzleSolution = array;

        puzzleDisplay = new int[9][9];
        availableForRow = new int[9];
        availableForBlock = new int[9];

        orderToGenerateBlocks = new int[9];

        // Deprecated
        //regenerateAvailableNumbersForRow();
        //regenerateAvailableNumbersForBlock();
        this.hint = hints;

    }

    /**
     * Accessor for the puzzle-solution array.
     *
     * @return The puzzle solution array.
     */
    public int[][] getSolution() {
        return this.puzzleSolution;

    }

    /**
     * Accessor for the user's puzzle-view.
     *
     * @return The user's view array.
     */
    public int[][] getDisplay() {
        return this.puzzleDisplay;

    }

    /**
     * This method controls the generation of the puzzle and presents the puzzle
     * for view by the user. Because the method selected may not result in a
     * successful puzzle every time (most often, but not every), two additional
     * controls are allowed: 1) a loop until a solvable puzzle is found, and 2)
     * a counter when expired drops in a pre-found puzzle into the solution.
     *
     * (NOTE:The second item is not present in the method yet.)
     *
     * @param seed The number of squares to pre-complete before executing the
     * backtracking method.
     * @param hints The number of squares to uncover when displaying to the
     * user.
     * @param game The calling UI.
     */
    public void execute(int seed, int hints, SudokuUI game) {
        // Count from this point how long execution takes and bail out
        // if longer than elapsed. 
        long baseTime = System.currentTimeMillis();

        do {
            // Method chosen here is to use seed-number of randomly selected 
            // numbers at random locations for the generation of the puzzle.
            generatePuzzlePureRandom(seed);

            // Console debug print: TAKE OUT LATER.
            //printArray(puzzleSolution, "INITIAL SEEDING");

            // Using the seeded array, we'll generate the solution via brute-
            // force backward generation.
            createSolution(getSolution(), baseTime, game);

            // Console debug print: TAKE OUT LATER.
            //printArray(puzzleSolution, "PUZZLE SOLUTION");
        } // Do-While check makes sure we haven't hit a seeding that does not allow for 
        // a proper solution. 
        while (!checkIfValidPuzzle());

        // I want the user to specify the number of hints, but the for-loop
        // is set to go to, but not include the hints-number, so we'll just
        // increment here instead of at the for-loop.
        createUserViewPuzzle(hints + 1);

        // Console debug print: TAKE OUT LATER.
        //printArray(puzzleDisplay, "USER VIEW");

    }

    /**
     * Regenerate the array to include all Sudoku numbers, available for the
     * row.
     * <br><br>
     * 20181227: This was used in the initial diagonal seeding method and before
     * the development of the isSafeColOrRow and isSafeBlock methods for error
     * detection.
     */
    @Deprecated
    public void regenerateAvailableNumbersForRow() {
        for (int i = 0; i < this.availableForRow.length; i++) {
            availableForRow[i] = i + 1;

        }

    }

    /**
     * Regenerate the array to include all Sudoku numbers, available for the
     * column.
     * <br><br>
     * 20181227: This was used in the initial diagonal seeding method and before
     * the development of the isSafeColOrRow and isSafeBlock methods for error
     * detection.
     */
    @Deprecated
    public void regenerateAvailableNumbersForBlock() {
        for (int i = 0; i < this.availableForBlock.length; i++) {
            availableForBlock[i] = i + 1;

        }

    }

    /**
     * This method generates the base of the puzzle by inserting values along
     * the diagonal indices of the grid. From here, this serves as the base to
     * execute the final completion of the puzzle.
     * <br><br>
     * 20181227: this was my earliest idea for generating a solution. It would
     * result in a properly seeded puzzle along the diagonal axes, but with the
     * pure random generation, there seems to be little need to use this method.
     */
    @Deprecated
    public void generateBasePuzzle() {
        // The value randomly selected to be an int between 1 and MAX_VALUE.  
        // Allows for puzzles greater than 1 to 9.
        int val = 0;

        // Tracks unique numbers throughout the generation phase.
        boolean isUnique = false;

        // Stores currently used values throughout the generation phase.
        ArrayList<Integer> usedArray = new ArrayList<Integer>(SudokuGenerator.MAX_VALUE);

        // <editor-fold desc="Step 1: Top-down Diagonal">
        // Step 1: Generate and place the top-down diagonal values.
        for (int i = 0; i < SudokuGenerator.MAX_VALUE; i++) {
            // Make sure we're set here for the next iteration.
            isUnique = false;

            // Generate a random number not already contained in usedArray.
            while (!isUnique) {
                val = 1 + (int) (Math.random() * SudokuGenerator.MAX_VALUE);

                if (!usedArray.contains(val)) {
                    puzzleSolution[i][i] = val;
                    usedArray.add(val);
                    isUnique = true;

                }

            }

        }

    }

    /**
     * This method creates the base seeding for the puzzle by creating a random
     * Sudoku value at a random position on the board. Without a seed greater
     * than 0, the puzzle would always result in the same solution because of
     * the way the backtracking works. Higher seeds mean more varied solution
     * set, but seed values greater than about 24 result in frequent hangs (and
     * hence more often a use of pre-generated puzzles, which is obviously not
     * the goal).
     *
     * @param seed The number of array locations required to give a starting
     * point for the puzzle.
     */
    public void generatePuzzlePureRandom(int seed) {
        int row = 0;
        int col = 0;
        int val = 0;
        boolean isEmptySq = true;

        // Iterate and fill the array at a random, unfilled location with a
        // random value.
        for (int i = 0; i < seed; i++) {
            // Get a position by selecting a random row and column.
            row = 0 + (int) (Math.random() * SudokuGenerator.MAX_VALUE);
            col = 0 + (int) (Math.random() * SudokuGenerator.MAX_VALUE);

            // The value, if already placed, must result in a hint not
            // being consumed and the remainder of the loop-code skipped.
            if (puzzleSolution[row][col] != 0) {
                i--;
                continue;

            }

            // Initial value to place.
            val = 0 + (int) (Math.random() * SudokuGenerator.MAX_VALUE);

            // Loop if the value is no-joy: not safe because of clashes with row,
            // column, or block.
            while (!isSafeBlock(puzzleSolution, val, row, col)
                    || !isSafeColOrRow(puzzleSolution, val, row, col)) {
                // Value wasn't safe, so let's try a different random.
                val = 0 + (int) (Math.random() * SudokuGenerator.MAX_VALUE);

            }

            // Because the square is empty, and because the placement violate no 
            // Sudoku rules, set the value.
            puzzleSolution[row][col] = val;

        }

    }

    /**
     * NOTE: This method appears to be a candidate for some optimization because
     * it iterates fully through the entire puzzle again to find a space rather
     * than keeping track of where we left off and starting there.
     *
     * @param workingSolution The array we're testing.
     * @param baseTime Indicates the starting time of the game generation, and
     * is used to calculate elapsed time in puzzleGen. If this exceeds a certain
     * threshold, we'll simply display a pre-generated puzzle.
     * @param game The place to display the puzzle.
     * @return Recursively, returns true if the current placement does not break
     * any rules, and false if it does. Also returns true (solved) once no
     * additional open spaces remain in the array.
     */
    public boolean createSolution(int[][] workingSolution, long baseTime, SudokuUI game) {
        // Used to track the index-row of the currently considered square.
        int row = -1;

        // Used to track the index-col of the currently considered square.
        int col = -1;

        // A quick test of whether the square can be considered.
        // 2018.12.24: Consider changing the name from isEmptySq to isFilledSq
        // to make it read more logically.
        boolean isEmptySq = true;

        // If we've detected that the time on recursion is too long AND we've
        // not yet started manually loading the puzzle, let's do this...
        if (isMagicUnwindVal && !isManuallyLoadingPuzzle) {
            // The unspooling of this method is problematic...  It will run many, many times
            // leading to some weird puzzle output results.
            showPreGeneratedPuzzle(game);

            // Flip this flag so we don't have to showPreGeneratedPuzzle again.
            SudokuGenerator.isManuallyLoadingPuzzle = true;

            // Begin unwinding the recursion.
            return false;

        }

        // Quick time check here!!!  Greater than 5 seconds, let's unravel and 
        // display a pre-gen puzzle.
        long elapsedTime = System.currentTimeMillis();
        if (elapsedTime - baseTime > 5000) {
            // Initially let the method know that we're passed the allowed time,
            // so it's time to begin unwinding recursion.
            SudokuGenerator.isMagicUnwindVal = true;

            // Start unwinding.
            return false;

        }

        // Begin at the left- and top-most cell and iterate the array, looking
        // for the first instance of an unassigned (0) cell.  NOTE: Could benefit
        // from some optimization by potentially adding additional parameters
        // to track the starting point of the array when passed by the caller.
        for (int i = 0; i < SudokuGenerator.MAX_VALUE; i++) {
            for (int j = 0; j < SudokuGenerator.MAX_VALUE; j++) {
                // Look for an empty space (translated, a 0-value).
                if (workingSolution[i][j] == 0) {
                    row = i;
                    col = j;
                    isEmptySq = false;
                    break;

                }

            }

            // If we find an empty square, we don't need to search further.
            // One will do.
            if (!isEmptySq) {
                break;

            }

        }

        // We assume that this is true unless we find that empty square.  If
        // it stays true to this point, we're finally at the end of the puzzle.
        if (isEmptySq) {
            // Could easily return isEmptySq, but this way makes it
            // a little easier to read.
            return true;

        }

        // Loop exists to insert a value into the empty square detected earlier,
        // starting with 1.  Recursion happens here, so the placement of a safe 
        // number allows the method to re-call itself using the next open
        // space (assuming this current one is now part of it's solution).
        for (int testVal = 1; testVal <= SudokuGenerator.MAX_VALUE; testVal++) {
            // If the testVal violates no rules...
            if (isSafeBlock(workingSolution, testVal, row, col)
                    && isSafeColOrRow(workingSolution, testVal, row, col)) {
                // Set the current index to the test value.
                workingSolution[row][col] = testVal;

                // Test by passing forward if this, combined with the next 
                // placement, constitute a safe move.  If so, we can iterate 
                // forward from here.
                if (createSolution(workingSolution, baseTime, game)) {
                    return true;

                } else {
                    // Reset the tested value to 0 because it don't work.
                    workingSolution[row][col] = 0;

                }

            }

        }

        // Didn't quite find a solution (or the testVal did not result in a 
        // proper part of the solution, so let's back up one level of recursion.
        return false;

    }

    /**
     * Checks all columns and rows in the passed array containing the specified
     * cell[row][col] for a value matching the origin cell's value. If a
     * matching value is found, it means that either the column or row is not a
     * safe placement for the value (the value would violate a rule of Sudoku).
     *
     * @param workingSolution The array to consider.
     * @param val The value to check for safety.
     * @param row The row where the target cell is located.
     * @param col The column where the target cell is located.
     * @return True, if both row and column are safe; false if either row or
     * column are unsafe.
     */
    public boolean isSafeColOrRow(int[][] workingSolution, int val, int row, int col) {
        // Check columns first.
        for (int i = 0; i < SudokuGenerator.MAX_VALUE; i++) {
            if (val == workingSolution[row][i]) {
                return false;
            }

        }

        for (int i = 0; i < SudokuGenerator.MAX_VALUE; i++) {
            if (val == workingSolution[i][col]) {
                return false;

            }

        }

        return true;

    }

    /**
     * This method determines whether the value specified in a Sudoku array of
     * MAX_VALUE length x MAX_VALUE length is contained within the specified
     * cell's current block or whether we have box-clash.
     *
     * @param workingSolution The current array to be tested.
     * @param val The value we're comparing against others in the same block.
     * @param row The row in the array representing the row-location of val.
     * @param col The column in the array representing the column-location of
     * val.
     * @return If a duplicate for val is detected within the method, false; and
     * if the method runs to completion without detecting a duplicate, true.
     */
    public boolean isSafeBlock(int[][] workingSolution, int val, int row, int col) {
        // Get the square root of the board's max value.
        int gridCount = (int) Math.sqrt(SudokuGenerator.MAX_VALUE);

        // The block's row is the current search row divided by the grid's root.  
        // This generates results on a normal board of 0, 1, and 2.  This is
        // also true for the block's column, as well.
        int searchBlockRow = row / gridCount;
        int searchBlockCol = col / gridCount;

        // Start the iteration.  The tracker will be 3 times the value 
        // calculated above, which should yield results on a normal board of
        // 0, 3, and 6 for both inner and outer loops.
        for (int i = 3 * searchBlockRow; i < searchBlockRow * gridCount + gridCount; i++) {
            // Inner iteration.
            for (int j = 3 * searchBlockCol; j < searchBlockCol * gridCount + gridCount; j++) {
                // If at any time, I encounter the same value I'm testing, let's
                // return false.
                if (workingSolution[i][j] == val) {
                    return false;
                }

            }

        }

        // We made it here, so we're safe.
        return true;

    }

    /**
     * Print the contents of the two-dimensional array in the format of a Sudoku
     * puzzle. This is a console/debug method only, and will not be used in
     * distribution.
     * <br><br>
     * NOTE: This is really only useful for console-supported debugging. It's
     * ultimately not needed, but I'm not deprecating until I'm permanently done
     * with the project.
     *
     * @param array The array to print to console. Debugging use intended.
     * @param caption A string indicating which portion of the generation is
     * being displayed currently.
     */
    public void printArray(int[][] array, String caption) {
        System.out.println(caption);
        System.out.print(" | - - -  | - - -  | - - - | ");
        System.out.println();

        // Iterate the rows.
        for (int row = 0; row < array.length; row++) {
            // Iterate the columns.
            for (int col = 0; col < array[row].length; col++) {

                // If we're at an evenly divisible element, insert | as a 
                // block separator.
                if (col % 3 == 0) {
                    System.out.print(" | ");

                }

                // Print the element.
                if (array[row][col] > 0) {
                    System.out.print(array[row][col] + " ");

                } else {
                    System.out.print("  ");
                }

            }

            System.out.print("|");

            if (row % 3 == 2) {
                System.out.println();
                System.out.print(" | - - -  | - - -  | - - - | ");

            }

            // Get a new line and begin the next row of the array.
            System.out.println();

        }

    }

    /**
     * Creates the array representing what the user will actually see. It is
     * assigns hints-number-of-squares from the puzzleSolution to the
     * puzzleDisplay, where the "uncovered" squares are determined by random row
     * and column values.
     *
     * @param hints The number of squares to show.
     */
    public void createUserViewPuzzle(int hints) {
        int row = 0;
        int col = 0;

        // Begin the iteration and continue until we've repeated uncovered the
        // requisite number of squares for the user's view.
        for (int i = 0; i < hints; i++) {
            // RNG for row and column.
            row = (int) (Math.random() * 9);
            col = (int) (Math.random() * 9);

            // Assignment.
            puzzleDisplay[row][col] = puzzleSolution[row][col];

        }

    }

    /**
     * A checker method that iterates the entire puzzleSolution array. If any
     * 0-values are detected, something failed in the puzzle generation space.
     *
     * @return True, if no zeroes are detected; false, at the first zero
     * detected.
     */
    public boolean checkIfValidPuzzle() {
        for (int i = 0; i < SudokuGenerator.MAX_VALUE; i++) {
            for (int j = 0; j < SudokuGenerator.MAX_VALUE; j++) {
                // If we encounter a zero-value, the puzzle is not valid,
                // so we have to do it again.
                if (puzzleSolution[i][j] == 0) {
                    return false;
                }

            }

        }

        // If we make it here, the puzzle is valid.
        return true;

    }

    /**
     * Presents a puzzle from a random pre-generated file backup. This method is
     * only called if the time for generation exceeds a specified threshold.
     * Currently, that threshold is 5 seconds.
     *
     * @param game The place we'll display the puzzle selected.
     */
    public void showPreGeneratedPuzzle(SudokuUI game) {
        // Get the number of files currently in the pregen folder.  All are named
        // with a single integer value, beginning with 0, so length will 
        // equal file count.  Length will also be the new file's name, since the
        // names will run to Length - 1.
        String filePath = null;

        // Reset the solution so no holdover artifacts exist.
        puzzleSolution = new int[SudokuGenerator.MAX_VALUE][SudokuGenerator.MAX_VALUE];

        try {
            // There are 101 files, so we want a random between 0 and 101, 
            // inclusive.  This calculation is done in cast I change my mind
            // and put a different amount in the folder or one gets deleted, etc.
            int count = new File("pregen").list().length;
            int file = 0 + (int) (Math.random() * count);

            // The name of the file, including it's relative pathing and extension.
            String filename = "pregen/" + String.valueOf(file) + ".ssf";

            // Create the reader.
            DataInputStream read = new DataInputStream(new FileInputStream(filename));

            // Begin nested-iteration of the puzzleSolution array, reading each value
            // from the selected file into the currently considered position
            // in puzzleSolution array.
            for (int row = 0; row < puzzleSolution.length; row++) {
                for (int col = 0; col < puzzleSolution[row].length; col++) {
                    // Assignment.  All data in the file is int-data.
                    puzzleSolution[row][col] = read.readInt();

                }

            }

            // Good habit: close the stream reader.
            read.close();

            // Debug print.
            //printArray(puzzleSolution, "TESTING FILE I/O");
        } catch (IOException ex) { /* No catch code at this time. */ }

        // Create the user view for the puzzle.
        this.createUserViewPuzzle(hint);

    }

    /**
     * This method generated a single SFF file at a time. Never use it again.
     *
     * @param captureArray The array we're trying to save to file.
     */
    @Deprecated
    public void addPreGeneratedPuzzle(int[][] captureArray) {
        try {
            int count = new File("pregen").list().length;
            String fileName = "pregen/" + String.valueOf(count) + ".ssf";

            DataOutputStream write = new DataOutputStream(new FileOutputStream(fileName));

            for (int row = 0; row < captureArray.length; row++) {
                for (int col = 0; col < captureArray[row].length; col++) {
                    write.writeInt(captureArray[row][col]);
                    //write.writeChar(' ');

                }

                //write.writeChar('\n');
            }

            write.close();
        } catch (IOException ex) {
        }

    }

    /**
     * This method generated (with difficulty) a number of SFF files. NEVER USE
     * IT AGAIN!!!
     *
     * @param game The calling UI piece.
     */
    @Deprecated
    public void addPuzzlesToFolder(SudokuUI game) {
        try {
            int count = new File("pregen").list().length;

            while (count < 100) {
                // Count the number of files currently in the pregen folder.  
                // This effectively increments with every iteration, so it
                // provides a natural kill-switch to the while-loop.
                count = new File("pregen").list().length;

                // This is the name of the file we're going to create.  All
                // are labeled with integers only and have the file-type of
                // a made-up .ssf (Sudoku Solution File).
                String fileName = "pregen/" + String.valueOf(count) + ".ssf";

                // Reset puzzleSolution before we get started.
                puzzleSolution = new int[SudokuGenerator.MAX_VALUE][SudokuGenerator.MAX_VALUE];

                // Generate a puzzle solution.  This method will only be used 
                // once, but it will potentially break if a puzzle without a 
                // solution is generated early.
                generatePuzzlePureRandom(seed);

                // Using the seeded array, we'll generate the solution via brute-
                // force backward generation.
                long baseTime = System.currentTimeMillis();
                createSolution(getSolution(), baseTime, game);

                // Create the writer.
                DataOutputStream write = new DataOutputStream(new FileOutputStream(fileName));

                // Write the puzzle to file, one character at a time.
                for (int row = 0; row < getSolution().length; row++) {
                    for (int col = 0; col < getSolution()[row].length; col++) {
                        write.writeInt(getSolution()[row][col]);

                    }

                }

                //printArray(puzzleSolution, "TESTING FILE I/O " + count);
                write.close();

            }

        } catch (IOException ex) {
        }

    }

}
