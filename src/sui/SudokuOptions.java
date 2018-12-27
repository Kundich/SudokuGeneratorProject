/*
 * Programmer: Aaron Myers
 * Date: 2018/12/27
 * Purpose: An incomplete class which exists to serve unimplemented functionality
 *          to the base application.  It is intended to help establish different
 *          protocols for puzzle difficulty, but as yet remains wholly 
 *          unintegrated.
 */
package sui;


/**
 * This class is intended to house some user-specified options, and is here really
 * only as a placeholder, in case I come back around to adding to the existing 
 * application.
 * <br><br>
 * NOTE: While this is not currently deprecated, it's not in use.
 */
public class SudokuOptions
{

    public enum Difficulty
    {
        EASY (20),
        NORMAL (16),
        HARD (12);

        private final int diffCode;

        private Difficulty(int diffCode)
        {
            this.diffCode = diffCode;

        }
        

        public int getDiffCode()
        {
            return this.diffCode;

        }

    }
    private Difficulty level;
    private int seed;
    
    public SudokuOptions()
    {
        level = Difficulty.NORMAL;
        this.seed = 16;
        
    }
    
    
    public SudokuOptions(int seed)
    {
        
        
    }
    
    
    public SudokuOptions(int seed, int level)
    {
        
    }
    
    public Difficulty getDifficulty() { return this.level; }
    public int getSeed() { return this.seed; }
    
}


