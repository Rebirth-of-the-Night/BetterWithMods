package betterwithmods.module.industry.pollution;

import betterwithmods.api.capabilities.PollutionCapability;
import betterwithmods.api.tile.IPollutant;
import betterwithmods.common.damagesource.BWDamageSource;
import betterwithmods.common.items.tools.ItemSoulforgeArmor;
import betterwithmods.util.player.PlayerHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class PollutionHandler {
    public HashMap<String, Float> biomeMods = new HashMap<>();


    @SubscribeEvent
    public void onMobSpawn(LivingSpawnEvent.SpecialSpawn event) {
        if (!(event.getEntityLiving() instanceof EntityLiving))
            return;
        EntityLiving living = (EntityLiving) event.getEntityLiving();
        BlockPos pos = new BlockPos(event.getX(), 0, event.getZ());
        float pollution = getPollutionStat(event.getWorld(), new ChunkPos(pos));
        if (pollution > 0) {
            event.setCanceled(true);
            living.onInitialSpawn(getDifficultyForLocation(event.getWorld(), pos, pollution), null);
        } else {
            living.onInitialSpawn(event.getWorld().getDifficultyForLocation(pos), null);
        }
    }

    public static DifficultyInstance getDifficultyForLocation(World world, BlockPos pos, float pollution) {
        long i = 0L;
        float f = 0.0F;

        if (world.isBlockLoaded(pos)) {
            f = world.getCurrentMoonPhaseFactor();
            i = (long) (world.getChunk(pos).getInhabitedTime() * pollution);
        }

        return new DifficultyInstance(world.getDifficulty(), world.getWorldTime(), i, f);
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkDataEvent.Load evt) {
        if (!evt.getWorld().isRemote && evt.getWorld().hasCapability(WorldPollutionCapability.POLLUTION, null)) {
            IWorldPollution pollution = evt.getWorld().getCapability(WorldPollutionCapability.POLLUTION, null);
            if (pollution != null) {
                ChunkPos pos = evt.getChunk().getPos();
                if (evt.getData().hasKey("bwm_pollution")) {
                    NBTTagCompound tag = evt.getData().getCompoundTag("bwm_pollution");
                    pollution.readNBT(pos, tag);
                } else {
                    pollution.setPollution(pos, 0.0F);
                    pollution.setLeafCount(pos, (byte) 0);
                }
            }
        }
    }

    @SubscribeEvent
    public void onChunkUnload(ChunkDataEvent.Save evt) {
        if (!evt.getWorld().isRemote && evt.getWorld().hasCapability(WorldPollutionCapability.POLLUTION, null)) {
            IWorldPollution pollution = evt.getWorld().getCapability(WorldPollutionCapability.POLLUTION, null);
            if (pollution != null) {
                ChunkPos pos = evt.getChunk().getPos();
                if (pollution.getPollution(pos) > -1) {
                    NBTTagCompound tag = evt.getData();
                    tag.setTag("bwm_pollution", pollution.writeNBT(pos, new NBTTagCompound()));
                    if (!evt.getChunk().isLoaded())
                        pollution.removePollution(pos);
                }
            }
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent evt) {
        World world = evt.world;
        if (world.hasCapability(WorldPollutionCapability.POLLUTION, null) && !evt.world.isRemote && evt.phase == TickEvent.Phase.START) {
            IWorldPollution pollution = world.getCapability(WorldPollutionCapability.POLLUTION, null);
            if (pollution != null) {
                long time = world.getWorldTime();
                if (time % 24000 == 6000) {
                    pollution.calculateLeafCount();
                }
                if (time % 8000 == 0) {
                    pollution.calculatePollutionSpread();
                }
                //TODO: Kill leaves if acid rain is happening.
                else if (time % 1000 == 0) {
                    pollution.calculatePollutionReduction();
                }

                List<TileEntity> tiles;
                synchronized (world.loadedTileEntityList) {
                    tiles = world.loadedTileEntityList.stream().filter(tileEntity -> tileEntity.hasCapability(PollutionCapability.POLLUTION, EnumFacing.UP)).collect(Collectors.toList());
                }
                if (!tiles.isEmpty()) {
                    for (TileEntity tile : tiles) {
                        IPollutant pollutant = tile.getCapability(PollutionCapability.POLLUTION, EnumFacing.UP);
                        if (pollutant.isPolluting() && pollution.getPollution(new ChunkPos(tile.getPos())) > -1) {
                            ChunkPos p = new ChunkPos(tile.getPos());
                            float pollute = pollution.getPollution(p);
                            pollution.setPollution(p, pollute + pollutant.getPollutionRate());
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.player instanceof EntityPlayerMP && event.phase == TickEvent.Phase.END) {
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            if (player.getEntityWorld().hasCapability(WorldPollutionCapability.POLLUTION, null)) {
                IWorldPollution pollution = player.getEntityWorld().getCapability(WorldPollutionCapability.POLLUTION, null);
                if (pollution != null && !player.capabilities.isCreativeMode && isRaining(player.getEntityWorld(), player.getPosition()) && player.getEntityWorld().canSeeSky(player.getPosition())) {
                    ChunkPos pos = new ChunkPos(player.getPosition());
                    float pollute = pollution.getPollution(pos);
                    if (pollute > 6000F && player.getEntityWorld().getWorldTime() % 20 == 0) {
                        if (!PlayerHelper.hasFullSet(player, ItemSoulforgeArmor.class)) {
                            player.attackEntityFrom(BWDamageSource.acidRain, 1F);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void attachWorldCapability(AttachCapabilitiesEvent<World> evt) {
        final World world = evt.getObject();
        if (!world.isRemote) {
            evt.addCapability(new ResourceLocation("betterwithmods", "world_pollution"), new ICapabilitySerializable<NBTTagCompound>() {
                IWorldPollution instance = new WorldPollutionCapability.Default(world);

                @Override
                public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
                    return capability == WorldPollutionCapability.POLLUTION;
                }

                @Override
                public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
                    return capability == WorldPollutionCapability.POLLUTION ? WorldPollutionCapability.POLLUTION.cast(instance) : null;
                }

                @Override
                public NBTTagCompound serializeNBT() {
                    return new NBTTagCompound();
                }

                @Override
                public void deserializeNBT(NBTTagCompound tag) {

                }
            });
        }
    }

    @SubscribeEvent
    public void attachPollutantCapability(AttachCapabilitiesEvent<TileEntity> evt) {
        TileEntity tile = evt.getObject();
        if (tile instanceof TileEntityFurnace) {
            final TileEntityFurnace furnace = (TileEntityFurnace) tile;
            evt.addCapability(new ResourceLocation("betterwithmods", "furnace_pollution"), new ICapabilitySerializable<NBTTagCompound>() {
                IPollutant instance = new CapabilityFurnacePollution(furnace);

                @Override
                public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
                    return capability == PollutionCapability.POLLUTION;
                }

                @Override
                public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
                    return capability == PollutionCapability.POLLUTION ? PollutionCapability.POLLUTION.cast(instance) : null;
                }

                @Override
                public NBTTagCompound serializeNBT() {
                    return new NBTTagCompound();
                }

                @Override
                public void deserializeNBT(NBTTagCompound tag) {

                }
            });
        }
    }

    private boolean isRaining(World world, BlockPos pos) {
        return world.isRaining() && world.getBiome(pos).canRain();
    }

    public float getPollutionStat(World world, ChunkPos pos) {
        if (world.hasCapability(WorldPollutionCapability.POLLUTION, null)) {
            IWorldPollution pollution = world.getCapability(WorldPollutionCapability.POLLUTION, null);
            return pollution.getPollution(pos);
        }
        return -1;
    }

    public byte getLeafStat(World world, ChunkPos pos) {
        if (world.hasCapability(WorldPollutionCapability.POLLUTION, null)) {
            return world.getCapability(WorldPollutionCapability.POLLUTION, null).getLeafCount(pos);
        }
        return -1;
    }
}
