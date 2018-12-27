/*
 * Programmer: Aaron Myers
 * Date: 2018/12/27
 * Purpose: Creates an extension class of Label, which contains an instance
 *          of an AnimationTimer.  This is used to display elapsed time on the 
 *          UI.
 */
package sui;
import javafx.animation.AnimationTimer;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

/**
 * This class represents an extension of Label.  It's intended to make it easier
 * to specialize the Elapsed Time function on SudokuUI.
 */
public class SudokuAnimationLabel extends Label
{
    private long startTime;
    private long displayTime;
    private LabelTimer timer;
    
    
    // A variable to store the number of millis at which point
    // the Oprah Bus is coming.  Intended to be equal to the best time - 
    // approximately 5 minutes.
    private long warningTime;
    
    
    /**
     * Parameterless CTOR.  warningTime is set with a default of 10 seconds.  
     * Please note the comments above the warningTime declaration, since I have
     * elected not to implement the personal-best functionality at this time.
     */
    public SudokuAnimationLabel()
    {
        // Capture the time the object was created.
        startTime = System.currentTimeMillis();
        
        
        // Sets Oprah Bus time, default when not invoked via parametered
        // CTOR will be 10 minutes for testing.
        warningTime = 10000;
        
        
        // Creates the LabelTimer object with references to everything done
        // so far.
        timer = new LabelTimer(startTime, warningTime, this);
        
        
        // Start the timer.
        timer.start();
        
    }
    
    
    /** 
     * Parametered CTOR.  The idea here is that, if I continue building, 
     * I can build in a personal-best time tracker.  This will become 
     * parametered warningTime - 5 minutes, passed from the UI.
     * @param warningTime The best time recorded by the user on puzzles so far.
     */
    public SudokuAnimationLabel(long warningTime)
    {
        // Capture the time the object was created.
        startTime = System.currentTimeMillis();
        
        
        // Set warning time equal to 5 minutes less than
        // the passed warning time.
        this.warningTime = warningTime - 30_000;
        
        
        // Creates the LabelTimer object with references to everything done
        // so far.
        timer = new LabelTimer(startTime, warningTime, this);
        
        
        // Start the timer.
        timer.start();
           
    }
    
    
    
    /**
     * The user clicked the commit button, the method indicated that the user's
     * solution was, indeed, the correct solution, so stop the watch.
     */
    public void doMessageGameWon()
    {
        // The user won, so let's stop the clock.
        timer.stop();
        
    }
      
}


/**
 * Private class that exists to handle the timer functionality and to create a
 * specialized display string based on current time elapsed.  An instance
 * of this class is contained in the SudokuAnimationLabel class.
 */
class LabelTimer extends AnimationTimer
{
    private long startTime;
    private long warningTime;
    private SudokuAnimationLabel labelRef;
    
    
    /**
     * Parametered CTOR.  Sets all the items needed to work the purpose of this
     * class.
     * @param startTime The creation time of the calling object.
     * @param warningTime The time set as Oprah Bus for the calling object.
     * @param labelRef The calling object reference, itself, since we change it
     * inside this class.
     */
    public LabelTimer(long startTime, long warningTime, SudokuAnimationLabel labelRef)
    {
        this.startTime = startTime;
        this.warningTime = warningTime;
        this.labelRef = labelRef;
        
    }
    
    
    /**
     * Check the current time to determine if we've gone beyond warning time and
     * display the current elapsed time.  If we're passed warningTime, color
     * the text of the caller red.
     * @param now 
     */
    @Override
    public void handle(long now)
    {
        long elapsedTime = System.currentTimeMillis() - startTime;
        
        
        // If the stopwatch indicates we're passed Oprah Bus time,
        if (elapsedTime >= warningTime)
        {
            // Set color to dark red!!!
            labelRef.setTextFill(Color.web("#8B0000"));
            
        }
        
        
        // Display the current time on the control.
        labelRef.setText(setAndFormatText(elapsedTime));
        
        
    }
    
    
    /**
     * Takes the currently elapsed time and converts it into a stop-watch like
     * display before returning that display-value to the caller.
     * @param elapsedTime The number of milliseconds that has elapsed since the 
     * creation and timer start of the caller's class-object.
     * @return The string to display to the user.
     */
    public String setAndFormatText(long elapsedTime)
    {
        // Calculate the time by factoring the remainder divided by the relevant
        // time intervals as converted from milliseconds to the corresponding
        // base unit.
        int seconds = (int)((elapsedTime % (1000 * 60)) / 1000);
        int minutes = (int)((elapsedTime % (1000 * 60 * 60)) / (1000 * 60));
        int hours = (int)((elapsedTime % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
                
        
        // This is what gets displayed to the user.
        String output = "Elapsed Time: " + 
                ((hours > 9) ? hours : "0" + hours) + ":" + 
                ((minutes > 9) ? minutes : "0" + minutes) + ":" + 
                ((seconds > 9) ? seconds : "0" + seconds);
        
        
        
        // Return.
        return output;
        
    }
    
}
