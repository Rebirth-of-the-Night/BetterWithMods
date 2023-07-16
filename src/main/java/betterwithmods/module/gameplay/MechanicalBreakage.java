package betterwithmods.module.gameplay;

import betterwithmods.module.Feature;

/**
 * Created by primetoxinz on 4/20/17.
 */
public class MechanicalBreakage extends Feature {
    public static boolean gearbox, saw, hopper, millstone, pulley, turntable;

    @Override
    public String getFeatureDescription() {
        return "When overpowered the configurable blocks will drop the component items";
    }

    @Override
    public void setupConfig() {
        gearbox = loadPropBool("Gearbox","generating drops when overpowered", true);
        saw = loadPropBool("Saw","generating drops when overpowered", true);
        hopper = loadPropBool("Hopper","generating drops when overpowered", true);
        millstone = loadPropBool("Millstone","generating drops when overpowered", true);
        pulley = loadPropBool("Pulley","generating drops when overpowered", true);
        turntable = loadPropBool("Turntable","generating drops when overpowered", true);
    }
}
