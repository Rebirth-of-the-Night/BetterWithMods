package betterwithmods.module.hardcore.needs;

import betterwithmods.common.BWMBlocks;
import betterwithmods.common.blocks.BlockDirtSlab;
import betterwithmods.module.Feature;
import betterwithmods.util.player.PlayerHelper;
import com.google.common.collect.Maps;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.UUID;

public class HCMovement extends Feature {
    public final static UUID HCMOVEMENT_SPEED_UUID = UUID.fromString("aece6a05-d163-4871-aaf3-ebab43b0fcfa");

    public static final HashMap<Material, Float> MATERIAL_MOVEMENT = Maps.newHashMap();
    public static final HashMap<IBlockState, Float> BLOCK_OVERRIDE_MOVEMENT = Maps.newHashMap();
    public static final float DEFAULT_SPEED = 0.75f;
    public static final float FAST = 1.2f;
    public static boolean dirtpathQuality;

    @Override
    public void setupConfig() {
        dirtpathQuality = loadPropBool("Dirt Paths Require Quality Shovel", "Dirt Paths require a shovel greater than stone to be created", true);
    }

    @Override
    public String getFeatureDescription() {
        return "Change walking speed depending on the block";
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        MATERIAL_MOVEMENT.put(Material.ROCK, FAST);
        MATERIAL_MOVEMENT.put(Material.WOOD, FAST);
        MATERIAL_MOVEMENT.put(Material.IRON, FAST);
        MATERIAL_MOVEMENT.put(Material.CLOTH, FAST);
        MATERIAL_MOVEMENT.put(Material.CARPET, FAST);
        MATERIAL_MOVEMENT.put(Material.CIRCUITS, FAST);

        MATERIAL_MOVEMENT.put(Material.GRASS, 1.0f);
        MATERIAL_MOVEMENT.put(Material.GLASS, 1.0f);
        MATERIAL_MOVEMENT.put(Material.GROUND, 1.0f);
        MATERIAL_MOVEMENT.put(Material.CLAY, 1.0f);

        MATERIAL_MOVEMENT.put(Material.SAND, 0.75f);
        MATERIAL_MOVEMENT.put(Material.SNOW, 0.75f);
        MATERIAL_MOVEMENT.put(Material.LEAVES, 0.70f);
        MATERIAL_MOVEMENT.put(Material.PLANTS, 0.70f);
        MATERIAL_MOVEMENT.put(Material.VINE, 0.70f);

        BLOCK_OVERRIDE_MOVEMENT.put(Blocks.SOUL_SAND.getDefaultState(), 0.70f);
        BLOCK_OVERRIDE_MOVEMENT.put(Blocks.GRAVEL.getDefaultState(), FAST);
        BLOCK_OVERRIDE_MOVEMENT.put(Blocks.GRASS_PATH.getDefaultState(), FAST);
        BLOCK_OVERRIDE_MOVEMENT.put(BWMBlocks.DIRT_SLAB.getDefaultState().withProperty(BlockDirtSlab.VARIANT, BlockDirtSlab.DirtSlabType.PATH), FAST);
    }

    public static HashMap<UUID, Float> PREVIOUS_SPEED = Maps.newHashMap();

    @SubscribeEvent
    public void onWalk(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            EntityPlayer player = event.player;
            if(player.isRiding())
                return;
            float speed = 0;

            if (player.onGround) {
                BlockPos blockpos = new BlockPos(MathHelper.floor(player.posX), MathHelper.floor(player.posY - 0.2D), MathHelper.floor(player.posZ));
                IBlockState state = player.world.getBlockState(blockpos);

                if (BLOCK_OVERRIDE_MOVEMENT.containsKey(state)) {
                    speed = BLOCK_OVERRIDE_MOVEMENT.get(state);
                } else {
                    speed = MATERIAL_MOVEMENT.getOrDefault(state.getMaterial(), DEFAULT_SPEED);
                }
                if (!player.world.getBlockState(player.getPosition()).getMaterial().isSolid()) {
                    state = player.world.getBlockState(player.getPosition());
                    if (BLOCK_OVERRIDE_MOVEMENT.containsKey(state)) {
                        speed = BLOCK_OVERRIDE_MOVEMENT.get(state);
                    } else if (MATERIAL_MOVEMENT.containsKey(state.getMaterial())) {
                        speed *= MATERIAL_MOVEMENT.get(state.getMaterial());
                    }
                }
                PREVIOUS_SPEED.put(player.getGameProfile().getId(), speed);
            }
            if (speed == 0)
                speed = PREVIOUS_SPEED.getOrDefault(player.getGameProfile().getId(), DEFAULT_SPEED);
            PlayerHelper.changeSpeed(player, "HCMovement", speed, HCMOVEMENT_SPEED_UUID);
        }
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }


    //Should cancel out the FOV change from HCMovement entirely
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onFOV(FOVUpdateEvent event) {
        EntityPlayer player = event.getEntity();
        float f = 1.0F;

        if (player.capabilities.isFlying) {
            f *= 1.1F;
        }

        IAttributeInstance iattributeinstance = player.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);

        double value = iattributeinstance.getAttributeValue();
        AttributeModifier mod = iattributeinstance.getModifier(HCMovement.HCMOVEMENT_SPEED_UUID);
        if (mod != null)
            value /= (1 + mod.getAmount());
        f = (float) ((double) f * ((value / (double) player.capabilities.getWalkSpeed() + 1.0D) / 2.0D));

        if (player.capabilities.getWalkSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f)) {
            f = 1.0F;
        }
        if (player.isHandActive() && player.getActiveItemStack().getItem() == Items.BOW) {
            int i = player.getItemInUseMaxCount();
            float f1 = (float) i / 20.0F;

            if (f1 > 1.0F) {
                f1 = 1.0F;
            } else {
                f1 = f1 * f1;
            }

            f *= 1.0F - f1 * 0.15F;
        }

        event.setNewfov(f);
    }
}
