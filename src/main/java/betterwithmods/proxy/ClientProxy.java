package betterwithmods.proxy;

import betterwithmods.BWMod;
import betterwithmods.api.client.IColorable;
import betterwithmods.client.*;
import betterwithmods.client.model.ModelKiln;
import betterwithmods.client.model.render.RenderUtils;
import betterwithmods.client.render.*;
import betterwithmods.client.tesr.*;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWMItems;
import betterwithmods.common.blocks.mechanical.tile.*;
import betterwithmods.common.blocks.tile.TileEntityBeacon;
import betterwithmods.common.blocks.tile.TileEntityBucket;
import betterwithmods.common.entity.*;
import betterwithmods.manual.api.ManualAPI;
import betterwithmods.manual.api.prefab.manual.TextureTabIconRenderer;
import betterwithmods.manual.common.DirectoryDefaultProvider;
import betterwithmods.manual.common.api.ManualDefinitionImpl;
import betterwithmods.manual.custom.StatePathProvider;
import betterwithmods.module.ModuleLoader;
import betterwithmods.module.gameplay.breeding_harness.BreedingHarness;
import betterwithmods.module.gameplay.breeding_harness.CapabilityHarness;
import betterwithmods.module.hardcore.crafting.HCFurnace;
import betterwithmods.module.hardcore.creatures.EntityTentacle;
import betterwithmods.module.hardcore.needs.HCGloom;
import betterwithmods.module.hardcore.world.stumping.HCStumping;
import betterwithmods.module.hardcore.world.stumping.PlacedCapability;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleLava;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = BWMod.MODID, value = Side.CLIENT)

public class ClientProxy implements IProxy {

    private static ResourceProxy resourceProxy;

    static {
        List<IResourcePack> packs = Minecraft.getMinecraft().defaultResourcePacks;
        resourceProxy = new ResourceProxy();
        packs.add(resourceProxy);
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        BWMItems.getItems().forEach(BWMItems::setInventoryModel);
        ModelLoader.setCustomStateMapper(BWMBlocks.STOKED_FLAME, new BWStateMapper(BWMBlocks.STOKED_FLAME.getRegistryName().toString()));
        ModelLoader.setCustomStateMapper(BWMBlocks.WINDMILL, new BWStateMapper(BWMBlocks.WINDMILL.getRegistryName().toString()));
        ModelLoader.setCustomStateMapper(BWMBlocks.WATERWHEEL, new BWStateMapper(BWMBlocks.WATERWHEEL.getRegistryName().toString()));
        ModelLoaderRegistry.registerLoader(new ModelKiln.Loader());
        ModuleLoader.registerModels(event);
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        ModuleLoader.preInitClient(event);
        registerRenderInformation();
        initRenderers();
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
    }

    @Override
    public void init(FMLInitializationEvent event) {
        ModuleLoader.initClient(event);
        registerColors();

        ManualAPI.addProvider(new DirectoryDefaultProvider(new ResourceLocation(BWMod.MODID, "documentation/docs/")));
        ManualDefinitionImpl.INSTANCE.addDefaultProviders();
        ManualAPI.addProvider(new StatePathProvider());
        ManualAPI.addTab(new TextureTabIconRenderer(new ResourceLocation(BWMod.MODID, "textures/gui/manual_home.png")), "bwm.manual.home", "%LANGUAGE%/index.md");

    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        ModuleLoader.postInitClient(event);
        RenderUtils.registerFilters();
    }

