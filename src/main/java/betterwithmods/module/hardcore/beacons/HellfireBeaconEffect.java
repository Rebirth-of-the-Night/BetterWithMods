package betterwithmods.module.hardcore.beacons;

import betterwithmods.common.registry.block.recipe.BlockIngredient;
import betterwithmods.module.ConfigHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * Created by michaelepps on 8/7/18.
 */
public class HellfireBeaconEffect extends PotionBeaconEffect {

    @GameRegistry.ObjectHolder("minecraft:fire")
    public static final Block fire = null;

    private static boolean catchFire = true;

    public HellfireBeaconEffect() {
        super("hellfire", new BlockIngredient("blockConcentratedHellfire"), EntityPlayer.class);
        this.addPotionEffect(MobEffects.FIRE_RESISTANCE, 120, PotionBeaconEffect.Amplification.LEVEL);
        this.setBaseBeamColor(Color.ORANGE);
        this.setActivationSound(SoundEvents.ENTITY_BLAZE_SHOOT);
    }

    @Override
    public void setupConfig(String categoryName) {
        super.setupConfig(categoryName);
        catchFire = ConfigHelper.loadPropBool("catchFire", categoryName, "The beacon will catch fire when it applies it effect", true);
    }

    @Override
    public void onBeaconCreate(@Nonnull World world, @Nonnull BlockPos pos, int beaconLevel) {
        BlockPos.MutableBlockPos firePos = new BlockPos.MutableBlockPos();

        if (catchFire) {
            for (int range = 1; range <= beaconLevel; range++) {
                for (int x = -range; x <= range; x++) {
                    for (int z = -range; z <= range; z++) {
                        firePos.setPos(pos.getX() + x, pos.getY() - range + 1, pos.getZ() + z);
                        IBlockState state = world.getBlockState(firePos);
                        if (state.getBlock().isReplaceable(world, firePos)) {
                            world.setBlockState(firePos, fire.getDefaultState());
                        }
                    }
                }
            }
        }

        super.onBeaconCreate(world, pos, beaconLevel);
    }
}
