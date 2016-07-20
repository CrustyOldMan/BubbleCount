package ca.chrisbarrett.bubblecount.dao;

import android.database.Cursor;

import java.util.List;

import ca.chrisbarrett.bubblecount.dao.model.Player;

/**
 * Created by chrisbarrett on 2016-07-18.
 */
public interface PlayerDao {

     int ALL_ENTRIES = 1;
     int USER_ENTRIES = 0;

    /**
     * This method retrieves a single Player from the database
     *
     * @param playerId
     * @return
     */
    Player selectPlayerById (long playerId);

    /**
     * This method returns a collection of Players
     *
     * @return
     */
    List<Player> selectAllPlayers ();

    /**
     * This method inserts a Player, or Players
     *
     * @param player
     * @return
     */
    boolean insertPlayer (Player... player);

    /**
     * This method updates a Player
     *
     * @param player
     * @return
     */
    boolean updatePlayer (Player player);

    /**
     * This method deletes a Player
     *
     * @param playerId
     * @return
     */
    boolean deletePlayerById (long playerId);


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
