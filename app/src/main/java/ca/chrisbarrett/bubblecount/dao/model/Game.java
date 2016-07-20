package ca.chrisbarrett.bubblecount.dao.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * POJO to hold a Game object, consisting of the primary key for the database (or NO_ID if not yet
 * assigned, the displayable of the game for display purposes and the fully qualified class path to
 * the class for a future class loader, and the minimum of required for the game.
 *
 * @author Chris Barrett
 * @see
 * @since Jul 10, 2016
 */

public class Game implements Parcelable {

    public static final long RANDOM = 1;
    public static final long NO_ID = 1;
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
    private long id;                     // pk of the game
    private String displayName;         // displays the name of the game
    private String classPathName;       // class path to the game
    private int minimumAge;             // minimum age of the game

    /**
     * Default constructor.
     */
    public Game () {
        this(NO_ID, null, null, -1);
    }

    /**
     * Constructor called with default NO_ID value.
     *
     * @param displayName   the displayName of the game for display purposes
     * @param classPathName
     * @param minimumAge    the minimum age required for the game
     */
    public Game (String displayName, String classPathName, int minimumAge) {
        this(NO_ID, displayName, classPathName, minimumAge);
    }


    /**
     * Constructor called from the database.
     *
     * @param id
     * @param displayName
     * @param classPathName
     * @param minimumAge
     */
    public Game (long id, String displayName, String classPathName, int minimumAge) {
        this.id = id;
        this.displayName = displayName;
        this.classPathName = classPathName;
        this.minimumAge = minimumAge;
    }

    protected Game (Parcel in) {
        id = in.readLong();
        displayName = in.readString();
        classPathName = in.readString();
        minimumAge = in.readInt();
    }

    public long getId () {
        return id;
    }

    public void setId (long id) {
        this.id = id;
    }

    public String getDisplayName () {
        return displayName;
    }

    public void setDisplayName (String displayName) {
        this.displayName = displayName;
    }

    public String getClassPathName () {
        return classPathName;
    }

    public void setClassPathName (String classPathName) {
        this.classPathName = classPathName;
    }

    public int getMinimumAge () {
        return minimumAge;
    }

    public void setMinimumAge (int minimumAge) {
        this.minimumAge = minimumAge;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Game game = (Game) o;

        if (id != game.id) return false;
        if (minimumAge != game.minimumAge) return false;
        if (displayName != null ? !displayName.equals(game.displayName) : game.displayName != null)
            return false;
        return !(classPathName != null ? !classPathName.equals(game.classPathName) : game.classPathName != null);

    }

    @Override
    public int hashCode () {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (displayName != null ? displayName.hashCode() : 0);
        result = 31 * result + (classPathName != null ? classPathName.hashCode() : 0);
        result = 31 * result + minimumAge;
        return result;
    }

    @Override
    public String toString () {
        return "Game{" +
                "id=" + id +
                ", displayName='" + displayName + '\'' +
                ", classPathName='" + classPathName + '\'' +
                ", minimumAge=" + minimumAge +
                '}';
    }

    @Override
    public int describeContents () {
        return 0;
    }

    @Override
    public void writeToParcel (Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(displayName);
        dest.writeString(classPathName);
        dest.writeInt(minimumAge);
    }
}