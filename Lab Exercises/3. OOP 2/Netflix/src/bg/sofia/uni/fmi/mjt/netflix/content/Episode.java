package bg.sofia.uni.fmi.mjt.netflix.content;

public class Episode {

    private String name;
    private int duration;

    public Episode(String name, int duration) {
        this.name = name;
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }
}
