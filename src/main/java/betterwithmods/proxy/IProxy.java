package betterwithmods.proxy;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.Collection;

public interface IProxy {
    default void preInit(FMLPreInitializationEvent event) {

    }

    default void init(FMLInitializationEvent event) {
    }

    default void postInit(FMLPostInitializationEvent event) {
    }

    default void addResourceOverride(String space, String dir, String file, String ext) {
    }

    default void addResourceOverride(String space, String domain, String dir, String file, String ext) {
    }

    default void syncHarness(int entityId, ItemStack harness) {
    }

    default void syncGloom(String entityId, int gloom) {
    }

    default void syncPlaced(BlockPos[] pos) {
    }

    default void createExplosionParticles(Vec3d center, float size, Collection<BlockPos> affectedPositions) {
    }
}
