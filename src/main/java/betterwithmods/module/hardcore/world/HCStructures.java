package betterwithmods.module.hardcore.world;

import betterwithmods.common.BWMRecipes;
import betterwithmods.common.world.BWComponentScatteredFeaturePieces;
import betterwithmods.common.world.BWMapGenScatteredFeature;
import betterwithmods.module.Feature;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by primetoxinz on 4/20/17.
 */
public class HCStructures extends Feature {
    public static int HARDCORE_STRUCTURE_RADIUS;
    private boolean disableRecipes;

    public static boolean isInRadius(World world, int x, int z) {
        BlockPos center = world.getSpawnPoint();
        return Math.sqrt(Math.pow(x - center.getX(), 2) + Math.pow(z - center.getZ(), 2)) < HARDCORE_STRUCTURE_RADIUS;
    }

    @Override
    public String getFeatureDescription() {
        return "Makes it so structures are looted within a radius of spawn and unlooted outside of that radius. Encourages exploration and also makes unlooted structures the only source of Enchanting Tables and Brewing Stands.";
    }

    @Override
    public void setupConfig() {
        HARDCORE_STRUCTURE_RADIUS = loadPropInt("Hardcore Structure Radius", "Radius from original spawn which structures will be abandoned in", 2000);
        disableRecipes = loadPropBool("Disable Recipes", "Disable Recipes for blocks that generate only in structures, including Enchanting Tables and Brewing Stands", true);
    }

    @Override
    public boolean requiresMinecraftRestartToEnable() {
        return true;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        if (disableRecipes) {
            BWMRecipes.removeRecipe(new ItemStack(Blocks.ENCHANTING_TABLE));
            BWMRecipes.removeRecipe(new ItemStack(Items.BREWING_STAND));
        }
    }

    @Override
    public void init(FMLInitializationEvent event) {
        MapGenStructureIO.registerStructure(BWMapGenScatteredFeature.Start.class, "BWTemple");
        MapGenStructureIO.registerStructureComponent(BWComponentScatteredFeaturePieces.DesertPyramid.class, "BWTeDP");
        MapGenStructureIO.registerStructureComponent(BWComponentScatteredFeaturePieces.JunglePyramid.class, "BWTeJP");
        MapGenStructureIO.registerStructureComponent(BWComponentScatteredFeaturePieces.SwampHut.class, "BWTeSH");
        //MapGenStructureIO.registerStructureComponent(BWComponentScatteredFeaturePieces.Igloo.class, "BWIglu");
    }

    @SubscribeEvent
    public void overrideScatteredFeature(InitMapGenEvent event) {
        if (event.getType().equals(InitMapGenEvent.EventType.SCATTERED_FEATURE))
            event.setNewGen(new BWMapGenScatteredFeature());
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

    @Override
    public boolean hasTerrainSubscriptions() {
        return true;
    }
}
