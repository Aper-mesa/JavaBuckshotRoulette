package Components;

public class HealthComponent extends Component {
    private int amount;
    public int maxHealth;

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
