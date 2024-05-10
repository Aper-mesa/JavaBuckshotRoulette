package Systems;

public class ShotgunSystem extends ComponentSystem {
    private boolean isBarrelSawed = false;

    public void sawBarrel() {
        isBarrelSawed = true;
    }

    public void respawnBarrel() {
        isBarrelSawed = false;
    }

    public boolean isBarrelSawed() {
        return isBarrelSawed;
    }
}
