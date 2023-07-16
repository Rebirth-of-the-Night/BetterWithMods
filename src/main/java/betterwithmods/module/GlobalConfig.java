package betterwithmods.module;

import betterwithmods.client.gui.GuiStatus;
import betterwithmods.common.blocks.BlockHemp;
import betterwithmods.common.items.ItemDynamite;
import net.minecraftforge.fluids.Fluid;

public final class GlobalConfig {
    public static boolean debug;
    public static int maxPlatformBlocks;

    public static int waterBottleAmount;

    public static boolean blocksAreOpaque;

    public static void initGlobalConfig() {
        String category = "_global";

        ConfigHelper.needsRestart = ConfigHelper.allNeedRestart = true;

        debug = ConfigHelper.loadPropBool("Debug", category, "Enables debug features", false);
        maxPlatformBlocks = ConfigHelper.loadPropInt("Max Platform Blocks", category, "Max blocks a platform can have", 128);

        ConfigHelper.needsRestart = ConfigHelper.allNeedRestart = false;

        blocksAreOpaque = ConfigHelper.loadPropBool("Blocks Are Opaque", category, "Whether BWM blocks that are normally opaque should be opaque. Disable for more flexibility when replacing models.", true);

        BlockHemp.growthChance = ConfigHelper.loadPropDouble("Growth Chance", "Hemp", "Hemp has a 1/X chance of growing where X is this value, the following modifiers divide this value", 15D);
        BlockHemp.fertileModifier = ConfigHelper.loadPropDouble("Fertile Modifier", "Hemp", "Modifies Hemp Growth Chance when planted on Fertile Farmland", 1.33);
        BlockHemp.lampModifier = ConfigHelper.loadPropDouble("Light Block Modifier", "Hemp", "Modifies Hemp Growth Chance when a Light Block is two blocks above the Hemp", 1.5D);
        BlockHemp.neighborModifier = ConfigHelper.loadPropDouble("Neighbor Modifier", "Hemp", "Modifies Hemp Growth Chance for each other crop next to it ", 1.1D);

        ItemDynamite.needsOffhand = ConfigHelper.loadPropBool("Needs Offhand Igniter", "Dynamite", "Must hold flint and steel in the offhand instead of anywhere in your inventory to ignire dynamite.", false);
        ItemDynamite.bloodThrow = ConfigHelper.loadPropBool("Blood Throw", "Dynamite", "Hold ignited dynamite in hand before throwing.", true);
        ItemDynamite.newtonianThrow = ConfigHelper.loadPropBool("Newtonian Throw", "Dynamite", "Thrown dynamite is affected by thrower's velocity. Disable this if another mod provides this feature.", true);
        ItemDynamite.dispenseLit = ConfigHelper.loadPropBool("Light Fuse When Dispensed", "Dynamite", "Dynamite dispensed by dispensers have their fuse lit.", true);

        waterBottleAmount = ConfigHelper.loadPropInt("Water Bottle Fluid amount", category, "The amount of fluid contained in a glass bottle", "", Fluid.BUCKET_VOLUME / 3, 0, Fluid.BUCKET_VOLUME);
    }

    public static void initGlobalClient() {
        GuiStatus.offsetY = ConfigHelper.loadPropInt("Status Effect Offset Y", "gui", "Y Offset for the Hunger, Injury and Gloom Status effects.", 0);
        GuiStatus.offsetX = ConfigHelper.loadPropInt("Status Effect Offset X", "gui", "X Offset for the Hunger, Injury and Gloom Status effects.", 0);
    }
}
