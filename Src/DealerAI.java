import Components.*;
import Systems.AmmoSystem;
import Systems.Engine;
import Systems.PersonSystem;
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
        PersonSystem personSystem = (PersonSystem) engine.getSystem(PersonSystem.class);
        //handcuff has the highest priority
        if (propSystem.dealerHasProp(HandcuffComponent.class)) {
            System.out.println("DEALER USED HANDCUFF");
            propSystem.handcuff();
            propSystem.removeDealerProp(HandcuffComponent.class);
        }
        for (int i = 0; i < 2; i++) {
            //use medicine if he is wounded and has no cigarette
            while (personSystem.isWounded() && !propSystem.dealerHasProp(CigaretteComponent.class)
                    && propSystem.dealerHasProp(MedicineComponent.class)) {
                System.out.println("DEALER USED MEDICINE");
                propSystem.medicine();
                propSystem.removeDealerProp(MedicineComponent.class);
            }
            //use cigarette if he is wounded
            while (personSystem.isWounded() && propSystem.dealerHasProp(CigaretteComponent.class)) {
                System.out.println("DEALER USED CIGARETTE");
                propSystem.cigarette();
                propSystem.removeDealerProp(CigaretteComponent.class);
            }
        }
        //use handsaw if he wants to shoot the player
        if (!shootSelf() && propSystem.dealerHasProp(HandsawComponent.class)) {
            System.out.println("DEALER USED HANDSAW");
            propSystem.handsaw();
            propSystem.removeDealerProp(HandsawComponent.class);
        }
        //use beer if he wants to shoot himself
        while (shootSelf() && propSystem.dealerHasProp(BeerComponent.class)) {
            System.out.println("DEALER USED BEER");
            propSystem.beer();
            propSystem.removeDealerProp(BeerComponent.class);
        }
    }
}
