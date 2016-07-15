package ca.chrisbarrett.bubblecount.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import ca.chrisbarrett.bubblecount.dao.model.Game;
import ca.chrisbarrett.bubblecount.dao.model.GameResult;
import ca.chrisbarrett.bubblecount.dao.model.Player;

/**
 * @author Chris Barrett
 * @see
 * @since Jul 14, 2016
 */
public interface AppDatabase {

    /**
     * Gets a Player from the database based on the id value
     *
     * @param db
     * @param id
     * @return
     */
    Player getPlayerById(SQLiteDatabase db, long id);

    /**
     * Gets a GameResult from the database based on the id value
     *
     * @param db
     * @param id
     * @return
     */
    GameResult getGameResultById(SQLiteDatabase db, long id);

    /**
     * Gets a Game from the database based on the id value
     *
     * @param db
     * @param id
     * @return
     */
    Game getGameById(SQLiteDatabase db, long id);

    /**
     * Gets all Games from the database
     *
     * @param db
     * @return
     */
    List<Game> getAllGames(SQLiteDatabase db);

    /**
     * Gets all Players from the database
     *
     * @param db
     * @return
     */
    List<Player> getAllPlayers(SQLiteDatabase db);

    /**
     * Gets all GamesResults from the database
     *
     * @param db
     * @param playerId
     * @return
     */
    List<GameResult> getAllGamesResultsForPlayer(SQLiteDatabase db, long playerId);

    /**
     * Inserts a Player to the database
     *
     * @param db
     * @param player
     * * @return
     */
    long insertPlayer(SQLiteDatabase db, Player player);

    /**
     * Inserts a GameResult to the database
     *
     * @param db
     * @param gameResult
     * @return
     */
    long insertGameResult(SQLiteDatabase db, GameResult gameResult);

    /**
     * Updates a Player in the database
     *
     * @param db
     * @param player
     */
    void updatePlayer(SQLiteDatabase db, Player player);

    /**
     * Deletes a Player in the database
     *
     * @param db
     * @param player
     * @return number of rows deleted
     */
    int deletePlayer(SQLiteDatabase db, Player player);

}
