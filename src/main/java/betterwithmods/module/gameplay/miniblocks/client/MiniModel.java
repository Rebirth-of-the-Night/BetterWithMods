package betterwithmods.module.gameplay.miniblocks.client;

import betterwithmods.client.baking.ModelFactory;
import betterwithmods.client.baking.WrappedBakedModel;
import betterwithmods.client.model.render.RenderUtils;
import betterwithmods.module.gameplay.miniblocks.blocks.BlockMini;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.TRSRTransformation;

public class MiniModel extends ModelFactory<MiniCacheInfo> {

    public static MiniModel SIDING, MOULDING, CORNER;

    public IModel template;

    public MiniModel(IModel template) {
        super(BlockMini.MINI_INFO, TextureMap.LOCATION_MISSING_TEXTURE);
        this.template = template;
    }

    @Override
    public IBakedModel bake(MiniCacheInfo object, boolean isItem, BlockRenderLayer layer) {
        ImmutableMap.Builder<String, String> textures = new ImmutableMap.Builder<>();
        for (EnumFacing facing : EnumFacing.VALUES) {
            textures.put(facing.getName2(), RenderUtils.getTextureFromFace(object.getState(), facing).getIconName());
        }
        TRSRTransformation state = object.getOrientation().toTransformation();
        IModel retexture = template.retexture(textures.build()).uvlock(true);
        return new WrappedBakedModel(retexture.bake(state, DefaultVertexFormats.BLOCK, RenderUtils.textureGetter), RenderUtils.getParticleTexture(object.getState())).addDefaultBlockTransforms();
    }


    @Override
    public MiniCacheInfo fromItemStack(ItemStack stack) {
        return MiniCacheInfo.from(stack);
    }
}
