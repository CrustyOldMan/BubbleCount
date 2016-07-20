package ca.chrisbarrett.bubblecount.dao;

import java.util.List;

import ca.chrisbarrett.bubblecount.dao.model.GameResult;

/**
 * Created by chrisbarrett on 2016-07-18.
 */
public interface GameResultDao {

    /**
     * This method retrieves a single GameResult from the database
     *
     * @param gameResultId
     * @return
     */
    GameResult selectGameResultById (long gameResultId);

    /**
     * This method returns a collection of all GamesResults for a specific Player
     *
     * @param playerId
     * @return
     */
    List<GameResult> selectGameResultByPlayerId (long playerId);

    /**
     * This method returns a collection of all GamesResults for a specific Game
     *
     * @param gameId
     * @return
     */
    List<GameResult> selectGameResultByGameId (long gameId);

    /**
     * This method returns a collection of GameResults
     *
     * @return
     */
    List<GameResult> selectAllGameResults ();

    /**
     * This method inserts a GameResult, or GameResults
     *
     * @param gameResult
     * @return
     */
    boolean insertGameResult (GameResult... gameResult);

    /**
     * This method updates a GameResult
     *
     * @param gameResult
     * @return
     */
    boolean updateGameResult (GameResult gameResult);

    /**
     * This method deletes a GameResult
     *
     * @param gameResultId
     * @return
     */
    boolean deleteGameResultById (long gameResultId);
}
