package Components;

public class HealthComponent extends Component {
    private int amount;
    public static final int MAX = 4;

    public HealthComponent(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
