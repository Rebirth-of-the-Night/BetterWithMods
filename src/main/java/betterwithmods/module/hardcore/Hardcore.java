package betterwithmods.module.hardcore;

import betterwithmods.client.gui.GuiStatus;
import betterwithmods.module.CompatModule;
import betterwithmods.module.ModuleLoader;
import betterwithmods.module.hardcore.beacons.HCBeacons;
import betterwithmods.module.hardcore.crafting.*;
import betterwithmods.module.hardcore.crafting.brewing.HCBrewing;
import betterwithmods.module.hardcore.creatures.*;
import betterwithmods.module.hardcore.creatures.chicken.HCChickens;
import betterwithmods.module.hardcore.needs.*;
import betterwithmods.module.hardcore.needs.hunger.HCHunger;
import betterwithmods.module.hardcore.world.*;
import betterwithmods.module.hardcore.world.saplings.HCSapling;
import betterwithmods.module.hardcore.world.spawn.HCSpawn;
import betterwithmods.module.hardcore.world.strata.HCStrata;
import betterwithmods.module.hardcore.world.stumping.HCStumping;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

/**
 * Created by primetoxinz on 4/20/17.
 */
public class Hardcore extends CompatModule {
    public Hardcore() {
        priority = 1;
    }

    @Override
    public void addCompatFeatures() {
        registerCompatFeature("applecore", HCHunger.class.getName());
        registerCompatFeature("hardcorebuoy", HCBuoy.class.getName());
        registerCompatFeature("betterwithlib", HCMobEquipment.class.getName());
    }

    @Override
    public void addFeatures() {
        this.addCompatFeatures();

        registerFeature(new HCDiamond().recipes());
        registerFeature(new HCRedstone().recipes());
        registerFeature(new HCBoating().recipes());
        registerFeature(new HCFishing().recipes());

        registerFeature(new HCArmor());
        registerFeature(new HCBeacons());
        registerFeature(new HCBeds());
        registerFeature(new HCBonemeal());
        registerFeature(new HCBrewing());
        registerFeature(new HCBuckets());
        registerFeature(new HCBuoy());
        registerFeature(new HCCooking());
        registerFeature(new HCChickens());
        registerFeature(new HCEndermen());
        registerFeature(new HCGloom());
        registerFeature(new HCGunpowder());
        registerFeature(new HCHardness());
        registerFeature(new HCHunting());
        registerFeature(new HCInfo());
        registerFeature(new HCInjury());
        registerFeature(new HCJumping());
        registerFeature(new HCLumber());
        registerFeature(new HCMelon());
        registerFeature(new HCNames());
        registerFeature(new HCOres());
        registerFeature(new HCPiles());
        registerFeature(new HCSeeds());
        registerFeature(new HCSaw());
        registerFeature(new HCSheep());
        registerFeature(new HCSpawn());
        registerFeature(new HCStructures().recipes());
        registerFeature(new HCStumping());
        registerFeature(new HCTools());
        registerFeature(new HCTorches());
        registerFeature(new HCVillages());
        registerFeature(new HCMovement());
        registerFeature(new HCCobblestone());
        registerFeature(new HCDeadweight());
        registerFeature(new HCEnchanting());
        registerFeature(new HCStrata());
        registerFeature(new HCFurnace());
        registerFeature(new HCSapling());
        registerFeature(new ExplosiveRecipes());
        registerFeature(new HCBabyZombies());
        registerFeature(new HCNetherBrick());
        registerFeature(new HCFighting());

        //Disabled by default
        registerFeature(new HCHopper(), false);
//        registerFeature(new HCSquid());
//        registerFeature(new HCVillagers());
        this.load();
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

    }

    @Override
    public void initClient(FMLInitializationEvent event) {
        super.initClient(event);
        GuiStatus.isGloomLoaded = ModuleLoader.isFeatureEnabled(HCGloom.class);
        GuiStatus.isHungerLoaded = ModuleLoader.isFeatureEnabled(HCHunger.class);
        GuiStatus.isInjuryLoaded = ModuleLoader.isFeatureEnabled(HCInjury.class);
    }

    @Override
    public String getModuleDescription() {
        return "Changes to the game that make it more challenging";
    }
}
