package betterwithaddons.interaction.minetweaker;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;

@ZenClass("mods.betterwithaddons.IBeaconCreateFunction")
@ZenRegister
public interface IBeaconCreateFunction {
    void create(IBeaconInfo beaconInfo);
}
