package Systems;

import Components.TurnComponent;

import static Core.Engine.personSystem;

public class TurnSystem extends ComponentSystem {
    TurnComponent turn = new TurnComponent();

    public boolean isPlayer1Turn() {
        return turn.isPlayer1Turn;
    }

    public boolean isPlayer2Turn() {
        return !isPlayer1Turn();
    }

    public void player1Turn() {
        if (turn.handcuffed) {
            return;
        }
        turn.isPlayer1Turn = true;
    }

    public void player2Turn() {
        if (turn.handcuffed) {
            return;
        }
        turn.isPlayer1Turn = false;
    }

    public void handcuff() {
        turn.handcuffed = true;
        if (turn.isPlayer1Turn) {
            System.out.println(personSystem.player2Name() + "被铐住");
        } else System.out.println(personSystem.player1Name() + "被铐住");
    }

    public void noHandcuff() {
        turn.handcuffed = false;
    }

    public boolean notHandcuffed() {
        return !turn.handcuffed;
    }
}
