package Components;

public class HealthComponent extends Component {
    int amount;
    static final int MAX = 4;

    public HealthComponent(int amount) {
        this.amount = amount;
    }
}
