package Systems;

import Components.BallComponent;
import Components.BlankComponent;
import Components.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Stack;

public class AmmoSystem {
    private int ballAmount;
    private int blankAmount;
    private int totalAmount;
    Stack<Component> chamber = new Stack<>();
    Random r = new Random();

    public AmmoSystem(int totalAmount) {
        nextRound(totalAmount);
    }

    public void nextRound(int totalAmount) {
        this.totalAmount = totalAmount;
        ballAmount = r.nextInt(totalAmount) + 1;
        blankAmount = totalAmount - ballAmount;
        for (int i = 0; i < ballAmount; i++) {
            chamber.push(new BallComponent());
        }
        for (int i = 0; i < blankAmount; i++) {
            chamber.push(new BlankComponent());
        }
        Collections.shuffle(chamber);
        System.out.println("*****");
        System.out.println(Arrays.toString(chamber.toArray()));
        System.out.println("*****.");
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public int getBallAmount() {
        return ballAmount;
    }

    public int getBlankAmount() {
        return blankAmount;
    }

    public Component nextBullet() {
        return chamber.pop();
    }


}
