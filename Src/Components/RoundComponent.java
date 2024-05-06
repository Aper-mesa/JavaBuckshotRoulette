package Components;

public class RoundComponent extends Component {
    private int currentRound = 1;
    public static final int MAX_ROUND = 3;

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }
}
