package lab;

public class Score {
    private String namePlayer;
    private int scorePlayer;

    public Score(String namePlayerOne, int scorePlayerOne) {
        this.namePlayer = namePlayerOne;
        this.scorePlayer = scorePlayerOne;
    }

    public String getNamePlayer() {
        return namePlayer;
    }

    public int getScorePlayer() {
        return scorePlayer;
    }

}