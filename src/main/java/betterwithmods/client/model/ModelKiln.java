package betterwithmods.client.model;

import betterwithmods.BWMod;
import betterwithmods.client.baking.BakedModelKiln;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by primetoxinz on 6/4/17.
 */
public class ModelKiln implements IModel {

    public static final ModelKiln INSTANCE = new ModelKiln();
    @Override
    public Collection<ResourceLocation> getDependencies() {
        return Collections.emptySet();
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return Collections.emptySet();
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, java.util.function.Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return new BakedModelKiln();
    }

    @Override
    public IModelState getDefaultState() {
        return TRSRTransformation.identity();
    }

    public static class Loader implements ICustomModelLoader {
        @Override
        public boolean accepts(ResourceLocation modelLocation) {
            return modelLocation.getNamespace().equals(BWMod.MODID) && modelLocation.getPath().contains("kiln");
        }

        @Override
        public IModel loadModel(ResourceLocation modelLocation) throws Exception {
            return ModelKiln.INSTANCE;
        }

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {

        }
    }
}
