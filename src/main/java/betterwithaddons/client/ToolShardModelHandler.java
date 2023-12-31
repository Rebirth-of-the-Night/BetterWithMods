package betterwithaddons.client;

import betterwithaddons.client.models.ModelToolShardInner;
import betterwithaddons.interaction.InteractionBWA;
import betterwithaddons.util.ItemUtil;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ToolShardModelHandler {
    Method getVariantNames;
    public static IModelState STATE;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onModelBake(ModelBakeEvent event)
    {
        if(!InteractionBWA.ARMOR_SHARD_RENDER)
            return;

        for (Item item : Item.REGISTRY) {
            if (!ItemUtil.isTool(item)) continue;
            for (String s : getVariantNames(event.getModelLoader(), item)) {
                ResourceLocation file = getItemLocation(s);
                ModelResourceLocation memory = ModelLoader.getInventoryVariant(s);
                IModel model = null;
                try {
                    model = ModelLoaderRegistry.getModel(file);
                } catch (Exception e) {
                    try {
                        model = ModelLoaderRegistry.getModel(memory);
                    } catch (Exception e1) {
                        //e1.printStackTrace();
                    }
                }
                if(model == null) continue;
                IModel brokenmodel = new ModelToolShardInner(ImmutableList.copyOf(model.getTextures()));
                IBakedModel bakedbrokenmodel = brokenmodel.bake(STATE, DefaultVertexFormats.ITEM, location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
                ToolShardOverrideHandler.addModel(item,bakedbrokenmodel);
            }
        }
    }

    @SuppressWarnings({"deprecation", "unchecked"})
    protected List<String> getVariantNames(ModelLoader loader, Item item)
    {
        if(getVariantNames == null)
            getVariantNames = ReflectionHelper.findMethod(ModelBakery.class, "getVariantNames", "func_177596_a", Item.class);

        try {
            return (List<String>) getVariantNames.invoke(loader,item);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    protected ResourceLocation getItemLocation(String location)
    {
        ResourceLocation resourcelocation = new ResourceLocation(location.replaceAll("#.*", ""));
        return new ResourceLocation(resourcelocation.getNamespace(), "item/" + resourcelocation.getPath());
    }
}
