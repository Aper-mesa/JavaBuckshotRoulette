import Components.HandcuffComponent;
import Systems.AmmoSystem;
import Systems.Engine;
import Systems.PropSystem;

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

    public void useProp() {
        PropSystem propSystem = (PropSystem) engine.getSystem(PropSystem.class);
        if (propSystem.dealerHasProp(HandcuffComponent.class)) {
            System.out.println("DEALER USED HANDCUFF");
            propSystem.handcuff();
            propSystem.removeDealerProp(HandcuffComponent.class);
        }
    }

}
