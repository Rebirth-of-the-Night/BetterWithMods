package betterwithaddons;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import betterwithaddons.block.*;
import betterwithaddons.client.ToolShardModelHandler;
import betterwithaddons.client.fx.FXFireBlast;
import betterwithaddons.client.fx.FXFireExplosion;
import betterwithaddons.client.fx.FXLeafParticle;
import betterwithaddons.client.fx.FXLightning;
import betterwithaddons.client.models.ItemModels;
import betterwithaddons.client.render.*;
import betterwithaddons.entity.*;
import betterwithaddons.interaction.ModInteractions;
import betterwithaddons.item.ModItems;
import betterwithaddons.lib.Reference;
import betterwithaddons.tileentity.TileEntityAlchDragon;
import betterwithaddons.tileentity.TileEntityInfuser;
import betterwithaddons.tileentity.TileEntityNabe;
import betterwithaddons.util.ResourceProxy;

public class ClientProxy implements IProxy {
    public static ModelResourceLocation aqueductWaterLocation = new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID, "aqueduct_water"), "normal");
    public static ModelResourceLocation ropeBridgeLocation = new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID, "rope_bridge"), "normal");
    public static ModelResourceLocation ropePostLocation = new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID, "rope_post_knot"), "normal");

    static ResourceProxy resourceProxy;

    // private static Pattern varPattern = Pattern.compile("var:([^\\)]+)");

    static {
        resourceProxy = new ResourceProxy();
    }

    @SubscribeEvent
    public void textureStitch(TextureStitchEvent.Pre event) {
        event.getMap().registerSprite(new ResourceLocation(Reference.MOD_ID, "items/breakmask"));
        event.getMap().registerSprite(new ResourceLocation(Reference.MOD_ID, "blocks/nabe_liquid"));
    }

    @Override
    public void preInit() {
        MinecraftForge.EVENT_BUS.register(new ToolShardModelHandler());
        MinecraftForge.EVENT_BUS.register(this);
        ModInteractions.preInitClient();
    }

    @Override
    public void init() {
        registerColorable(ModBlocks.ECKSIE_SAPLING);
        registerColorable(ModBlocks.GRASS);
        registerColorable(ModBlocks.PCB_WIRE);
        registerColorable(ModItems.SAMURAI_BOOTS);
        registerColorable(ModItems.SAMURAI_CHESTPLATE);
        registerColorable(ModItems.SAMURAI_LEGGINGS);
        registerColorable(ModItems.BROKEN_ARTIFACT);
        registerColorable(ModBlocks.TEA);
        registerColorable(ModItems.TEA_LEAVES);
        registerColorable(ModItems.TEA_SOAKED);
        registerColorable(ModItems.TEA_WILTED);
        registerColorable(ModItems.TEA_POWDER);
        registerColorable(ModItems.TEA_CUP);
    }

    @Override
    public void postInit() {

    }

    @Override
    public void makeLeafFX(double x, double y, double z, float r, float g, float b, float size, float motionx, float motiony, float motionz, float maxAgeMul) {
        FXLeafParticle leaf = new FXLeafParticle(Minecraft.getMinecraft().world, x, y, z, size, r, g, b, true, maxAgeMul);
        leaf.setSpeed(motionx, motiony, motionz);
        Minecraft.getMinecraft().effectRenderer.addEffect(leaf);
    }

    @Override
    public void makeLightningFX(double x, double y, double z, float r, float g, float b, float size, float maxAgeMul) {
        FXLightning spark = new FXLightning(Minecraft.getMinecraft().world, x, y, z, size, r, g, b, true, maxAgeMul);
        Minecraft.getMinecraft().effectRenderer.addEffect(spark);
    }

    @Override
    public void makeFireBlastFX(World world, double x, double y, double z, float size, int time) {
        FXFireBlast blast = new FXFireBlast(world, x, y, z, size, time);
        Minecraft.getMinecraft().effectRenderer.addEffect(blast);
    }

    @Override
    public void makeFireExplosionFX(World world, double x, double y, double z, float size, float radius, int time, int count) {
        FXFireExplosion explosion = new FXFireExplosion(world,x,y,z,size,radius,time,count);
        Minecraft.getMinecraft().effectRenderer.addEffect(explosion);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void registerResourcePack() {
        List<IResourcePack> packs = ObfuscationReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "aD", "field_110449_ao", "defaultResourcePacks");
        packs.add(resourceProxy);
    }

    @Override
    public void addResourceOverride(String space, String dir, String file, String ext) {
        resourceProxy.addResource(space, dir, file, ext);
    }

    @Override
    public void addResourceOverride(String modid, String space, String dir, String file, String ext) {
        resourceProxy.addResource(space, modid, dir, file, ext);
    }



    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event)
    {
        ItemModels.register();

        ModelResourceLocation toolShardLocation = new ModelResourceLocation(ModItems.BROKEN_ARTIFACT.getRegistryName(), "inventory");
        ModelLoader.setCustomMeshDefinition(ModItems.BROKEN_ARTIFACT, stack -> toolShardLocation);
        ModelBakery.registerItemVariants(ModItems.BROKEN_ARTIFACT, toolShardLocation);

        ModelLoader.setCustomStateMapper(ModBlocks.THORNS,new StateMap.Builder().ignore(BlockThorns.FACING).build());
        ModelLoader.setCustomStateMapper(ModBlocks.ROPE_SIDEWAYS, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                if(state.getValue(BlockRopeSideways.HAS_PLANKS))
                    return ropeBridgeLocation;

                Map <IProperty<?>,Comparable<?>> map = Maps.newLinkedHashMap(state.getProperties());
                map.remove(BlockRopeSideways.HAS_PLANKS);

                return new ModelResourceLocation(Block.REGISTRY.getNameForObject(state.getBlock()),this.getPropertyString(map));
            }
        });
        ModelLoader.setCustomStateMapper(ModBlocks.ROPE_POST, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                if(state.getValue(BlockRopePost.HAS_POST))
                    return ropePostLocation;

                Map <IProperty<?>,Comparable<?>> map = Maps.newLinkedHashMap(state.getProperties());
                map.remove(BlockRopePost.HAS_PLANKS);
                map.remove(BlockRopePost.HAS_POST);

                return new ModelResourceLocation(Block.REGISTRY.getNameForObject(state.getBlock()),this.getPropertyString(map));
            }
        });
        ModelLoader.setCustomStateMapper(ModBlocks.AQUEDUCT_WATER, new StateMapperBase() {
            @Override
            protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                return aqueductWaterLocation;
            }
        });

        RenderingRegistry.registerEntityRenderingHandler(EntityGreatarrow.class, RenderGreatarrow.GREATARROW_RENDER);
        RenderingRegistry.registerEntityRenderingHandler(EntityYa.class, RenderYa.YA_RENDER);
        RenderingRegistry.registerEntityRenderingHandler(EntitySpirit.class, manager -> new RenderSpirit(manager));
        RenderingRegistry.registerEntityRenderingHandler(EntityAncestryBottle.class, manager -> new RenderSnowball<>(manager,ModItems.ANCESTRY_BOTTLE,Minecraft.getMinecraft().getRenderItem()));
        RenderingRegistry.registerEntityRenderingHandler(EntityArtifactFrame.class, RenderArtifactFrame.ARTIFACEFRAME_RENDER);
        RenderingRegistry.registerEntityRenderingHandler(EntityKarateZombie.class, RenderKarateZombie::new);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAlchDragon.class, new RenderAlchDragon());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInfuser.class, new RenderInfuser());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityNabe.class, new RenderNabe());
    }

    private void registerColorable(IColorable colorable)
    {
        if(colorable instanceof Item && colorable.getItemColor() != null)
        {
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(colorable.getItemColor(), (Item)colorable );
        }
        else
        {
            Block block = (Block) colorable;
            if (colorable.getBlockColor() != null && block != null)
                Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(colorable.getBlockColor(), block);
            if (colorable.getItemColor() != null)
                Minecraft.getMinecraft().getItemColors().registerItemColorHandler(colorable.getItemColor(), block);
        }
    }
}
