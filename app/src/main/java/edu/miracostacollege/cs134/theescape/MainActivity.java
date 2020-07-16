package edu.miracostacollege.cs134.theescape;

import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import edu.miracostacollege.cs134.theescape.model.Direction;
import edu.miracostacollege.cs134.theescape.model.Player;
import edu.miracostacollege.cs134.theescape.model.Zombie;

import static edu.miracostacollege.cs134.theescape.model.BoardValues.EXIT;
import static edu.miracostacollege.cs134.theescape.model.BoardValues.FREE;
import static edu.miracostacollege.cs134.theescape.model.BoardValues.OBST;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener{

    private int wins = 0;
    private int losses = 0;

    public static final int TOTAL_ROWS = 8;
    public static final int TOTAL_COLS = 8;

    public static final int PLAYER_ROW = 1;
    public static final int PLAYER_COL = 1;

    public static final int ZOMBIE_ROW = 2;
    public static final int ZOMBIE_COL = 4;

    public static final int EXIT_ROW = 5;
    public static final int EXIT_COL = 7;

    private static final float FLING_THRESHOLD = 500f;

    private LinearLayout boardLinearLayout;
    private TextView winsTextView;
    private TextView lossesTextView;
    private GestureDetector gestureDetector;

    private Player player;
    private Zombie zombie;

    final int gameBoard[][] = {
            {OBST, OBST, OBST, OBST, OBST, OBST, OBST, OBST},
            {OBST, FREE, FREE, FREE, FREE, FREE, OBST, OBST},
            {OBST, FREE, OBST, FREE, FREE, FREE, FREE, OBST},
            {OBST, FREE, OBST, FREE, FREE, FREE, FREE, OBST},
            {OBST, FREE, FREE, FREE, FREE, FREE, OBST, OBST},
            {OBST, FREE, FREE, FREE, FREE, FREE, FREE, EXIT},
            {OBST, FREE, OBST, FREE, FREE, FREE, FREE, OBST},
            {OBST, OBST, OBST, OBST, OBST, OBST, OBST, OBST}
    };

    ImageView viewBoard[][] = new ImageView[TOTAL_ROWS][TOTAL_COLS];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boardLinearLayout = findViewById(R.id.boardLinearLayout);
        winsTextView = findViewById(R.id.winsTextView);
        lossesTextView = findViewById(R.id.lossesTextView);

        // Turn on GestureDetector
        gestureDetector = new GestureDetector(this, this);

        startNewGame();
    }

    private void startNewGame() {
        //DONE: Loop through the viewBoard and initialize each of the ImageViews
        //DONE: to the children of the LinearLayouts
        //DONE: Use the gameBoard to determine which image to assign:
        LinearLayout row;
        for (int i = 0; i < TOTAL_ROWS; i++) {
            row = (LinearLayout) boardLinearLayout.getChildAt(i);
            for (int j = 0; j < TOTAL_COLS; j++) {
                viewBoard[i][j] = (ImageView) row.getChildAt(j);

                // Make a decision on the board
                switch (gameBoard[i][j]) {
                    //DONE: OBST = R.drawable.obstacle
                    case OBST:
                        // Put obstacle drawable in ImageView
                        viewBoard[i][j].setImageResource(R.drawable.obstacle);
                        break;

                    //DONE: EXIT = R.drawable.exit
                    case FREE:
                        // Put null in ImageView
                        viewBoard[i][j].setImageDrawable(null);
                        break;

                    //DONE: FREE = null (no image to load)
                    case EXIT:
                        // Put the exit in ImageView
                        viewBoard[i][j].setImageResource(R.drawable.exit);
                        break;
                }
            }
        }

        //DONE: Instantiate a new Player object at PLAYER_ROW, PLAYER_COL
        player = new Player(PLAYER_ROW, PLAYER_COL);

        //DONE: Set the imageView at that position to R.drawable.player
        viewBoard[player.getRow()][player.getCol()].setImageResource(R.drawable.female_player);

        //DONE: Instantiate a new Zombie object at ZOMBIE_ROW, ZOMBIE_COL
        zombie = new Zombie(ZOMBIE_ROW, ZOMBIE_COL);

        //DONE: Set the imageView at that position to R.drawable.zombie
        viewBoard[zombie.getRow()][zombie.getCol()].setImageResource(R.drawable.zombie);

        //DONE: Initialize the winsTextView and lossesTextView
        winsTextView.setText(getString(R.string.win, wins));
        lossesTextView.setText(getString(R.string.losses, losses));
    }

    private void movePlayer(float velocityX, float velocityY) {

        //DONE: Determine the direction of the fling (based on velocityX and velocityY)
        Direction direction = null;

        // Whichever absolute value is bigger determines the direction
        float absX = Math.abs(velocityX);
        float absY = Math.abs(velocityY);

        // if absX > absY, move either LEFT or RIGHT
        if (absX > absY) {
            //DONE: The velocity must exceed FLING_THRESHOLD to count (otherwise, it's not really a move)
            if (velocityX <= -FLING_THRESHOLD) {
                direction = Direction.LEFT;
            }
            else if (velocityX >= FLING_THRESHOLD) {
                direction = Direction.RIGHT;
            }
        }

        // else move either UP or DOWN
        else {
            if (velocityY <= -FLING_THRESHOLD) {
                direction = Direction.UP;
            }
            else if (velocityY >= FLING_THRESHOLD) {
                direction = Direction.DOWN;
            }
        }

        if (direction != null) {
            //DONE: Set the player's current image view drawable to null
            viewBoard[player.getRow()][player.getCol()].setImageDrawable(null);

            //DONE: Move the player
            player.move(gameBoard, direction);

            //DONE: Set the player's current image view to R.drawable.player after the move
            viewBoard[player.getRow()][player.getCol()].setImageResource(R.drawable.female_player);
        }
    }

    private void moveZombie() {
        //DONE: Set the zombie's current image view drawable to null
        viewBoard[zombie.getRow()][zombie.getCol()].setImageDrawable(null);

        //DONE: Move the zombie
        zombie.move(gameBoard, player.getRow(), player.getCol());

        //DONE: Set the zombie's current image view to R.drawable.zombie after the move
        viewBoard[zombie.getRow()][zombie.getCol()].setImageResource(R.drawable.zombie);
    }

    private void determineOutcome() {
        //DONE: Determine the outcome of the game (win or loss)
        //DONE: It's a win if the player's row/col is the same as the exit row/col
        if (player.getRow() == EXIT_ROW && player.getCol() == EXIT_COL) {
            //DONE: Call the handleWin() method
            handleWin();
        }

        //DONE: It's a loss if the player's row/col is the same as the zombie's row/col
        else if (player.getRow() == zombie.getRow() && player.getCol() == zombie.getCol()) {
            //DONE: Call the handleLoss() method
            handleLoss();
        }

        //DONE: Otherwise, do nothing, just return.
    }

    private void handleWin()
    {
        //DONE: Implement the handleWin() method by accomplishing the following:
        //DONE: Increment the wins
        wins++;

        //DONE: Set the imageView (at the zombie's row/col) to the R.drawable.bunny
        viewBoard[zombie.getRow()][zombie.getCol()].setImageResource(R.drawable.bunny);

        //DONE: Start an animation
        viewBoard[player.getRow()][player.getCol()].
                startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_anim));

        //DONE: Wait 2 seconds, then start a new game
        Handler handler = new Handler();
        handler.postDelayed(newGameRunnable, 2000);
    }

    private void handleLoss()
    {
        //DONE: Implement the handleLoss() method by accomplishing the following:
        //DONE: Increment the losses
        losses++;

        //DONE: Set the imageView (at the player's row/col) to the R.drawable.blood
        viewBoard[player.getRow()][player.getCol()].setImageResource(R.drawable.blood);

        //DONE: Start an animation
        viewBoard[player.getRow()][player.getCol()].
                startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_anim));

        //DONE: Wait 2 seconds, then start a new game
        Handler handler = new Handler();
        handler.postDelayed(newGameRunnable, 2000);
    }

    Runnable newGameRunnable = new Runnable() {
        @Override
        public void run() {
            startNewGame();
        }
    };

    /**
     * Notified when a tap occurs with the down {@link MotionEvent}
     * that triggered it. This will be triggered immediately for
     * every down event. All other events should be preceded by this.
     *
     * @param e The down motion event.
     */
    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    /**
     * The user has performed a down {@link MotionEvent} and not performed
     * a move or up yet. This event is commonly used to provide visual
     * feedback to the user to let them know that their action has been
     * recognized i.e. highlight an element.
     *
     * @param e The down motion event
     */
    @Override
    public void onShowPress(MotionEvent e) {

    }

    /**
     * Notified when a tap occurs with the up {@link MotionEvent}
     * that triggered it.
     *
     * @param e The up motion event that completed the first tap
     * @return true if the event is consumed, else false
     */
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    /**
     * Notified when a scroll occurs with the initial on down {@link MotionEvent} and the
     * current move {@link MotionEvent}. The distance in x and y is also supplied for
     * convenience.
     *
     * @param e1        The first down motion event that started the scrolling.
     * @param e2        The move motion event that triggered the current onScroll.
     * @param distanceX The distance along the X axis that has been scrolled since the last
     *                  call to onScroll. This is NOT the distance between {@code e1}
     *                  and {@code e2}.
     * @param distanceY The distance along the Y axis that has been scrolled since the last
     *                  call to onScroll. This is NOT the distance between {@code e1}
     *                  and {@code e2}.
     * @return true if the event is consumed, else false
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    /**
     * Notified when a long press occurs with the initial on down {@link MotionEvent}
     * that trigged it.
     *
     * @param e The initial on down motion event that started the longpress.
     */
    @Override
    public void onLongPress(MotionEvent e) {

    }

    /**
     * Notified of a fling event when it occurs with the initial on down {@link MotionEvent}
     * and the matching up {@link MotionEvent}. The calculated velocity is supplied along
     * the x and y axis in pixels per second.
     *
     * @param e1        The first down motion event that started the fling.
     * @param e2        The move motion event that triggered the current onFling.
     * @param velocityX The velocity of this fling measured in pixels per second
     *                  along the x axis.
     * @param velocityY The velocity of this fling measured in pixels per second
     *                  along the y axis.
     * @return true if the event is consumed, else false
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        movePlayer(velocityX, velocityY);
        moveZombie();
        determineOutcome();
        return true;
    }

    /**
     * Called when a touch screen event was not handled by any of the views
     * under it.  This is most useful to process touch events that happen
     * outside of your window bounds, where there is no view to receive it.
     *
     * @param event The touch screen event being processed.
     * @return Return true if you have consumed the event, false if you haven't.
     * The default implementation always returns false.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        return gestureDetector.onTouchEvent(event);
    }
}
