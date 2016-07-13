package ca.chrisbarrett.bubblecount.dao.model;

import android.os.Parcel;
import android.os.Parcelable;

import ca.chrisbarrett.bubblecount.util.Values;

/**
 * POJO to hold a Game object, consisting of the primary key for the database (or NO_ID if not yet
 * assigned, the name of the game for display purposes, and the minimum of required for the game.
 *
 * @author Chris Barrett
 * @see
 * @since Jul 10, 2016
 */

public class Game implements Parcelable {

    private int id;             // pk of the game
    private String name;        // name of the game
    private int minimumAge;     // minimum age of the game

    /**
     * Default constructor.
     */
    public Game () {
        this(Values.NO_ID, "", Values.NO_ID);
    }

    /**
     * Constructor called with default NO_ID value.
     *
     * @param name       the name of the game for display purposes
     * @param minimumAge the minimum age required for the game
     */
    public Game (String name, int minimumAge) {
        this(Values.NO_ID, name, minimumAge);
    }

    /**
     * Constructor called from the database.
     *
     * @param id         the id key assigned in the database
     * @param name
     * @param minimumAge
     */
    public Game (int id, String name, int minimumAge) {
        this.id = id;
        this.name = name;
        this.minimumAge = minimumAge;
    }

    protected Game (Parcel in) {
        id = in.readInt();
        name = in.readString();
        minimumAge = in.readInt();
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

    public int getMinimumAge () {
        return minimumAge;
    }

    public void setMinimumAge (int minimumAge) {
        this.minimumAge = minimumAge;
    }

    /**
     * As the database PK is unique, equals should run find on that.
     * As a safety check, the name will also be used for those objects not yet persisted.
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Game game = (Game) o;

        if (id != game.id) return false;
        return !(name != null ? !name.equals(game.name) : game.name != null);

    }

    @Override
    public int hashCode () {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents () {
        return 0;
    }

    @Override
    public void writeToParcel (Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(minimumAge);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Game> CREATOR = new Parcelable.Creator<Game>() {
        @Override
        public Game createFromParcel (Parcel in) {
            return new Game(in);
        }

        @Override
        public Game[] newArray (int size) {
            return new Game[size];
        }
    };
}