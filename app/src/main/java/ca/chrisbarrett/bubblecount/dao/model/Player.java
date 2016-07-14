package ca.chrisbarrett.bubblecount.dao.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;
import java.util.Date;

import ca.chrisbarrett.bubblecount.util.Values;

/**
 * POJO to hold a Player object, consisting of the primary key for the database (or NO_ID if not yet
 * assigned, the name of the Player for display purposes, the yearOfBirth (used with the minimumAge
 * from the Game table to determine playability) and the date created.
 *
 * @author Chris Barrett
 * @see
 * @since Jul 10, 2016
 */

public class Player  implements Parcelable {

    private int id;                 // pk of the player
    private String name;            // name of the player
    private int yearOfBirth;        // year of birth of the player
    private Date createdOn;         // date the Player was created
    private Date syncedOn;         // date the Player was created


    /**
     * Default constructor. Date will be null.
     */
    public Player () {
        this(Values.NO_ID, null, Values.NO_ID, null, null);
    }

    /**
     * Constructor called with default NO_ID. Date created on will be assigned by the system.
     *
     * @param name
     * @param yearOfBirth
     */
    public Player (String name, int yearOfBirth) {
        this(Values.NO_ID, name, yearOfBirth, Calendar.getInstance().getTime(), null);
    }

    /**
     * Constructor called from the database.
     *
     * @param id          the id key assigned in the database
     * @param name
     * @param yearOfBirth
     * @param createdOn
     * @param syncedOn
     */
    public Player (int id, String name, int yearOfBirth, Date createdOn, Date syncedOn) {
        this.id = id;
        this.name = name;
        this.yearOfBirth = yearOfBirth;
        this.createdOn = createdOn;
        this.syncedOn = syncedOn;
    }



    public int getId () {
        return id;
    }

    public void setId (int id) {
        this.id = id;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public int getYearOfBirth () {
        return yearOfBirth;
    }

    public void setYearOfBirth (int yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
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

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        if (id != player.id) return false;
        if (yearOfBirth != player.yearOfBirth) return false;
        if (name != null ? !name.equals(player.name) : player.name != null) return false;
        return !(createdOn != null ? !createdOn.equals(player.createdOn) : player.createdOn != null);

    }

    @Override
    public int hashCode () {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + yearOfBirth;
        result = 31 * result + (createdOn != null ? createdOn.hashCode() : 0);
        return result;
    }

    protected Player(Parcel in) {
        id = in.readInt();
        name = in.readString();
        yearOfBirth = in.readInt();
        long tmpCreatedOn = in.readLong();
        createdOn = tmpCreatedOn != -1 ? new Date(tmpCreatedOn) : null;
        long tmpSyncedOn = in.readLong();
        syncedOn = tmpSyncedOn != -1 ? new Date(tmpSyncedOn) : null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(yearOfBirth);
        dest.writeLong(createdOn != null ? createdOn.getTime() : -1L);
        dest.writeLong(syncedOn != null ? syncedOn.getTime() : -1L);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Player> CREATOR = new Parcelable.Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };
}

