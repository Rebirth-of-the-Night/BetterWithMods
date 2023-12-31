package betterwithaddons.tileentity;

import betterwithaddons.lib.Reference;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModTileEntities
{
    public static void register()
    {
        registerTE(TileEntityBannerDetector.class, "bannerDetector");
        registerTE(TileEntityWorldScaleActive.class, "worldScaleActive");
        registerTE(TileEntityAlchDragon.class, "alchDragon");
        registerTE(TileEntityNettedScreen.class, "nettedScreen");
        registerTE(TileEntityTatara.class, "tatara");
        registerTE(TileEntitySoakingBox.class, "soakingbox");
        registerTE(TileEntityDryingBox.class, "dryingbox");
        registerTE(TileEntityLureTree.class, "luretree");
        registerTE(TileEntityChute.class, "chute");
        registerTE(TileEntityAqueductWater.class, "aqueductWater");
        registerTE(TileEntityLegendarium.class, "legendarium");
        registerTE(TileEntityAncestrySand.class, "ancestrySand");
        registerTE(TileEntityInfuser.class, "infuser");
        registerTE(TileEntityLoom.class, "loom");
        registerTE(TileEntityRopeSideways.class, "rope_sideways");
        registerTE(TileEntityRopePost.class, "rope_post");
        registerTE(TileEntityInvertedGearbox.class, "inverted_gearbox");
        registerTE(TileEntityTea.class, "tea");
        registerTE(TileEntityNabe.class, "nabe");
    }

    private static void registerTE(Class<? extends TileEntity> clazz, String name)
    {
        GameRegistry.registerTileEntity(clazz, new ResourceLocation(Reference.MOD_ID, name));
    }
}
