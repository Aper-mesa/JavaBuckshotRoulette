package Systems;

import Components.TurnComponent;

public class TurnSystem extends ComponentSystem {
    TurnComponent turn = new TurnComponent();

    public boolean isPlayer1Turn() {
        return turn.isPlayerTurn;
    }

    public boolean isPlayer2Turn() {
        return !isPlayer1Turn();
    }

    public void player1Turn() {
        if (turn.handcuffed) {
            return;
        }
        turn.isPlayerTurn = true;
    }

    public void player2Turn() {
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
