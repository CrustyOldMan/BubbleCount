package ca.chrisbarrett.bubblecount.dao.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Date;

/**
 * POJO for holding GameResult objects. GameResults are associated with both a Player and a
 * Game through their foreign key int fields.
 *
 * @author Chris Barrett
 * @see
 * @since Jul 10, 2016
 */
public class GameResult implements Parcelable {

    public static final long NO_ID = -1;
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<GameResult> CREATOR = new Parcelable.Creator<GameResult>() {
        @Override
        public GameResult createFromParcel (Parcel in) {
            return new GameResult(in);
        }

        @Override
        public GameResult[] newArray (int size) {
            return new GameResult[size];
        }
    };
    private long id;                 // pk of the game result
    private long timeResult;         // time to complete the game
    private Date createdOn;         // date game was played
    private Date syncedOn;         // date the Player was created
    private long playerId;           // fk of the Player id who played the game
    private long gameId;             // fk of the Game played

    /**
     * Default constructor
     */
    public GameResult () {
        this(NO_ID, -1, null, null, NO_ID, NO_ID);
    }

    /**
     * Constructor called with default NO_ID. Date created on will be assigned by the system.
     *
     * @param timeResult
     * @param playerId
     * @param gameId
     */
    public GameResult (long timeResult, long playerId, long gameId) {
        this(NO_ID, timeResult, Calendar.getInstance().getTime(), null, playerId, gameId);
    }


    /**
     * Constructor called from the database.
     *
     * @param id
     * @param timeResult
     * @param createdOn
     * @param syncedOn
     * @param playerId
     * @param gameId
     */
    public GameResult (long id, long timeResult, Date createdOn, Date syncedOn, long playerId, long gameId) {
        this.id = id;
        this.timeResult = timeResult;
        this.createdOn = createdOn;
        this.syncedOn = syncedOn;
        this.playerId = playerId;
        this.gameId = gameId;
    }

    protected GameResult (Parcel in) {
        id = in.readLong();
        timeResult = in.readLong();
        long tmpCreatedOn = in.readLong();
        createdOn = tmpCreatedOn != -1 ? new Date(tmpCreatedOn) : null;
        long tmpSyncedOn = in.readLong();
        syncedOn = tmpSyncedOn != -1 ? new Date(tmpSyncedOn) : null;
        playerId = in.readLong();
        gameId = in.readLong();
    }

    public long getId () {
        return id;
    }

    public void setId (long id) {
        this.id = id;
    }

    public long getTimeResult () {
        return timeResult;
    }

    public void setTimeResult (long timeResult) {
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

    public long getPlayerId () {
        return playerId;
    }

    public void setPlayerId (int playerId) {
        this.playerId = playerId;
    }

    public long getGameId () {
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
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (timeResult ^ (timeResult >>> 32));
        result = 31 * result + (createdOn != null ? createdOn.hashCode() : 0);
        result = 31 * result + (syncedOn != null ? syncedOn.hashCode() : 0);
        result = 31 * result + (int) (playerId ^ (playerId >>> 32));
        result = 31 * result + (int) (gameId ^ (gameId >>> 32));
        return result;
    }

    @Override
    public String toString () {
        return "GameResult{" +
                "id=" + id +
                ", timeResult=" + timeResult +
                ", createdOn=" + createdOn +
                ", syncedOn=" + syncedOn +
                ", playerId=" + playerId +
                ", gameId=" + gameId +
                '}';
    }

    @Override
    public int describeContents () {
        return 0;
    }

    @Override
    public void writeToParcel (Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(timeResult);
        dest.writeLong(createdOn != null ? createdOn.getTime() : -1L);
        dest.writeLong(syncedOn != null ? syncedOn.getTime() : -1L);
        dest.writeLong(playerId);
        dest.writeLong(gameId);
    }

}
