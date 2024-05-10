import Systems.AmmoSystem;
import Systems.Engine;

public class DealerAI {
    Engine engine;

    public DealerAI(Engine engine) {
        this.engine = engine;
    }

    public boolean shootSelf() {
        AmmoSystem ammoSystem = (AmmoSystem) engine.getSystem(AmmoSystem.class);
        if (ammoSystem.equalBullets()) {
            return engine.rand.nextInt(2) == 1;
        }
        return ammoSystem.moreBlanks();
    }

}