    private void registerRenderInformation() {

        OBJLoader.INSTANCE.addDomain(BWMod.MODID);
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWindmillHorizontal.class, new TESRWindmill());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWindmillVertical.class, new TESRVerticalWindmill());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityWaterwheel.class, new TESRWaterwheel());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFilteredHopper.class, new TESRFilteredHopper());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTurntable.class, new TESRTurntable());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCauldron.class, new TESRCookingPot());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCrucible.class, new TESRCookingPot());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBeacon.class, new TESRBeacon());
        ClientRegistry.bindTileEntitySpecialRenderer(TileSteelSaw.class, new TESRSteelSaw());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBucket.class, new TESRBucket());
        if (ModuleLoader.isFeatureEnabled(HCFurnace.class)) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFurnace.class, new TESRFurnaceContent());
        }
    }

    private void registerColor(ItemColors registry, Item item) {
        if(item instanceof IColorable) {
            registry.registerItemColorHandler(((IColorable) item).getColorHandler(), item);
        }
    }

    @SuppressWarnings("deprecation")
    private void registerColors() {
        final BlockColors col = Minecraft.getMinecraft().getBlockColors();
        col.registerBlockColorHandler(ColorHandlers.BlockPlanterColor, BWMBlocks.PLANTER);
        col.registerBlockColorHandler(ColorHandlers.BlockFoliageColor, BWMBlocks.VINE_TRAP);
        col.registerBlockColorHandler(ColorHandlers.BlockBloodLeafColor, BWMBlocks.BLOOD_LEAVES);
        final ItemColors itCol = Minecraft.getMinecraft().getItemColors();
        itCol.registerItemColorHandler(ColorHandlers.ItemPlanterColor, BWMBlocks.PLANTER);
        itCol.registerItemColorHandler(ColorHandlers.ItemFoliageColor, BWMBlocks.VINE_TRAP);
        itCol.registerItemColorHandler(ColorHandlers.ItemBloodLeafColor, BWMBlocks.BLOOD_LEAVES);
        BWMItems.getItems().forEach( item -> registerColor(itCol, item));
        col.registerBlockColorHandler((state, worldIn, pos, tintIndex) -> worldIn != null && pos != null ? BiomeColorHelper.getGrassColorAtPos(worldIn, pos) : ColorizerGrass.getGrassColor(0.5D, 1.0D), BWMBlocks.DIRT_SLAB);
        itCol.registerItemColorHandler((stack, tintIndex) -> {
            IBlockState iblockstate = ((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata());
            return col.colorMultiplier(iblockstate, null, null, tintIndex);
        }, BWMBlocks.DIRT_SLAB);
    }

    private void initRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityDynamite.class, manager -> new RenderDynamite(manager, Minecraft.getMinecraft().getRenderItem()));
        RenderingRegistry.registerEntityRenderingHandler(EntityUrn.class, RenderUrn::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityMiningCharge.class, RenderMiningCharge::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityExtendingRope.class, RenderExtendingRope::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityShearedCreeper.class, RenderShearedCreeper::new);


