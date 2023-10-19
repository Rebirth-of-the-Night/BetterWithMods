package betterwithaddons.interaction.minetweaker;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.entity.IEntityLivingBase;
import stanhebben.zenscript.annotations.ZenClass;

import java.util.List;

@ZenClass("mods.betterwithaddons.IBeaconEffectFunction")
@ZenRegister
public interface IBeaconEffectFunction {
    boolean apply(IBeaconInfo beaconInfo, List<IEntityLivingBase> entities);
}
