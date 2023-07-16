package betterwithmods.common.blocks;

import betterwithmods.client.BWCreativeTabs;
import betterwithmods.client.BWParticleDigging;
import betterwithmods.client.baking.IStateParticleBakedModel;
import betterwithmods.common.blocks.tile.TileBasic;
import betterwithmods.util.item.ToolsManager;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public abstract class BWMBlock extends Block {
    @SuppressWarnings("deprecation")
    public BWMBlock(Material material) {
        super(material);
        setCreativeTab(BWCreativeTabs.BWTAB);
        if (material == Material.WOOD || material == betterwithmods.common.blocks.mini.BlockMini.MINI) {
            ToolsManager.setAxesAsEffectiveAgainst(this);
            this.setSoundType(SoundType.WOOD);
            this.setHarvestLevel("axe", 0);
        } else if (material == Material.ROCK) {
            this.setSoundType(SoundType.STONE);
            setHarvestLevel("pickaxe", 1);
            ToolsManager.setPickaxesAsEffectiveAgainst(this);
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote && worldIn.getTileEntity(pos) instanceof TileBasic) {
            ((TileBasic) worldIn.getTileEntity(pos)).onBreak();
            worldIn.updateComparatorOutputLevel(pos, this);
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);
        onBlockPlacedBy(world, pos, state, placer, stack, null, 0.5f, 0.5f, 0.5f);
    }

    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack, @Nullable EnumFacing face, float hitX, float hitY, float hitZ) {
        if (hasTileEntity(state)) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileBasic) {
                ((TileBasic) tile).onPlacedBy(placer, face, stack, hitX, hitY, hitZ);
            }
        }
    }


    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        IBlockState state = world.getBlockState(pos);
        IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(state);
        if (model instanceof IStateParticleBakedModel) {
            state = getExtendedState(state.getActualState(world, pos), world, pos);
            TextureAtlasSprite sprite = ((IStateParticleBakedModel) model).getParticleTexture(state, null);
            if (sprite != null) {
                for (int j = 0; j < 4; ++j) {
                    for (int k = 0; k < 4; ++k) {
                        for (int l = 0; l < 4; ++l) {
                            double d0 = ((double) j + 0.5D) / 4.0D;
                            double d1 = ((double) k + 0.5D) / 4.0D;
                            double d2 = ((double) l + 0.5D) / 4.0D;
                            manager.addEffect(new BWParticleDigging(world, (double) pos.getX() + d0, (double) pos.getY() + d1, (double) pos.getZ() + d2, d0 - 0.5D, d1 - 0.5D, d2 - 0.5D, state, pos, sprite, getParticleTintIndex()));
                        }
                    }
                }

                return true;
            }
        }

        return super.addDestroyEffects(world, pos, manager);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager manager) {
        IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(state);
        if (model instanceof IStateParticleBakedModel) {
            BlockPos pos = target.getBlockPos();
            EnumFacing side = target.sideHit;

            state = getExtendedState(state.getActualState(world, pos), world, pos);
            TextureAtlasSprite sprite = ((IStateParticleBakedModel) model).getParticleTexture(state, side);
            if (sprite != null) {
                int i = pos.getX();
                int j = pos.getY();
                int k = pos.getZ();
                AxisAlignedBB axisalignedbb = state.getBoundingBox(world, pos);
                double d0 = (double) i + RANDOM.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minX;
                double d1 = (double) j + RANDOM.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minY;
                double d2 = (double) k + RANDOM.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - 0.20000000298023224D) + 0.10000000149011612D + axisalignedbb.minZ;

                if (side == EnumFacing.DOWN) {
                    d1 = (double) j + axisalignedbb.minY - 0.10000000149011612D;
                }

                if (side == EnumFacing.UP) {
                    d1 = (double) j + axisalignedbb.maxY + 0.10000000149011612D;
                }

                if (side == EnumFacing.NORTH) {
                    d2 = (double) k + axisalignedbb.minZ - 0.10000000149011612D;
                }

                if (side == EnumFacing.SOUTH) {
                    d2 = (double) k + axisalignedbb.maxZ + 0.10000000149011612D;
                }

                if (side == EnumFacing.WEST) {
                    d0 = (double) i + axisalignedbb.minX - 0.10000000149011612D;
                }

                if (side == EnumFacing.EAST) {
                    d0 = (double) i + axisalignedbb.maxX + 0.10000000149011612D;
                }

                Particle particle = new BWParticleDigging(world, d0, d1, d2, 0.0D, 0.0D, 0.0D, state, pos, sprite, getParticleTintIndex())
                        .multiplyVelocity(0.2F)
                        .multipleParticleScaleBy(0.6F);
                manager.addEffect(particle);

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean addLandingEffects(IBlockState state, WorldServer world, BlockPos pos, IBlockState stateAgain, EntityLivingBase entity, int numberOfParticles) {
        //TODO
//        PacketCustomBlockDust packet = new PacketCustomBlockDust(world, pos, entity.posX, entity.posY, entity.posZ, numberOfParticles, 0.15f);
//        CharsetLib.packet.sendToDimension(packet, world.provider.getDimension());
        return super.addLandingEffects(state, world, pos, stateAgain, entity, numberOfParticles);
    }

    @Override
    public boolean addRunningEffects(IBlockState state, World world, BlockPos pos, Entity entity) {
        return super.addRunningEffects(state, world, pos, entity);
//        return UtilProxyCommon.proxy.addRunningParticles(state, world, pos, entity);

    }

    public int getParticleTintIndex() {
        return -1;
    }
}
