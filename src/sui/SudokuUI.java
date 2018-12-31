/*
 * Programmer: Aaron Myers
 * Date: 2018/12/28
 * Purpose: The entire purpose of this program was to figure out how to generate
 *          a Sudoku puzzle from scratch.  A UI was really just an afterthought
 *          to make the generation result...useful.  This application does
 *          serve as a very simple version of the Sudoku game.
 *
 *          The application features 81 textfields into which the user must
 *          place correct solution-values.  In the most basic form, the UI will 
 *          show any placement that violates a Sudoku rule (no duplicate values
 *          in the same row, the same column, or the same 3x3 grid) as a red 
 *          text to alert the user that s/he has messed up.  This feature does
 *          not ensure that the value is the correct value, however.  That
 *          cannot be determiend for certain until the 'Commit' button is 
 *          pressed.
 *          
 *          The commit button will check to ensure that all spaces are completed
 *          with Sudoku-valid values, and then compare against the generated
 *          puzzle-solution.  A match indicates successful completion.
 * 
 *          The surrender button simply exits.  I decided not to show the 
 *          completed puzzle upon surrender.
 *
 *          A time counts up just above the buttons to show time elapsed since 
 *          the start of the game.
 */
package sui;

import java.util.Timer;
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import java.util.Optional;
import javafx.stage.Stage;

// Pane imports.
import javafx.scene.layout.Pane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

// Control imports
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

// Rcytsnroud
import sdg.SudokuGenerator;

/**
 * This is the basic UI for a Sudoku game.
 */
public class SudokuUI extends Application {

    // The primary scene for the app.
    private Scene scene;

    // All frame go in this item.
    private FlowPane mainFrame;

    // Stores the textFields that constitute the actual puzzle.
    private GridPane puzzlePane;

    // Holds the Time-Elapsed timer.
    private Pane timePane;

    // Stores the commit and the surrender buttons.
    private VBox buttonPane;

    // The logic for the game.
    private SudokuGenerator sudokuGame;

    // Long-term plan to set options.
    private SudokuOptions sudokuOptions;

