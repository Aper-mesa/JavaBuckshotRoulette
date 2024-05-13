package Core;

import Components.*;

import static Core.Engine.*;

public class DealerAI {
    static boolean shootSelf;
    public static boolean nextBall = false;
    static boolean usedMagnifier = false;

    public static boolean shootSelfByBulletNumbers() {
        if (ammoSystem.equalBullets()) {
            shootSelf = rand.nextInt(2) == 1;
            return shootSelf;
        }
        shootSelf = ammoSystem.moreBlanks();
        return shootSelf;
    }

    public static void useProp() {
        usedMagnifier = false;
        nextBall = false;
        //handcuff has the highest priority
        if (turnSystem.notHandcuffed() && propSystem.dealerHasProp(HandcuffComponent.class)) {
            System.out.println("大哥使用了手铐");
            propSystem.handcuff();
            propSystem.removeDealerProp(HandcuffComponent.class);
        }
        for (int i = 0; i < 2; i++) {
            //use medicine if he is wounded and has no cigarette
            while (personSystem.isWounded() && !propSystem.dealerHasProp(CigaretteComponent.class)
                    && propSystem.dealerHasProp(MedicineComponent.class)) {
                System.out.println("大哥使用了药片");
                propSystem.medicine();
                propSystem.removeDealerProp(MedicineComponent.class);
            }
            //use cigarette if he is wounded
            while (personSystem.isWounded() && propSystem.dealerHasProp(CigaretteComponent.class)) {
                System.out.println("大哥使用了香烟");
                propSystem.cigarette();
                propSystem.removeDealerProp(CigaretteComponent.class);
            }
        }
        //steal if he has adrenaline and player has props; the order is the same as the order of dealer uses his own
        //if the dealer is healthy and the player only has health props, do not use adrenaline
        if (propSystem.dealerHasProp(AdrenalineComponent.class) && !propSystem.playerProps.isEmpty()
                && !(!personSystem.isWounded() && propSystem.playerOnlyHealthProps())) {
            System.out.println("大哥使用了肾上腺素");
            //use player's handcuff if the player is not handcuffed
            if (propSystem.playerHasProp(HandcuffComponent.class) && turnSystem.notHandcuffed()) {
                System.out.println("大哥偷了你的手铐");
                propSystem.dealerAdrenaline(HandcuffComponent.class);
                propSystem.handcuff();
            }
            //use player's cigarette if dealer is wounded; the dealer won't use player's medicine
            else if (personSystem.isWounded() && propSystem.playerHasProp(CigaretteComponent.class)) {
                System.out.println("大哥偷了你的香烟");
                propSystem.dealerAdrenaline(CigaretteComponent.class);
                propSystem.cigarette();
            }
            //use player's magnifier
            else if (propSystem.playerHasProp(MagnifierComponent.class)) {
                usedMagnifier = true;
                System.out.println("大哥偷了你的放大镜");
                propSystem.dealerAdrenaline(MagnifierComponent.class);
                magnifier();
            } else if (propSystem.playerHasProp(PhoneComponent.class)) {
                System.out.println("大哥偷了你的手机");
                propSystem.dealerAdrenaline(PhoneComponent.class);
                propSystem.dealerPhone();
            }
            //use player's handsaw
            else if (propSystem.playerHasProp(HandsawComponent.class) && (!shootSelfByBulletNumbers() || nextBall)) {
                System.out.println("大哥偷了你的锯子");
                propSystem.dealerAdrenaline(HandsawComponent.class);
                propSystem.handsaw();
            }
            //use player's beer
            else if (propSystem.playerHasProp(BeerComponent.class) && (shootSelfByBulletNumbers() && !nextBall)) {
                System.out.println("大哥偷了你的啤酒");
                propSystem.dealerAdrenaline(BeerComponent.class);
                propSystem.beer();
            }
            //use player's converter
            else if (!ammoSystem.noBullet() && propSystem.dealerHasProp(ConverterComponent.class)
                    && (shootSelfByBulletNumbers() || !nextBall)) {
                System.out.println("大哥偷了你的转换器");
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
            System.out.println("大哥用了手机");
            propSystem.dealerPhone();
        }
        //use handsaw if he wants to shoot the player
        if (!ammoSystem.noBullet() && (!shootSelfByBulletNumbers() || nextBall)
                && propSystem.dealerHasProp(HandsawComponent.class)) {
            System.out.println("大哥用了锯子");
            propSystem.handsaw();
            propSystem.removeDealerProp(HandsawComponent.class);
        }
        //use beer if he wants to shoot himself
        while (!ammoSystem.noBullet() && (shootSelfByBulletNumbers() && !nextBall)
                && propSystem.dealerHasProp(BeerComponent.class)) {
            System.out.println("大哥用了啤酒");
            propSystem.beer();
            propSystem.removeDealerProp(BeerComponent.class);
        }
        //use converter if he wants to shoot himself
        if (!ammoSystem.noBullet() && (shootSelfByBulletNumbers() && !nextBall)
                && propSystem.dealerHasProp(ConverterComponent.class)) {
            System.out.println("大哥用了转换器");
            propSystem.converter();
            propSystem.removeDealerProp(ConverterComponent.class);
        }
    }

    private static void magnifier() {
        System.out.println("大哥用了放大镜");
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
