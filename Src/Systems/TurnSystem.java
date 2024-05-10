package Systems;

import Components.TurnComponent;

public class TurnSystem extends ComponentSystem {
    TurnComponent turn = new TurnComponent();

    public boolean isPlayerTurn() {
        return turn.isPlayerTurn;
    }

    public boolean isDealerTurn() {
        return !isPlayerTurn();
    }

    public void playerTurn() {
        if (turn.handcuffed) {
            return;
        }
        turn.isPlayerTurn = true;
    }

    public void dealerTurn() {
        if (turn.handcuffed) {
            return;
        }
        turn.isPlayerTurn = false;
    }

    public void handcuff() {
        turn.handcuffed = true;
    }

    public void noHandcuff() {
        turn.handcuffed = false;
    }

    public boolean notHandcuffed() {
        return !turn.handcuffed;
    }

}
