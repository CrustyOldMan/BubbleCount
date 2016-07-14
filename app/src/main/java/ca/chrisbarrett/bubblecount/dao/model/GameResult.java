package ca.chrisbarrett.bubblecount.dao.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Date;

import ca.chrisbarrett.bubblecount.util.Values;

/**
 * POJO for holding GameResult objects. GameResults are associated with both a Player and a
 * Game through their foreign key int fields.
 *
 * @author Chris Barrett
 * @see
 * @since Jul 10, 2016
 */
public class GameResult implements Parcelable {


    private int id;                 // pk of the game result
    private int timeResult;         // time to complete the game
    private Date createdOn;         // date game was played
    private Date syncedOn;         // date the Player was created
    private int playerId;           // fk of the Player id who played the game
    private int gameId;             // fk of the Game played

    /**
     * Default constructor
     */
    public GameResult () {
        this(Values.NO_ID, Values.NO_ID, null, null,Values.NO_ID, Values.NO_ID);
    }

    /**
     * Constructor called with default NO_ID. Date created on will be assigned by the system.
     *
     * @param timeResult
     * @param playerId
     * @param gameId
     */
    public GameResult (int timeResult, int playerId, int gameId) {
        this(Values.NO_ID, timeResult, Calendar.getInstance().getTime(), null, playerId, gameId);
    }

    /**
     * Constructor called from the database.
     * @param id
     * @param timeResult
     * @param createdOn
     * @param syncedOn
     * @param playerId
     * @param gameId
     */
    public GameResult (int id, int timeResult, Date createdOn, Date syncedOn, int playerId, int gameId) {
        this.id = id;
        this.timeResult = timeResult;
        this.createdOn = createdOn;
        this.syncedOn = syncedOn;
        this.playerId = playerId;
        this.gameId = gameId;
    }


    public int getId () {
        return id;
    }

    public void setId (int id) {
        this.id = id;
    }

    public int getTimeResult () {
        return timeResult;
    }

    public void setTimeResult (int timeResult) {
        this.timeResult = timeResult;
    }

    public Date getCreatedOn () {
        return createdOn;
    }

    public void setCreatedOn (Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getSyncedOn () {
        return syncedOn;
    }

    public void setSyncedOn (Date syncedOn) {
        this.syncedOn = syncedOn;
    }

    public int getPlayerId () {
        return playerId;
    }

    public void setPlayerId (int playerId) {
        this.playerId = playerId;
    }

    public int getGameId () {
        return gameId;
    }

    public void setGameId (int gameId) {
        this.gameId = gameId;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameResult that = (GameResult) o;

        if (timeResult != that.timeResult) return false;
        return !(createdOn != null ? !createdOn.equals(that.createdOn) : that.createdOn != null);

    }

    @Override
    public int hashCode () {
        int result = timeResult;
        result = 31 * result + (createdOn != null ? createdOn.hashCode() : 0);
        return result;
    }
    protected GameResult(Parcel in) {
        id = in.readInt();
        timeResult = in.readInt();
        long tmpCreatedOn = in.readLong();
        createdOn = tmpCreatedOn != -1 ? new Date(tmpCreatedOn) : null;
        long tmpSyncedOn = in.readLong();
        syncedOn = tmpSyncedOn != -1 ? new Date(tmpSyncedOn) : null;
        playerId = in.readInt();
        gameId = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(timeResult);
        dest.writeLong(createdOn != null ? createdOn.getTime() : -1L);
        dest.writeLong(syncedOn != null ? syncedOn.getTime() : -1L);
        dest.writeInt(playerId);
        dest.writeInt(gameId);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<GameResult> CREATOR = new Parcelable.Creator<GameResult>() {
        @Override
        public GameResult createFromParcel(Parcel in) {
            return new GameResult(in);
        }

        @Override
        public GameResult[] newArray(int size) {
            return new GameResult[size];
        }
    };

}
