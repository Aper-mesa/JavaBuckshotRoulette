package Components;

public class TurnComponent extends Component {
    private boolean isPlayerTurn = true;
    public boolean handcuffed = false;

    public void setPlayerTurn(boolean playerTurn) {
        isPlayerTurn = playerTurn;
    }

    public boolean getPlayerTurn() {
        return isPlayerTurn;
    }
}
