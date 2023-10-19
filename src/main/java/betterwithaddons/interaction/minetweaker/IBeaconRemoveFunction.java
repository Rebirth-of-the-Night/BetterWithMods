package betterwithaddons.interaction.minetweaker;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;

@ZenClass("mods.betterwithaddons.IBeaconRemoveFunction")
@ZenRegister
public interface IBeaconRemoveFunction {
    void remove(IBeaconInfo beaconInfo);
}
