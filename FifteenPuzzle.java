import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;
 
class FifteenPuzzle extends JPanel {
 
    private final int side = 3;
    private final int numTiles = side * side - 1;
    /*
    *   Generating a rand variable for getting random numbers for shuffling tiles
    *   tiles[] is used to keep track of the number in that tile in current state
    *   tileSize, blankPos,margin,gridSize are for GUI purpose
    *
    *   gameOver is for knowing the Game State.
    */
    private final Random rand = new Random();
    private final int[] tiles = new int[numTiles + 1];
    private final int tileSize;
    private int blankPos;
    private final int margin;
    private final int gridSize;
    private boolean gameOver; //State Pattern
 
    /*
    *   Singleton Pattern,making constructor private
    *   Restricting the creation of Objects ( One Here )
    */
    public static FifteenPuzzle FifteenObject = null;
    private FifteenPuzzle() {
        
        final int dim = 640;//Initializing the board to 640 px
 
        margin = 80; 
        tileSize = (dim - 2 * margin) / side; //So each tile has size of (640-160)/3 
        gridSize = tileSize * side; //grid size is total size by adding all tiles
 
        setPreferredSize(new Dimension(dim, dim + margin));
        setBackground(Color.RED);
        setForeground(new Color(0x6495ED)); // cornflowerblue
        setFont(new Font("SansSerif", Font.BOLD, 60));
 
        gameOver = true;    //Initializing State to True
 
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                /*
                *   State Pattern
                *   gameOver keeps track of state
                */
                if (gameOver) {
                    newGame();
 
                } else {
                    /*
                    *   Gets the coordinates of the click on the game board
                    *   Check if the positiin of click is in gridSize
                    */
                    int ex = e.getX() - margin;
                    int ey = e.getY() - margin;
 
                    if (ex < 0 || ex > gridSize || ey < 0 || ey > gridSize) {
                        return;
                    }

                    /*
                    *   Checks if the click is on the board, which tile is being targeted by user
                    */
                    int c1 = ex / tileSize;
                    int r1 = ey / tileSize;

                    /*
                    *   Getting coordinates of blank Positions
                    *   blankPos is the variable which keeps track of the position of the balnk when state changes
                    */
                    int c2 = blankPos % side;
                    int r2 = blankPos / side;

                    /*
                    *   Reach Exact position of the click, i.e reaching tiles[clikedPosition]
                    *   Now tiles[clikedPosition] returns the number on which user has clicked
                    */
                    int clickPos = r1 * side + c1;
 
                    int dir = 0; //Indicates how many tiles to shift
                    /*
                    *   If the clicked Position is itself blank, then do nothing i.e dir=0
                    *   If the clicked position and blank position are in same column, then dir = Math.abs(r1-r2)
                    *       indicating how far is the blankSpace from the clicked position int the column
                    *   If the clicked position and blank position are in same row, then dir = Math.abs(c1-c2)
                    *       indicating how far is the blankSpace from the clicked position int the column
                    */
                    if (c1 == c2 && Math.abs(r1 - r2) > 0) {
                        dir = (r1 - r2) > 0 ? side : -1*side;
 
                    } else if (r1 == r2 && Math.abs(c1 - c2) > 0) {
                        dir = (c1 - c2) > 0 ? 1 : -1;
                    }
 
                    if (dir != 0) {
                        do {
                            int newBlankPos = blankPos + dir;
                            tiles[blankPos] = tiles[newBlankPos];
                            blankPos = newBlankPos; //updating blankspace whenever move happens
                        } while (blankPos != clickPos);
                        tiles[blankPos] = 0;
                    }
 
                    gameOver = isSolved();  //Every time state changes , we check if the endState is reached
                }
                repaint();  //For every click
            }
        });
        //FifteenObject = null;
        newGame();  //every time game is solved
    
    }

    /*
    *   Lazy Initialization Of the Object,
    *   Gets intiaized only when it is first Enquired
    */
    public static FifteenPuzzle getInstance(){
        if(FifteenObject == null){
            FifteenObject = new FifteenPuzzle();
        }
        return FifteenObject;
    }

    /*
    *   Builder Pattern
    *   Set of fixed method which leads to game development
    *   And the final object ,output of the Builder method is Game Object
    */ 
    private void newGame() {
        do {
            reset();    //First we need to allocate the tiles[] array to initial states, Which later can be shuffled
            shuffle();  //The tiles should be shuffled first whenever a new game is being built
        } while (!isSolvable());
        gameOver = false;
    }
 
    private void reset() {
        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = (i + 1) % tiles.length;
        }
        blankPos = tiles.length - 1;
    }
 
    private void shuffle() {
        // don't include the blank space in the shuffle, leave it
        // in the last position during initialization
        int n = numTiles;
        while (n > 1) {
            int r = rand.nextInt(n--);
            int tmp = tiles[r];
            tiles[r] = tiles[n];
            tiles[n] = tmp;
        }
    }
 
    /*  Only half the permutations of the puzzle are solvable.
 
        Whenever a tile is preceded by a tile with higher value it counts
        as an inversion. In our case, with the blank space in the home
        position, the number of inversions must be even for the puzzle
        to be solvable.
 
        See also:
        www.cs.bham.ac.uk/~mdr/teaching/modules04/java2/TilesSolvability.html
     */
    private boolean isSolvable() {
        int countInversions = 0;
        for (int i = 0; i < numTiles; i++) {
            for (int j = 0; j < i; j++) {
                if (tiles[j] > tiles[i]) {
                    countInversions++;
                }
            }
        }
        return countInversions % 2 == 0;
    }
 
    private boolean isSolved() {
        if (tiles[tiles.length - 1] != 0) {
            return false;
        }
        for (int i = numTiles - 1; i >= 0; i--) {
            if (tiles[i] != i + 1) {
                return false;
            }
        }
        return true;
    }
 
    private void drawGrid(Graphics2D g) {
        int count=0;
        for (int i = 0; i < tiles.length; i++) {
            int r = i / side;
            int c = i % side;
            int x = margin + c * tileSize;
            int y = margin + r * tileSize;
 
            if (tiles[i] == 0) {
                if (gameOver) {
                    g.setColor(Color.GREEN);
                    drawCenteredString(g, "✓", x, y);
                }
                continue;
            }
 
            
            if(tiles[i] == i+1){
                g.setColor(Color.GREEN);
                g.fillRoundRect(x, y, tileSize, tileSize, 25, 25);
                g.setColor(Color.blue.darker());
                g.drawRoundRect(x, y, tileSize, tileSize, 25, 25);
                g.setColor(Color.WHITE);
                count++;
            }
            else{
            g.setColor(getForeground());
            g.fillRoundRect(x, y, tileSize, tileSize, 25, 25);
            g.setColor(Color.blue.darker());
            g.drawRoundRect(x, y, tileSize, tileSize, 25, 25);
            g.setColor(Color.WHITE);
            }
            drawCenteredString(g, String.valueOf(tiles[i]), x, y);
        }
        if(count>tiles.length/2)
        {
            setBackground(Color.WHITE);
        }
        else
            setBackground(Color.RED);
    }
 
    private void drawStartMessage(Graphics2D g) {
        if (gameOver) {
            g.setFont(getFont().deriveFont(Font.BOLD, 18));
            g.setColor(getForeground());
            String s = "click to start a new game";
            int x = (getWidth() - g.getFontMetrics().stringWidth(s)) / 2;
            int y = getHeight() - margin;
            g.drawString(s, x, y);
            setBackground(Color.BLACK);
        }
    }
 
    private void drawCenteredString(Graphics2D g, String s, int x, int y) {
        FontMetrics fm = g.getFontMetrics();
        int asc = fm.getAscent();
        int des = fm.getDescent();
 
        x = x + (tileSize - fm.stringWidth(s)) / 2;
        y = y + (asc + (tileSize - (asc + des)) / 2);
 
        g.drawString(s, x, y);
    }
 
    @Override
    public void paintComponent(Graphics gg) {
        super.paintComponent(gg);
        Graphics2D g = (Graphics2D) gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
 
        drawGrid(g);
        drawStartMessage(g);
    }
}