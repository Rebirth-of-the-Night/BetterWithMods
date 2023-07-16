package betterwithmods.module.tweaks;

import betterwithmods.module.Module;

/**
 * Created by primetoxinz on 4/20/17.
 */
public class Tweaks extends Module {
    @Override
    public void addFeatures() {
        registerFeature(new FastStick().recipes());
        registerFeature(new CheaperAxes().recipes());
        registerFeature(new DetectorRail().recipes());
        registerFeature(new SaddleRecipe().recipes());
        registerFeature(new WoolArmor().recipes());
        registerFeature(new AxeLeaves());
        registerFeature(new CreeperShearing());
        registerFeature(new Dung());
        registerFeature(new EasyBreeding());
        registerFeature(new MoreTempting());
        registerFeature(new EggDrops());
        registerFeature(new EquipmentDrop());
        registerFeature(new ImprovedFlee());
        registerFeature(new HeadDrops());
        registerFeature(new KilnCharcoal());
        registerFeature(new KilnSmelting());
        registerFeature(new MobSpawning());
        registerFeature(new MossGeneration());
        registerFeature(new RenewableEndstone());
        registerFeature(new RSBlockGlow());
        registerFeature(new Sinkholes());
        registerFeature(new MysteryMeat());
        registerFeature(new GrassPath());
        registerFeature(new DarkQuartz());
        registerFeature(new CactusSkeleton());
        registerFeature(new BatWings());
        registerFeature(new FoodPoisoning());
        registerFeature(new Notes());
        registerFeature(new MineshaftGeneration());
        registerFeature(new VisibleStorms());
        registerFeature(new LongBoi());
        registerFeature(new MobEating());
        registerFeature(new LlamaDrops());
        registerFeature(new BabyJumping());
        registerFeature(new EnchantmentTooltip());
        registerFeature(new NoSkeletonTrap());
        registerFeature(new SilverfishClay());
        registerFeature(new AnimalBirth());
        registerFeature(new HopperMinecarts());
        registerFeature(new ExplosionTracker());
        //        registerFeature(new MushroomFarming());
    }

    @Override
    public String getModuleDescription() {
        return "General Tweaks to the game, Vanilla or BWM itself";
    }
}
