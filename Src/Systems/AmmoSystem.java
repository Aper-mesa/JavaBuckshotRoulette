package Systems;

import Components.BallComponent;
import Components.BlankComponent;
import Components.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Stack;

public class AmmoSystem extends ComponentSystem {
    private int ballAmount;
    private int blankAmount;
    Stack<Component> chamber = new Stack<>();
    Stack<Component> chamberDuplication = new Stack<>();
    Engine engine;

    public AmmoSystem(Engine engine, int totalAmount) {
        this.engine = engine;
        reload(totalAmount);
    }

    public void reload(int totalAmount) {
        System.out.println("\t\t\t\u001B[31mSHOTGUN RELOADED\u001B[0m");
        //even bullets always have half balls and blanks
        //odd bullets have x/2 or x/2 + 1
        if (totalAmount % 2 == 0) ballAmount = totalAmount / 2;
        else ballAmount = totalAmount / 2 + engine.rand.nextInt(2);
        blankAmount = totalAmount - ballAmount;
        chamber.clear();
        chamberDuplication.clear();
        for (int i = 0; i < ballAmount; i++) {
            chamber.push(new BallComponent());
        }
        for (int i = 0; i < blankAmount; i++) {
            chamber.push(new BlankComponent());
        }
        Collections.shuffle(chamber);
        for (int i = chamber.size() - 1; i >= 0; i--) {
            chamberDuplication.push(chamber.get(i));
        }
        cheat();
    }

    //for testing only
    public void cheat() {
        System.out.println("\t\t\t" + Arrays.toString(chamber.toArray()));
    }

    public boolean noBullet() {
        return chamber.isEmpty();
    }

    public int getBallAmount() {
        return ballAmount;
    }

    public int getBlankAmount() {
        return blankAmount;
    }

    public Component nextBullet() {
        Component bullet = chamber.pop();
        if (bullet instanceof BallComponent) {
            ballAmount--;
        } else {
            blankAmount--;
        }
        return bullet;
    }

    public Component checkBullet() {
        return chamber.peek();
    }

    public void convertCurrentBullet() {
        Component bullet = chamber.pop();
        if (bullet instanceof BlankComponent) {
            chamber.push(new BallComponent());
        } else {
            chamber.push(new BlankComponent());
        }
    }

    public int getTotalAmount() {
        return blankAmount + ballAmount;
    }

    public Component checkBulletByPhone(int index) {
        return chamberDuplication.get(index);
    }

    public boolean moreBlanks() {
        return blankAmount > ballAmount;
    }

    public boolean equalBullets() {
        return blankAmount == ballAmount;
    }

}
