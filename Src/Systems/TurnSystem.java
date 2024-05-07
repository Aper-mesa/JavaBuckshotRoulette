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
        turn.setPlayerTurn(true);
    }

    public void dealerTurn() {
        turn.setPlayerTurn(false);
    }
}
