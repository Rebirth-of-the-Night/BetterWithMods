package betterwithmods.module.tweaks;

import betterwithmods.BWMod;
import betterwithmods.client.render.RenderLongboi;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWRegistry;
import betterwithmods.common.blocks.BlockWolf;
import betterwithmods.common.entity.EntityLongboi;
import betterwithmods.common.registry.block.recipe.BlockIngredient;
import betterwithmods.common.registry.block.recipe.TurntableRecipe;
import betterwithmods.module.Feature;
import betterwithmods.util.EntityUtils;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Optional;

public class LongBoi extends Feature {


    public static Block LONG_FRIEND = new BlockWolf(new ResourceLocation(BWMod.MODID, "longboi")).setRegistryName("long_friend");

    private static Optional<EntityLivingBase> getEntity(World world, BlockPos pos) {
        return world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos, pos.add(1, 1, 1)), Entity::isEntityAlive).stream().findFirst();
    }

    @Override
    public String getFeatureDescription() {
        return "Long Bois!";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void preInitClient(FMLPreInitializationEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(EntityLongboi.class, RenderLongboi::new);
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        BWMBlocks.registerBlock(LONG_FRIEND);
        BWRegistry.registerEntity(EntityLongboi.class, "longboi", 64, 1, true, 0xe4d3d0, 0xfd742b);
        BWRegistry.TURNTABLE.addRecipe(new LongRecipe());
    }

    private static class EntityIngredient extends BlockIngredient {

        private ResourceLocation name;

        public EntityIngredient(ResourceLocation name) {
            this.name = name;
        }

        @Override
        public boolean apply(World world, BlockPos pos, @Nullable IBlockState state) {
            Optional<EntityLivingBase> entity = getEntity(world, pos);
            return entity.filter(entity1 -> EntityList.isMatchingName(entity1, name)).isPresent();
        }

    }

    private static class LongRecipe extends TurntableRecipe {

        public LongRecipe() {
            super(new EntityIngredient(new ResourceLocation("wolf")), Lists.newArrayList(), Blocks.AIR.getDefaultState(), 8);
        }



        @Override
        public NonNullList<ItemStack> onCraft(World world, BlockPos pos) {

            Optional<EntityLivingBase> entity = getEntity(world, pos);
            if (entity.isPresent()) {
                EntityLongboi longboi = new EntityLongboi(world);
                EntityUtils.copyEntityInfo(entity.get(), longboi);
                world.spawnEntity(longboi);
                world.playSound(null, pos, SoundEvents.ENTITY_WOLF_HURT, SoundCategory.NEUTRAL, 0.75F, 1.0F);
                entity.get().setDead();
            }

            return super.onCraft(world, pos);
        }

        @Override
        public boolean isInvalid() {
            return false;
        }

        @Override
        public boolean isHidden() {
            return true;
        }
    }

}
