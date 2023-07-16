package betterwithmods.client.baking;

import betterwithmods.common.BWMBlocks;
import betterwithmods.common.blocks.BlockKiln;
import betterwithmods.common.blocks.tile.TileKiln;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.property.IExtendedBlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by primetoxinz on 6/4/17.
 */

public class BakedModelKiln implements IBakedModel {
    @SuppressWarnings("deprecation")
    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if(state.getBlock() != BWMBlocks.KILN)
            return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel().getQuads(state, side, rand);

        BlockRenderLayer layer = MinecraftForgeClient.getRenderLayer();
        IBlockState heldState = ((IExtendedBlockState) state).getValue(BlockKiln.HELD_STATE);
        IBlockAccess heldWorld = ((IExtendedBlockState) state).getValue(BlockKiln.HELD_WORLD);
        BlockPos heldPos = ((IExtendedBlockState) state).getValue(BlockKiln.HELD_POS);

        if (heldWorld == null || heldPos == null) {
            return ImmutableList.of();
        }

        Minecraft mc = Minecraft.getMinecraft();
        if(heldState == null && layer == BlockRenderLayer.SOLID) {
            // No camo
            ModelResourceLocation path = new ModelResourceLocation("minecraft:brick_block");
            return mc.getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getModel(path).getQuads(state, side, rand);
        } else if(heldState != null) {
            if(heldState.getBlock().canRenderInLayer(heldState, layer)) {
                IBlockState actual = heldState.getBlock().getActualState(heldState, new FakeBlockAccess(heldWorld), heldPos);

                // Steal camo's model
                IBakedModel model = mc.getBlockRendererDispatcher().getBlockModelShapes().getModelForState(actual);

                // Their model can be smart too
                IBlockState extended = heldState.getBlock().getExtendedState(actual, new FakeBlockAccess(heldWorld), heldPos);
                return model.getQuads(extended, side, rand);
            }
        }

        return ImmutableList.of(); // Nothing renders
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/brick");
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }

    private static class FakeBlockAccess implements IBlockAccess {

        private final IBlockAccess compose;

        private FakeBlockAccess(IBlockAccess compose) {
            this.compose = compose;
        }

        @Override
        public TileEntity getTileEntity(@Nonnull BlockPos pos) {
            return compose.getTileEntity(pos);
        }

        @Override
        public int getCombinedLight(@Nonnull BlockPos pos, int lightValue) {
            return 15 << 20 | 15 << 4;
        }

        @Nonnull
        @Override
        public IBlockState getBlockState(@Nonnull BlockPos pos) {
            IBlockState state = compose.getBlockState(pos);
            if(state.getBlock() instanceof BlockKiln) {
                state = ((TileKiln) compose.getTileEntity(pos)).camoState;
            }
            return state == null ? Blocks.AIR.getDefaultState() : state;
        }

        @Override
        public boolean isAirBlock(@Nonnull BlockPos pos) {
            return compose.isAirBlock(pos);
        }

        @Nonnull
        @Override
        public Biome getBiome(@Nonnull BlockPos pos) {
            return compose.getBiome(pos);
        }

        @Override
        public int getStrongPower(@Nonnull BlockPos pos, @Nonnull EnumFacing direction) {
            return compose.getStrongPower(pos, direction);
        }

        @Nonnull
        @Override
        public WorldType getWorldType() {
            return compose.getWorldType();
        }

        @Override
        public boolean isSideSolid(@Nonnull BlockPos pos, @Nonnull EnumFacing side, boolean _default) {
            return compose.isSideSolid(pos, side, _default);
        }
    }
}
