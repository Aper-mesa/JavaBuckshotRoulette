package Systems;

import Components.TurnComponent;

public class TurnSystem {
    TurnComponent turn = new TurnComponent();

    public boolean isPlayerTurn() {
        return turn.getPlayerTurn();
    }

    public void playerTurn() {
        turn.setPlayerTurn(true);
    }

    public void dealerTurn() {
        turn.setPlayerTurn(false);
    }
}
