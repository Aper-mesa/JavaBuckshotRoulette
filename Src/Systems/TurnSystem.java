package Systems;

import Components.TurnComponent;

public class TurnSystem extends EntitySystem {
    TurnComponent turn = new TurnComponent();

    public boolean isPlayerTurn() {
        return turn.getPlayerTurn();
    }

    public boolean isDealerTurn() {
        return !isPlayerTurn();
    }

    public void playerTurn() {
        if (turn.handcuffed) {
            return;
        }
        turn.setPlayerTurn(true);
    }

    public void dealerTurn() {
        if (turn.handcuffed) {
            return;
        }
        turn.setPlayerTurn(false);
    }

    public void handcuff() {
        turn.handcuffed = true;
    }

    public void noHandcuff() {
        turn.handcuffed = false;
    }

    public boolean isHandcuffed() {
        return turn.handcuffed;
    }

}