//        RenderingRegistry.registerEntityRenderingHandler(EntityCow.class, RenderCowHarness::new);
//        RenderingRegistry.registerEntityRenderingHandler(EntityPig.class, RenderPigHarness::new);
//        RenderingRegistry.registerEntityRenderingHandler(EntitySheep.class, RenderSheepHarness::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityBroadheadArrow.class, RenderBroadheadArrow::new);
        RenderingRegistry.registerEntityRenderingHandler(EntitySpiderWeb.class, manager -> new RenderSnowball<>(manager, Item.getItemFromBlock(Blocks.WEB), Minecraft.getMinecraft().getRenderItem()));
        RenderingRegistry.registerEntityRenderingHandler(EntityJungleSpider.class, RenderJungleSpider::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityTentacle.class, RenderTentacle::new);
    }



    @Override
    public void addResourceOverride(String space, String dir, String file, String ext) {
        resourceProxy.addResource(space, dir, file, ext);
    }

    @Override
    public void addResourceOverride(String space, String domain, String dir, String file, String ext) {
        resourceProxy.addResource(space, domain, dir, file, ext);
    }

    @Override
    public void syncHarness(int entityId, ItemStack harness) {
        Entity entity = getEntityByID(entityId);
        if (entity != null) {
            CapabilityHarness cap = BreedingHarness.getCapability(entity);
            if (cap != null) {
                cap.setHarness(harness);
            }
        }
    }

    private Entity getEntityByID(int id) {
        World world = Minecraft.getMinecraft().world;
        if (world == null)
            return null;
        return world.getEntityByID(id);
    }

    private EntityPlayer getPlayerById(String id) {
        World world = Minecraft.getMinecraft().world;
        if (world == null)
            return null;
        return world.getPlayerEntityByUUID(UUID.fromString(id));
    }

    @Override
    public void syncGloom(String entityId, int gloom) {
        EntityPlayer e = getPlayerById(entityId);
        if (e != null) {
            HCGloom.Gloom g = HCGloom.getGloom(e);
            if (g != null) {
                g.setGloom(gloom);
            }
        }
    }

    @Override
    public void syncPlaced(BlockPos[] pos) {
        World world = Minecraft.getMinecraft().world;
        if (world == null)
            return;
        PlacedCapability capability = HCStumping.getCapability(world);
        if (capability != null) {
            capability.addAll(pos);
        }
    }

    @Override
    public void createExplosionParticles(Vec3d center, float size, Collection<BlockPos> affectedPositions) {
        World world = Minecraft.getMinecraft().world;
        ParticleManager particleManager = Minecraft.getMinecraft().effectRenderer;
        BlockPos blockpos = new BlockPos(center);
        double x = center.x;
        double y = center.y;
        double z = center.z;
        for (BlockPos pos : affectedPositions) {
            double xRandom = blockpos.getX() + world.rand.nextDouble();
            double yRandom = blockpos.getY() + world.rand.nextDouble();
            double zRandom = blockpos.getZ() + world.rand.nextDouble();
            double dx = xRandom - x;
            double dy = yRandom - y;
            double dz = zRandom - z;
            double distance = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
            dx /= distance;
            dy /= distance;
            dz /= distance;
            double velocity = 0.5D / (distance / size + 0.1D);
            velocity *= (world.rand.nextDouble() * world.rand.nextDouble() + 0.3);
            dx *= velocity;
            dy *= velocity;
            dz *= velocity;
            IBlockState state = world.getBlockState(pos);

            if(state.getMaterial() == Material.WATER) { //Water shockwave to show the range of the dynamite
                particleManager.addEffect(new ParticleBubbleFast(world,(xRandom + x) / 2.0D, (yRandom + y) / 2.0D, (zRandom + z) / 2.0D, dx, dy, dz));
                particleManager.addEffect(new ParticleBubbleFast(world, xRandom, yRandom, zRandom, dx, dy, dz));
                world.spawnParticle(EnumParticleTypes.WATER_BUBBLE,pos.getX() + world.rand.nextDouble(), pos.getY() + world.rand.nextDouble(), pos.getZ() + world.rand.nextDouble(), dx, dy, dz);
            } else {
                world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, (xRandom + x) / 2.0D, (yRandom + y) / 2.0D, (zRandom + z) / 2.0D, dx, dy, dz);
                world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, xRandom, yRandom, zRandom, dx, dy, dz);
            }

            if(state.getMaterial() == Material.LAVA) {
                world.spawnParticle(EnumParticleTypes.LAVA,pos.getX() + world.rand.nextDouble(),pos.getY() + world.rand.nextDouble(), pos.getZ() + world.rand.nextDouble(),0,0,0);
            }
        }
    }

    public static class FluidStateMapper extends StateMapperBase implements ItemMeshDefinition {

        public final Fluid fluid;
        public final ModelResourceLocation location;

        public FluidStateMapper(Fluid fluid) {
            this.fluid = fluid;
            // have each block hold its fluid per nbt? hm
            this.location = new ModelResourceLocation(new ResourceLocation("betterwithmods", "fluid_block"), fluid.getName());
        }

        @Nonnull
        @Override
        protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
            return location;
        }

        @Nonnull
        @Override
        public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack) {
            return location;
        }
    }
}
