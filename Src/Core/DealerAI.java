package Core;

import Components.*;
import Systems.*;

public class DealerAI {
    Engine engine;
    boolean shootSelf;
    public boolean nextBall = false;
    boolean usedMagnifier = false;
    PropSystem propSystem;
    PersonSystem personSystem;
    TurnSystem turnSystem;
    AmmoSystem ammoSystem;

    public DealerAI(Engine engine) {
        this.engine = engine;
        propSystem = (PropSystem) engine.getSystem(PropSystem.class);
        personSystem = (PersonSystem) engine.getSystem(PersonSystem.class);
        turnSystem = (TurnSystem) engine.getSystem(TurnSystem.class);
        ammoSystem = (AmmoSystem) engine.getSystem(AmmoSystem.class);
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
        usedMagnifier = false;
        nextBall = false;
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
            else if (personSystem.isWounded() && propSystem.playerHasProp(CigaretteComponent.class)) {
                System.out.println("DEALER STOLE YOUR CIGARETTE");
                propSystem.dealerAdrenaline(CigaretteComponent.class);
                propSystem.cigarette();
                return;
            }
            //use player's magnifier
            else if (propSystem.playerHasProp(MagnifierComponent.class)) {
                usedMagnifier = true;
                System.out.println("DEALER STOLE YOUR MAGNIFIER");
                propSystem.dealerAdrenaline(MagnifierComponent.class);
                magnifier();
            }
            //use player's handsaw
            else if (propSystem.playerHasProp(HandsawComponent.class) && (!shootSelfByBulletNumbers() || nextBall)) {
                System.out.println("DEALER STOLE YOUR HANDSAW");
                propSystem.dealerAdrenaline(HandsawComponent.class);
                propSystem.handsaw();
            }
            //use player's beer
            else if (propSystem.playerHasProp(BeerComponent.class) && (shootSelfByBulletNumbers() && !nextBall)) {
                System.out.println("DEALER STOLE YOUR BEER");
                propSystem.dealerAdrenaline(BeerComponent.class);
                propSystem.beer();
            }
            //use player's converter
            else if (!ammoSystem.noBullet() && propSystem.dealerHasProp(ConverterComponent.class)
                    && (shootSelfByBulletNumbers() || !nextBall)) {
                System.out.println("DEALER STOLE YOUR CONVERTER");
                propSystem.dealerAdrenaline(ConverterComponent.class);
                propSystem.converter();
            }
            propSystem.removeDealerProp(AdrenalineComponent.class);
        }
        //use magnifier to check the bullet; a ball directly makes him shoot the player
        if (!usedMagnifier && propSystem.dealerHasProp(MagnifierComponent.class)) {
            magnifier();
            propSystem.removeDealerProp(MagnifierComponent.class);
        }
        //use phone
        if (propSystem.dealerHasProp(PhoneComponent.class) && !ammoSystem.oneBullet()) {
            System.out.println("DEALER USED PHONE");
            propSystem.dealerPhone();
            System.out.println("Blanks: " + propSystem.dealerBlankIndexes + "Balls: " + propSystem.dealerBallIndexes);
            propSystem.removeDealerProp(PhoneComponent.class);
        }
        //use handsaw if he wants to shoot the player
        if (!ammoSystem.noBullet() && (!shootSelfByBulletNumbers() || nextBall)
                && propSystem.dealerHasProp(HandsawComponent.class)) {
            System.out.println("DEALER USED HANDSAW");
            propSystem.handsaw();
            propSystem.removeDealerProp(HandsawComponent.class);
        }
        //use beer if he wants to shoot himself
        while (!ammoSystem.noBullet() && (shootSelfByBulletNumbers() && !nextBall)
                && propSystem.dealerHasProp(BeerComponent.class)) {
            System.out.println("DEALER USED BEER");
            propSystem.beer();
            propSystem.removeDealerProp(BeerComponent.class);
        }
        //use converter if he wants to shoot himself
        if (!ammoSystem.noBullet() && (shootSelfByBulletNumbers() && !nextBall)
                && propSystem.dealerHasProp(ConverterComponent.class)) {
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
        } else {
            shootSelf = true;
            nextBall = false;
        }
    }
}
