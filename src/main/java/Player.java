import java.io.Serializable;

public class Player implements Serializable{

    private long id;
    private String firstName;
    private String secondName;
    private boolean isPlay;

    //default constructor
    public Player() {
    }

    public String getFullName() {
        return firstName + " " + secondName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }
}