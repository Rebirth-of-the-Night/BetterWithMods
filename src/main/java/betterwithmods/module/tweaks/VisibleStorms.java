package betterwithmods.module.tweaks;

import betterwithmods.module.Feature;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.Random;

public class VisibleStorms extends Feature {
    public static boolean DUST_STORMS;
    public static boolean SAND_STORMS;
    public static int DUST_PARTICLES;
    public static int AIR_PARTICLES;

    @Override
    public void setupConfig() {
        DUST_STORMS = loadPropBool("Dust Storms","Storms are clearly visible in dry biomes.",true);
        SAND_STORMS = loadPropBool("Sand Storms","Adds a fog change during storms in deserts.",true);
        DUST_PARTICLES = loadPropInt("Dust Particle Count","How many dust particles should be created, too many may contribute to lag.",2);
        AIR_PARTICLES = loadPropInt("Air Particle Count","How many air particles should be created, too many may contribute to lag.",3);
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

    float currentRed, currentGreen, currentBlue;
    float currentDistance, currentDistanceScale;
    float desiredRed, desiredGreen, desiredBlue;
    float desiredDistance, desiredDistanceScale;

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent tickEvent) {
        if (tickEvent.phase == TickEvent.Phase.START)
            return;

        EntityPlayer entity = tickEvent.player;
        if (entity == null)
            return;
        World world = entity.world;
        if (world == null || !world.isRemote)
            return;
        if (DUST_STORMS) {
            ParticleManager particleManager = Minecraft.getMinecraft().effectRenderer;

            Random random = world.rand;
            BlockPos pos = entity.getPosition();
            int radius = 16; //blocks
            for (int i = 0; i < DUST_PARTICLES; i++) {
                BlockPos posGround = pos.add(random.nextInt(radius * 2 + 1) - radius, random.nextInt(radius * 2 + 1) - radius, random.nextInt(radius * 2 + 1) - radius);
                if (!shouldStorm(world, posGround))
                    continue;
                posGround = world.getHeight(posGround).down(); //Constant access whaaaat???

                IBlockState stateGround = world.getBlockState(posGround);
                @SuppressWarnings("unused")
                Particle particleGround = particleManager.spawnEffectParticle(EnumParticleTypes.BLOCK_DUST.getParticleID(), posGround.getX() + random.nextDouble(), posGround.getY() + 1.2, posGround.getZ() + random.nextDouble(), -0.5 - random.nextDouble() * 0.6, 0.0, 0.0, Block.getStateId(stateGround));
            }

            for (int i = 0; i < AIR_PARTICLES; i++) {
                BlockPos posAir = pos.add(random.nextInt(radius * 2 + 1) - radius, random.nextInt(radius * 2 + 1) - radius, random.nextInt(radius * 2 + 1) - radius);
                if (world.canSeeSky(posAir) && shouldStorm(world, posAir)) {
                    Particle particleAir = particleManager.spawnEffectParticle(EnumParticleTypes.SMOKE_NORMAL.getParticleID(), posAir.getX() + random.nextDouble(), posAir.getY() + random.nextDouble(), posAir.getZ() + random.nextDouble(), -0.5 - random.nextDouble() * 0.6, 0.0, 0.0);
                    if(particleAir != null)
                        particleAir.setRBGColorF(1.0f, 1.0f, 1.0f);
                }
            }
        }

        if (SAND_STORMS) {
            float epsilon = 0.001f;
            if (Math.abs(currentDistance - desiredDistance) > epsilon)
                currentDistance += (desiredDistance - currentDistance) * 0.2; //TODO: We can do better.
            if (Math.abs(currentDistanceScale - desiredDistanceScale) > epsilon)
                currentDistanceScale += (desiredDistanceScale - currentDistanceScale) * 0.2; //TODO: We can do better.
            if (Math.abs(currentRed - desiredRed) > epsilon)
                currentRed += (desiredRed - currentRed) * 0.2;
            if (Math.abs(currentGreen - desiredGreen) > epsilon)
                currentGreen += (desiredGreen - currentGreen) * 0.2;
            if (Math.abs(currentBlue - desiredBlue) > epsilon)
                currentBlue += (desiredBlue - currentBlue) * 0.2;
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void fogDistance(EntityViewRenderEvent.RenderFogEvent fogEvent) {
        if (!SAND_STORMS)
            return;
        Entity entity = fogEvent.getEntity();
        World world = entity.world;
        BlockPos pos = entity.getPosition();

        if (world.isRaining()) {
            desiredDistance = 0;
            desiredDistanceScale = 0;
            int totalweight = 0;
            BlockPos[] probes = new BlockPos[]{pos, pos.add(1, 0, 0), pos.add(0, 0, 1), pos.add(-1, 0, 0), pos.add(0, 0, -1)};
            for (BlockPos probepos : probes) {
                boolean aboveground = world.canSeeSky(probepos);
                if (isDesert(world, probepos) && aboveground) {
                    desiredDistance += fogEvent.getFarPlaneDistance() / 3f;
                    desiredDistanceScale += -1.0f;
                    totalweight += 1;
                } else if (aboveground) {
                    desiredDistance += fogEvent.getFarPlaneDistance();
                    desiredDistanceScale += 0.75f;
                    totalweight += 1;
                }
            }
            desiredDistance /= totalweight;
            desiredDistanceScale /= totalweight;
        } else {
            desiredDistance = fogEvent.getFarPlaneDistance();
            desiredDistanceScale = 0.75F;
        }

        if (Math.abs(fogEvent.getFarPlaneDistance() - currentDistance) > 0.001f)
            renderFog(fogEvent.getFogMode(), currentDistance, currentDistanceScale);

        //renderFog(fogEvent.getFogMode(),fogEvent.getFarPlaneDistance(),-1.0f);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void fogColor(EntityViewRenderEvent.FogColors fogEvent) {
        if (!SAND_STORMS)
            return;
        Entity entity = fogEvent.getEntity();
        World world = entity.world;
        BlockPos pos = entity.getPosition();
        Color desiredcolor = new Color(
                MathHelper.clamp(fogEvent.getRed(), 0.0f,1.0f),
                MathHelper.clamp(fogEvent.getGreen(),0.0f,1.0f),
                MathHelper.clamp(fogEvent.getBlue(),0.0f,1.0f)
        );

        if (world.isRaining()) {
            float red = 0;
            float green = 0;
            float blue = 0;
            int totalweight = 0;
            BlockPos[] probes = new BlockPos[]{pos, pos.add(1, 0, 0), pos.add(0, 0, 1), pos.add(-1, 0, 0), pos.add(0, 0, -1)};
            for (BlockPos probepos : probes) {
                boolean aboveground = world.canSeeSky(probepos);
                if (isDesert(world, probepos)) {
                    Biome biome = world.getBiome(probepos);
                    MapColor mapcolor = biome.topBlock.getMapColor(world,probepos);
                    Color color = new Color(mapcolor.colorValue);
                    red += 2 * (color.getRed() / 255.0f);
                    green += 2 * (color.getGreen() / 255.0f);
                    blue += 2 * (color.getBlue() / 255.0f);
                    totalweight += 2;
                } else if (aboveground) {
                    red += fogEvent.getRed();
                    green += fogEvent.getGreen();
                    blue += fogEvent.getBlue();
                    totalweight += 1;
                }
            }
            desiredcolor = new Color(MathHelper.clamp(red / totalweight,0.0f,1.0f), MathHelper.clamp(green / totalweight,0.0f,1.0f), MathHelper.clamp(blue / totalweight,0.0f,1.0f));
            fogEvent.setRed(currentRed / 255.0f);
            fogEvent.setGreen(currentGreen / 255.0f);
            fogEvent.setBlue(currentBlue / 255.0f);
        }

        desiredRed = desiredcolor.getRed();
        desiredGreen = desiredcolor.getGreen();
        desiredBlue = desiredcolor.getBlue();
    }

    private boolean shouldStorm(World world, BlockPos pos) {
        Biome biome = world.getBiome(pos);

        return world.isRaining() && !biome.canRain() && !biome.isSnowyBiome();
    }

    private boolean isDesert(World world, BlockPos pos) {
        Biome biome = world.getBiome(pos);

        return BiomeDictionary.hasType(biome, BiomeDictionary.Type.SANDY);
    }

    @SideOnly(Side.CLIENT)
    private static void renderFog(int fogMode, float farPlaneDistance, float farPlaneDistanceScale) {
        if (fogMode < 0) {
            GlStateManager.setFogStart(0.0F);
            GlStateManager.setFogEnd(farPlaneDistance);
        } else {
            GlStateManager.setFogEnd(farPlaneDistance);
            GlStateManager.setFogStart(farPlaneDistance * farPlaneDistanceScale);
        }
    }

    @Override
    public String getFeatureDescription() {
        return "Add Sandstorms visual effects when it is raining in desert biomes. This helps the player know why a windmill will still break when there is no actual rain.";
    }
}
