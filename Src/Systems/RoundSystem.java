package Systems;

import Components.RoundComponent;

public class RoundSystem extends EntitySystem{
    RoundComponent round = new RoundComponent();

    public int getRound() {
        return round.getCurrentRound();
    }

    public void nextRound() {
        if (round.getCurrentRound() < RoundComponent.MAX_ROUND) {
            round.setCurrentRound(round.getCurrentRound() + 1);
        }
    }

    public boolean noMoreRound() {
        return round.getCurrentRound() >= 3;
    }
}
