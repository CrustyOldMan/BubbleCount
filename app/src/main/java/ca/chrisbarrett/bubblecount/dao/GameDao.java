package ca.chrisbarrett.bubblecount.dao;

import android.database.Cursor;

import java.util.List;

import ca.chrisbarrett.bubblecount.dao.model.Game;

/**
 * Created by chrisbarrett on 2016-07-18.
 */
public interface GameDao {

    int ALL_ENTRIES = 1;
    int USER_ENTRIES = 0;

    /**
     * This method retrieves a single Game from the database
     *
     * @param gameId
     * @return
     */
    Game selectGameById (long gameId);

    /**
     * This method returns a collection of Games
     *
     * @return
     */
    List<Game> selectAllGames ();

    /**
     * This method inserts a Game, or Games
     *
     * @param games
     * @return
     */
    boolean insertGame (Game... games);

    /**
     * This method updates a Game
     *
     * @param game
     * @return
     */
    boolean updateGame (Game game);

    /**
     * This method deletes a Game
     *
     * @param gameId
     * @return
     */
    boolean deleteGameById (long gameId);

    /**
     * Provides a Cursor for use in an Adapter. Generates format will contain objects based on the
     * two selectors:
     * <ul>
     * <li>{@link #ALL_ENTRIES} to generate with all entries, including the system provided
     * defaults</li>
     * <li>{@link #USER_ENTRIES} to generate with entries created by the User only</li>
     * </ul>
     *
     * @param entriesSelector
     * @return
     */
    Cursor getCursorForAdapter (int entriesSelector);
}
