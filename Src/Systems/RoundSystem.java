package Systems;

import Components.RoundComponent;

public class RoundSystem extends ComponentSystem {
    RoundComponent round = new RoundComponent();

    public int getRound() {
        return round.currentRound;
    }

    public void nextRound() {
//        if (round.getCurrentRound() < RoundComponent.MAX_ROUND) {
        round.currentRound = round.currentRound + 1;
//        }
    }

    public boolean noMoreRound() {
        return round.currentRound > 3;
    }
}