    /**
     * Starts the application.
     *
     * @param primaryStage The...primary stage.
     */
    @Override
    public void start(Stage primaryStage) {
        sudokuOptions = new SudokuOptions();
        sudokuGame = new SudokuGenerator(16, 16);

        mainFrame = new FlowPane();
        scene = new Scene(mainFrame, 600, 600);

        initializePuzzlePane();
        initializeStopwatchPane();
        initializeFuctionalButtons();

        showPuzzleToUser();

        primaryStage.setTitle(("Sudoku: Just 'Cause"));
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    /**
     * Initializes the puzzlePane and sets the eventHandlers and other
     * properties of the puzzle textFields. There is one SudokuTextField for
     * each element of the array, so 81 items in total by default. Borders are
     * also set here based on the physical location of the SudokuTextField, so
     * that the entire collection resembles an actual Sudoku puzzle.
     */
    private void initializePuzzlePane() {
        puzzlePane = new GridPane();
        puzzlePane.setHgap(1);
        puzzlePane.setVgap(1);
        puzzlePane.setPadding(new Insets(10, 10, 10, 10));

        // Create a SudokuTextField for every element of the puzzleSolution
        // array.
        for (int i = 0; i < SudokuGenerator.MAX_VALUE; i++) {
            for (int j = 0; j < SudokuGenerator.MAX_VALUE; j++) {
                SudokuTextField textFieldObj = new SudokuTextField();
                textFieldObj.setPrefHeight(50);
                textFieldObj.setPrefWidth(50);
                textFieldObj.setAlignment(Pos.CENTER);
                textFieldObj.setFont(Font.font(STYLESHEET_MODENA, FontWeight.NORMAL, 16));
                textFieldObj.setEditable(true);

                // Aaaarrrrgggghhhh!!!!  Effectively finalizing variables for
                // the listener.
                int row = i;
                int col = j;
                int index = row * 9 + col;

                // Set the listener: when we change text, call this.
                textFieldObj.textProperty().addListener((ov, oldV, newV) -> {
                    // Try to prevent the double-firing with
                    // an if-statement, which prevents double-
                    // firing when the old value and the new
                    // are the same.
                    if (!oldV.equals(newV)) {
                        // Call the listener's choice of method.
                        leaveFocus(index, sudokuGame.getDisplay(), row, col);

                    }

                });

                // Currently no plans for the leave-focus event.
                textFieldObj.focusedProperty().addListener((fov) -> {
                });

                // Finally, set the grid border so people can see which elements
                // constitute to block-rule.  This and the next if-block both
                // use the custom method in SudokuTextField class.
                if (!(col % 3 == 0) && row % 3 == 0) {
                    // Paint the top border if our row is a multiple of three.
                    //tf.setStyle("-fx-border-style: solid outside; -fx-border-width: 8 0 0 0;");
                    textFieldObj.setBorderThickness(8, 0, 0, 0, 8);
                } else if (!(row % 3 == 0) && col % 3 == 0) {
                    // Paint the left border, if our column is multiple of three.
                    textFieldObj.setBorderThickness(0, 0, 0, 8, 8);

                } else if (col % 3 == 0 && row % 3 == 0) {
                    textFieldObj.setBorderThickness(8, 0, 0, 8, 8);

                }

                // Any exterior borders not already painted by setting the 
                // border widths as stored in SudokuTextField object.
                if (row == SudokuGenerator.MAX_VALUE - 1 && col == SudokuGenerator.MAX_VALUE - 1) {
                    textFieldObj.setBorderThickness(0, 8, 8, 0, 8);

                } else if (col == SudokuGenerator.MAX_VALUE - 1 && !(row % 3 == 0)) {
                    textFieldObj.setBorderThickness(0, 8, 0, 0, 8);

                } else if (row == SudokuGenerator.MAX_VALUE - 1 && !(col % 3 == 0)) {
                    textFieldObj.setBorderThickness(0, 0, 8, 0, 8);

                } else if (row == SudokuGenerator.MAX_VALUE - 1 && col % 3 == 0) {
                    textFieldObj.setBorderThickness(0, 0, 8, 8, 8);

                } else if (col == SudokuGenerator.MAX_VALUE - 1 && row % 3 == 0) {
                    textFieldObj.setBorderThickness(8, 8, 0, 0, 8);

                }

                // Set the format to what we've already done.
                textFieldObj.setStyle(textFieldObj.getBorderFormat());

                // Make the app respond to arrow keys for control traversal
                // inside the puzzlePane only.
                textFieldObj.setOnKeyPressed((e)
                        -> {
                    // Handle movement based on the key that's pressed.
                    switch (e.getCode()) {
                        case UP: {
                            keyUp(index);
                            break;
                        }
                        case DOWN: {
                            keyDown(index);
                            break;
                        }
                        case LEFT: {
                            keyLeft(index);
                            break;
                        }
                        case RIGHT: {
                            keyRight(index);
                            break;
                        }

                    }

                });

                // Lastly, add the textField to its pane.
                puzzlePane.add(textFieldObj, j, i);

            }

        }

        // Add puzzlePane to the frame...
        mainFrame.getChildren().add(puzzlePane);

    }

    /**
     * Initializes the stop watch and its house-pane before adding it to the
     * main scene frame.
     */
    private void initializeStopwatchPane() {
        // This pane will house only the timer.
        timePane = new Pane();

        // Create the timer label.
        SudokuAnimationLabel elapsedTimer = new SudokuAnimationLabel();

        // Alignment and font settings.
        elapsedTimer.setAlignment(Pos.CENTER);
        elapsedTimer.setFont(Font.font(STYLESHEET_MODENA, FontWeight.NORMAL, 20));

        // Adds the timer label.
        timePane.getChildren().add(elapsedTimer);

        // Adds the pane to the frame.
        mainFrame.getChildren().add(timePane);

    }

    /**
     * Initializes the buttons, click event handler methods, and their storage
     * pane before adding the controls to the main scene frame.
     */
    private void initializeFuctionalButtons() {
        buttonPane = new VBox();
        buttonPane.setPadding(new Insets(10, 10, 10, 10));
        buttonPane.setPrefWidth(scene.getWidth() - puzzlePane.getWidth());

        Button bCommit = new Button();

        // Creating the text line here rather than in CTOR, so I can see where 
        // I want to change it easier, if I do.
        bCommit.setText("Commit Button");
        bCommit.setOnAction(commitClickEvent -> {
            onCommitClickEvent();
        });

        Button bSurrender = new Button();
        bSurrender.setText("Surrender Button");
        bSurrender.setOnAction(surrenderClickEvent -> {
            onSurrenderClickEvent();
        });

        buttonPane.getChildren().add(bCommit);
        buttonPane.getChildren().add(bSurrender);

        mainFrame.getChildren().add(buttonPane);

    }

    /**
     * This is the hub for initial puzzle display. It calls to the generator to
     * make a puzzle, seeding it with sixteen random numbers (by default) at
     * sixteen random locations in the solution array, and uses a backtracking
     * method to derive the solution. Once complete, it displays the values at
     * sixteen random locations (by default) on the puzzle-solution to the user.
     */
    public void showPuzzleToUser() {
        sudokuGame.execute(16, 16, this);

        // This method appears to work, but I'll leave the commented line
        // in in cast I need it again later.
        //sg.showPreGeneratedPuzzle(this);
        int row = 0;
        int col = 0;

        for (row = 0; row < SudokuGenerator.MAX_VALUE; row++) {
            for (col = 0; col < SudokuGenerator.MAX_VALUE; col++) {
                // Don't look up the child node if the array's value is zero. 
                // We want no display in that case.
                if (sudokuGame.getDisplay()[row][col] == 0) {
                    continue;
                }

                // The value is not zero, so we need to display it in the 
                // correct child node (only if it is a textField.
                if (puzzlePane.getChildren().get(col) instanceof TextField) {
                    // Set the text for hints.  This is done by converting the child-node index from the matrix index, so row * 9 + col.
                    ((TextField) puzzlePane.getChildren().get(row * 9 + col)).setText(Integer.toString(sudokuGame.getDisplay()[row][col]));

                    // Make the font bigger and bold for shown items.
                    ((TextField) puzzlePane.getChildren().get(row * 9 + col)).setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 20));

                    // Make sure the box can no longer be edited.  Keep persistent clues.
                    ((TextField) puzzlePane.getChildren().get(row * 9 + col)).setEditable(false);

                }

            }

        }

    }

    /**
     * Debug method.
     */
    @Deprecated
    public void debugShowUser() {
        int row = 0;
        int col = 0;

        for (int i = 0; i < puzzlePane.getChildren().size(); i++) {
            ((SudokuTextField) puzzlePane.getChildren().get(i)).clear();

        }

        for (row = 0; row < SudokuGenerator.MAX_VALUE; row++) {
            for (col = 0; col < SudokuGenerator.MAX_VALUE; col++) {
                // Don't look up the child node if the array's value is zero. 
                // We want no display in that case.
                if (sudokuGame.getSolution()[row][col] == 0) {
                    continue;
                }

                // The value is not zero, so we need to display it in the 
                // correct child node (only if it is a textField).
                if (puzzlePane.getChildren().get(col) instanceof TextField) {
                    // Set the text for hints.  This is done by converting the child-node index from the matrix index, so row * 9 + col.
                    ((TextField) puzzlePane.getChildren().get(row * 9 + col)).setText(Integer.toString(sudokuGame.getSolution()[row][col]));

                    // Make the font bigger and bold for shown items.
                    ((TextField) puzzlePane.getChildren().get(row * 9 + col)).setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 20));

                    // Make sure the box can no longer be edited.  Keep persistent clues.
                    ((TextField) puzzlePane.getChildren().get(row * 9 + col)).setEditable(false);

                }

            }

        }
    }

    /**
     * The purpose of this method is to check when a player leaves one of the
     * text boxes that the placed digit, if any, does not violate any rule of
     * the game. If no violations occur, the value at the location will be added
     * to the user's working solution array.
     * <br><br>
     * NOTE: If a rule is broken, the item will remain on-screen, it just won't
     * become a part of the solution to check. The on-screen item will be
     * clearly wrong (red color).
     *
     * @param index The control index so we can find and change the color.
     * @param array The user's working solution.
     * @param row The location-row we're testing here.
     * @param col The location-col we're testing here.
     */
    public void leaveFocus(int index, int[][] array, int row, int col) {
        // By default, I want thte contents of the cell to be black.  If the rules
        // below are violated, we'll turn it red.
        puzzlePane.getChildren().get(index).setStyle("-fx-text-fill: black;"
                + ((SudokuTextField) puzzlePane.getChildren().get(index)).getBorderFormat());

        if (((SudokuTextField) puzzlePane.getChildren().get(index)).getText().isEmpty()) {
            return;
        }

        int val = Integer.parseInt(((SudokuTextField) puzzlePane.getChildren().get(row * 9 + col)).getText());

        // Not gonna be able to set a value to an accessor.  Dumb ass.
        ///sg.getDisplay()[row][col] = val;
        row = index / 9;
        col = index - (row * 9);

        // Quick check of the current location, just so we don't wind up
        // overwriting the value needlessly...and triggering the red.
        if (array[row][col] == val) {
            // We don't need to check for something that's already there.
            return;

        }

        // If the move is unsafe, turn the recently departed box's text to red.
        if (!sudokuGame.isSafeBlock(array, val, row, col) || !sudokuGame.isSafeColOrRow(array, val, row, col) || !checkIfValidData(val)) {
            // Turn it red.  It's not safe.  Don't mark it to the user array.
            puzzlePane.getChildren().get(index).setStyle("-fx-text-fill: red;"
                    + ((SudokuTextField) puzzlePane.getChildren().get(index)).getBorderFormat());

        } // Otherwise, turn it black (assume not already black...just in case).
        else {
            // Turn it black.
            puzzlePane.getChildren().get(index).setStyle("-fx-text-fill: black;"
                    + ((SudokuTextField) puzzlePane.getChildren().get(index)).getBorderFormat());

            // Mark the value into the working array.
            array[row][col] = val;

        }

    }

    /**
     * Button event where the user indicates completion and wants to check if
     * completed successfully. If all items in the visible solution are correct
     * or do not violate rules, the comparison with the solution array occurs,
     * and if no errors are detected, the method displays an alert that
     * indicates victory. The elapsed timer is also stopped.
     */
    public void onCommitClickEvent() {
        for (Node field : puzzlePane.getChildren()) {
            // If field is a SudokuTextField without text...
            if (field instanceof SudokuTextField && ((SudokuTextField) field).getText().length() == 0) {
                // Show a popup box that says you're not done and return.
                this.showSudokuAlert("Incomplete Puzzle Solution", "You have not "
                        + "completed all available puzzle squares.  Please "
                        + "continue to solve the puzzle.");

                return;

            }

        }

        // Check the user's solution against the puzzle solution.
        for (int row = 0; row < sudokuGame.getSolution().length; row++) {
            for (int col = 0; col < sudokuGame.getSolution()[row].length; col++) {
                // DEBUG PRINTOUT
                //System.out.print("The text is " + Integer.parseInt(((SudokuTextField)puzzlePane.getChildren().get(row * 9 + col)).getText()));
                //System.out.print(" and the actual is " + sg.getSolution()[row][col]);
                //System.out.println();

                // If the value of any indexed SudokuTextField is not equal to the
                // corresponding solutions's value, the user solution is 
                // incorrect, so tell 'em and return.
                if (Integer.parseInt(((SudokuTextField) puzzlePane.getChildren().get(row * 9 + col)).getText()) != sudokuGame.getSolution()[row][col]) {
                    this.showSudokuAlert("Incorrect Selection", "One or more of your answers is incocrect.");
                    return;

                }

            }

        }

        // This is for debug only.
        this.showSudokuAlert(null, "Whoohoo!!!  You win!!!");

        // Find the timer label to shut it off
        for (Node field : puzzlePane.getChildren()) {
            // Check to make sure it's the right control type.
            if (field instanceof SudokuAnimationLabel) {
                // Shut off the timer now.
                ((SudokuAnimationLabel) field).doMessageGameWon();

            }

        }

    }

    /**
     * Button event where the user effectively gives up on solving the puzzle.
     * The user is chastised for a quitter's mentality. The elapsed timer is
     * also shut off.
     */
    public void onSurrenderClickEvent() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Sudoku Message");
        alert.setHeaderText("You are about to throw in the towel.");
        alert.setContentText("We will not normally show you the solution, "
                + "because you don't deserve to see it.  Are you sure you want "
                + "to surrender?");

        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yesButton, noButton);

        Optional<ButtonType> result = alert.showAndWait();

        // If we click the yes button, we're sure we want to give up, so...
        if (result.get() == yesButton) {
            // Need to decide whether to show the puzzle or not.

            // Right now, we'll just exit the app.
            System.exit(0);

        } else {
            // Do nothing.  Why do I even have this block?  Comments only, I 
            // guess.

        }

        // Find the timer label to shut it off. Probably could roll this into
        // a separate method, since I used it on the Commit event, but I don't
        // want to do any more typing. (says the guy with long comments).
        for (Node field : puzzlePane.getChildren()) {
            // Check to make sure it's the right control type.
            if (field instanceof SudokuAnimationLabel) {
                // Shut off the timer now.
                ((SudokuAnimationLabel) field).doMessageGameWon();

            }

        }

    }

    /**
     * Checks the typed value to ensure that it is a valid Sudoku number.
     * Anything outside of the 1 - 9 (inclusive) range will return false.
     *
     * @param val The value under consideration.
     * @return True, if the number is 1, 2, 3, 4, 5, 6, 7, 8, or 9; false, if
     * anything else.
     */
    private boolean checkIfValidData(int val) {
        // If the value is outside of the allowable, we need to 
        // reset it to be within bounds.
        if (val > 9 || val < 1) {
            return false;

        }

        return true;

    }

    /**
     * A custom alert message so I didn't have to keep typing the lines
     * contained in the method.
     *
     * @param header The caption value.
     * @param msg The body-text.
     */
    private void showSudokuAlert(String header, String msg) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Sudoku Message");
        alert.setHeaderText(header);
        alert.setContentText(msg);

        alert.showAndWait();

    }

    /**
     * This handles the key-up event and moves the focus to the SudokuTextField
     * directly above the recipient of the event.
     *
     * @param index The index of the STF that currently has focus and which
     * received the event.
     */
    public void keyUp(int index) {
        // The 8th index represents array[0][8], which is the first row.  If we
        // yield a negative number by subtracting 8, we must already be at
        // array[0][n], so we have no more upward maneuver.  
        if (index - 8 > 0) {
            // Move 9 indices up to the square immediately above...
            ((SudokuTextField) puzzlePane.getChildren().get(index - 9)).requestFocus();

        } else {
            System.out.println("DEBUG MESSAGE");
        }

    }

    /**
     * This handles the key-down event and moves the focus to the
     * SudokuTextField directly below the recipient of the event.
     *
     * @param index The index of the STF that currently has focus and which
     * received the event.
     */
    public void keyDown(int index) {
        // The 72nd index begins array[8][0], which is the last row
        // of the array, so there are no more space to go down.  If we yield
        // a negative number, we must still have downward maneuver room.
        if (index - (8 * 9) < 0) {
            // Move 9 indices down to the square immediately below...
            ((SudokuTextField) puzzlePane.getChildren().get(index + 9)).requestFocus();

        } else {
            System.out.println("DEBUG MESSAGE");
        }

    }

    /**
     * This handles the key-left event and moves the focus to the
     * SudokuTextField directly left of the recipient of the event.
     *
     * @param index The index of the STF that currently has focus and which
     * received the event.
     */
    public void keyLeft(int index) {
        // Mod-9 should result in a non-zero number if we're at any column
        // greater than to 0.  If this doesn't happen, we're already at 
        // the left mod index-column on the grid.
        if (index % 9 > 0) {
            // Move 1 index to the square immediately left...
            ((SudokuTextField) puzzlePane.getChildren().get(index - 1)).requestFocus();

        } else {
            System.out.println("DEBUG MESSAGE");
        }

    }

    /**
     * This handles the key-right event and moves the focus to the
     * SudokuTextField directly right of the recipient of the event.
     *
     * @param index The index of the STF that currently has focus and which
     * received the event.
     */
    public void keyRight(int index) {
        // If we get the Sudoku remainder and subtract the max-column index
        // available in the array, we will yield a negative number until
        // we reach the last possible element, when the result will be zero.
        if (index % 9 - 8 < 0) {
            // Move 1 index to the square immediately right...
            ((SudokuTextField) puzzlePane.getChildren().get(index + 1)).requestFocus();

        } else {
            System.out.println("DEBUG MESSAGE");
        }

    }

}
