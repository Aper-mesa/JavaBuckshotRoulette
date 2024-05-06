package Components;

public class TurnComponent extends Component {
    private boolean isPlayerTurn = true;

    public void setPlayerTurn(boolean playerTurn) {
        isPlayerTurn = playerTurn;
    }

    public boolean getPlayerTurn() {
        return isPlayerTurn;
    }
}
