import Components.*;
import Systems.*;

public class DealerAI {
    Engine engine;
    boolean shootSelf;
    public boolean nextBall = false;
    PropSystem propSystem = (PropSystem) engine.getSystem(PropSystem.class);
    PersonSystem personSystem = (PersonSystem) engine.getSystem(PersonSystem.class);
    TurnSystem turnSystem = (TurnSystem) engine.getSystem(TurnSystem.class);

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
        //steal if he has adrenaline and player has props; the order is the same as the order of dealer uses his own
        if (propSystem.dealerHasProp(AdrenalineComponent.class) && !propSystem.playerProps.isEmpty()) {
            System.out.println("DEALER USED ADRENALINE");
            //use player's handcuff if the player is not handcuffed
            if (propSystem.playerHasProp(HandcuffComponent.class) && turnSystem.notHandcuffed()) {
                System.out.println("DEALER STOLE YOUR HANDCUFF");
                propSystem.dealerAdrenaline(HandcuffComponent.class);
                propSystem.handcuff();
            }
            //use player's cigarette if dealer is wounded; the dealer won't use player's medicine
            if (personSystem.isWounded() && propSystem.playerHasProp(CigaretteComponent.class)) {
                System.out.println("DEALER STOLE YOUR CIGARETTE");
                propSystem.dealerAdrenaline(CigaretteComponent.class);
                propSystem.cigarette();
                return;
            }
            //use player's magnifier
            if (propSystem.playerHasProp(MagnifierComponent.class)) {
                System.out.println("DEALER STOLE YOUR MAGNIFIER");
                propSystem.dealerAdrenaline(MagnifierComponent.class);
                magnifier();
            }
            //use player's handsaw
            if (propSystem.playerHasProp(HandsawComponent.class) && (!shootSelfByBulletNumbers() || nextBall)) {
                System.out.println("DEALER STOLE YOUR HANDSAW");
                propSystem.dealerAdrenaline(HandsawComponent.class);
                System.out.println("DEALER USED HANDSAW");
                propSystem.handsaw();
            }
            //use player's beer
            if (propSystem.playerHasProp(BeerComponent.class) && (shootSelfByBulletNumbers() && !nextBall)) {
                System.out.println("DEALER STOLE YOUR BEER");
                propSystem.dealerAdrenaline(BeerComponent.class);
                System.out.println("DEALER USED BEER");
                propSystem.beer();
            }
            //use player's converter
            if (propSystem.playerHasProp(ConverterComponent.class) && (shootSelfByBulletNumbers() || !nextBall)) {
                System.out.println("DEALER STOLE YOUR CONVERTER");
                propSystem.dealerAdrenaline(ConverterComponent.class);
                System.out.println("DEALER USED CONVERTER");
                propSystem.converter();
            }
            propSystem.removeDealerProp(AdrenalineComponent.class);
        }
        //use magnifier to check the bullet; a ball directly makes him shoot the player
        if (propSystem.dealerHasProp(MagnifierComponent.class)) {
            magnifier();
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

    private void magnifier() {
        System.out.println("DEALER USED MAGNIFIER");
        Component bullet = propSystem.magnifier();
        if (bullet instanceof BallComponent) {
            shootSelf = false;
            nextBall = true;
        }
    }
}
