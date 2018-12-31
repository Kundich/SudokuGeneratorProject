/*
 * Programmer: Aaron Myers
 * Date: 2018/12/27
 * Purpose: Extends the TextField class to allow specialized formatting storage
 *          rather than having to re-calculate for formatting a standard 
 *          TextField at every event update.
 */
package sui;

import javafx.scene.control.TextField;

public class SudokuTextField extends TextField {

    private boolean hasLeftBorder;
    private boolean hasRightBorder;
    private boolean hasTopBorder;
    private boolean hasBottomBorder;

    private int borderThickness;

    public SudokuTextField() {
        super();

    }

    public boolean hasLeftBorderShown() {
        return hasLeftBorder;
    }

    public boolean hasRightBorderShown() {
        return hasRightBorder;
    }

    public boolean hasTopBorderShown() {
        return hasTopBorder;
    }

    public boolean hasBottomBorderShown() {
        return hasBottomBorder;
    }

    /**
     * This method set values to indicate which of the outer edges of the
     * textField frame include a border, and sets the uniform thickness of that
     * border for all areas that have one.
     *
     * @param top An integer greater than zero indicates the presence of a
     * border.
     * @param right An integer greater than zero indicates the presence of a
     * border.
     * @param bottom An integer greater than zero indicates the presence of a
     * border.
     * @param left An integer greater than zero indicates the presence of a
     * border.
     * @param borderThickness An integer indicates the width of border. Zeroes
     * or negatives are theoretically allowed since only non-zeroes will yield a
     * display border because all border booleans will be false otherwise.
     */
    public void setBorderThickness(int top, int right, int bottom, int left, int borderThickness) {
        this.hasTopBorder = (top > 0) ? true : false;
        this.hasRightBorder = (right > 0) ? true : false;
        this.hasBottomBorder = (bottom > 0) ? true : false;
        this.hasLeftBorder = (left > 0) ? true : false;

        this.borderThickness = borderThickness;

    }

    /**
     * This method set values to indicate which of the outer edges of the
     * textField frame include a border, and sets the uniform thickness of that
     * border for all areas that have one.
     *
     * @param top An boolean indicates the presence of a border.
     * @param right An boolean indicates the presence of a border.
     * @param bottom An boolean indicates the presence of a border.
     * @param left An boolean indicates the presence of a border.
     * @param borderThickness An integer indicates the width of border. Zeroes
     * or negatives are theoretically allowed since only non-zeroes will yield a
     * display border because all border booleans will be false otherwise.
     */
    public void setBorderThickness(boolean top, boolean right, boolean bottom, boolean left, int borderThickness) {
        this.hasTopBorder = top;
        this.hasRightBorder = right;
        this.hasBottomBorder = bottom;
        this.hasLeftBorder = left;

        this.borderThickness = borderThickness;

    }

    public String getBorderFormat() {
        String local = "-fx-border-style: solid outside; -fx-border-width: "
                + ((hasTopBorder) ? borderThickness : 0) + " "
                + ((hasRightBorder) ? borderThickness : 0) + " "
                + ((hasBottomBorder) ? borderThickness : 0) + " "
                + ((hasLeftBorder) ? borderThickness : 0) + " ";

        return local;

    }

}
