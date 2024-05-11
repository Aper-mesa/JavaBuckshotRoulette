import Components.*;
import Systems.*;

public class DealerAI {
    Engine engine;
    boolean shootSelf;
    public boolean nextBall = false;

    public DealerAI(Engine engine) {
        this.engine = engine;
    }

    public boolean shootSelfByBulletNumbers() {
        AmmoSystem ammoSystem = (AmmoSystem) engine.getSystem(AmmoSystem.class);
        if (ammoSystem.equalBullets()) {
            shootSelf = engine.rand.nextInt(2) == 1;
            return shootSelf;
        }
        shootSelf = ammoSystem.moreBlanks();
        return shootSelf;
    }

    public void useProp() {
        PropSystem propSystem = (PropSystem) engine.getSystem(PropSystem.class);
        PersonSystem personSystem = (PersonSystem) engine.getSystem(PersonSystem.class);
        TurnSystem turnSystem = (TurnSystem) engine.getSystem(TurnSystem.class);
        //handcuff has the highest priority
        if (turnSystem.notHandcuffed() && propSystem.dealerHasProp(HandcuffComponent.class)) {
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
        //use magnifier to check the bullet; a ball directly makes him shoot the player
        if (propSystem.dealerHasProp(MagnifierComponent.class)) {
            System.out.println("DEALER USED MAGNIFIER");
            Component bullet = propSystem.magnifier();
            if (bullet instanceof BallComponent) {
                shootSelf = false;
                nextBall = true;
            }
            propSystem.removeDealerProp(MagnifierComponent.class);
        }
        //use handsaw if he wants to shoot the player
        if ((!shootSelfByBulletNumbers() || nextBall) && propSystem.dealerHasProp(HandsawComponent.class)) {
            System.out.println("DEALER USED HANDSAW");
            propSystem.handsaw();
            propSystem.removeDealerProp(HandsawComponent.class);
        }
        //use beer if he wants to shoot himself
        while ((shootSelfByBulletNumbers() && !nextBall) && propSystem.dealerHasProp(BeerComponent.class)) {
            System.out.println("DEALER USED BEER");
            propSystem.beer();
            propSystem.removeDealerProp(BeerComponent.class);
        }
        //use converter if he wants to shoot himself
        if ((shootSelfByBulletNumbers() || !nextBall) && propSystem.dealerHasProp(ConverterComponent.class)) {
            System.out.println("DEALER USED CONVERTER");
            propSystem.converter();
            propSystem.removeDealerProp(ConverterComponent.class);
        }
    }
}
