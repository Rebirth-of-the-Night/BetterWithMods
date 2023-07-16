package betterwithmods.common.blocks.tile;

import betterwithmods.common.registry.block.recipe.BlockIngredient;
import betterwithmods.module.hardcore.beacons.BeaconEffect;
import betterwithmods.module.hardcore.beacons.CapabilityBeacon;
import betterwithmods.module.hardcore.beacons.HCBeacons;
import betterwithmods.module.hardcore.beacons.SpawnBeaconEffect;
import betterwithmods.util.ColorUtils;
import betterwithmods.util.WorldUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Created by primetoxinz on 7/17/17.
 */
public class TileEntityBeacon extends net.minecraft.tileentity.TileEntityBeacon {

    private static final int DEFAULT_TICK_RATE = 120;

    private int currentLevel;
    private boolean active;
    private IBlockState type = Blocks.AIR.getDefaultState();
    private BeaconEffect effect;
    private int tick;
    private List<BeamSegment> segments = Lists.newArrayList();

    @SideOnly(Side.CLIENT)
    private long beamRenderCounter;
    @SideOnly(Side.CLIENT)
    private float beamRenderScale;

    @Override
    public void update() {
        if (tick <= 0) {
            if (!canSeeSky() && active) {
                deactivate();
                return;
            }

            effect = HCBeacons.getEffect(world, pos, world.getBlockState(pos.down()));

            tick = DEFAULT_TICK_RATE;

            if (effect != null) {
                BlockIngredient structureBlock = effect.getStructureBlock();
                currentLevel = calcLevel(structureBlock);
                if (currentLevel > 0) {
                    if (!active) {
                        activate();
                    }
                    effect.apply(effect.getEntitiesInRange(world, pos, currentLevel), world, pos, currentLevel);
                    calcSegments();
                    tick = effect.getTickRate();
                } else if (active) {
                    deactivate();
                }
            }
        }
        tick--;
    }

    private void activate() {
        CapabilityBeacon storage = world.getCapability(CapabilityBeacon.BEACON_CAPABILITY, EnumFacing.UP);
        if (storage != null) {
            storage.addBeacon(pos, currentLevel);
        }
        effect.onBeaconCreate(world, pos, currentLevel);
        WorldUtils.playBroadcast(world, effect.getActivationSound());
        this.world.getEntitiesWithinAABB(EntityPlayerMP.class, new AxisAlignedBB(pos, pos.add(1, -4, 1)).grow(10.0D, 5.0D, 10.0D)).forEach(player -> CriteriaTriggers.CONSTRUCT_BEACON.trigger(player, this));
        this.active = true;
    }

    private void deactivate() {
        this.segments.clear();
        if (effect != null) {
            effect.onBeaconBreak(world, pos, currentLevel);
            WorldUtils.playBroadcast(world, effect.getDeactivationSound());
            this.effect = null;
        }


        CapabilityBeacon storage = world.getCapability(CapabilityBeacon.BEACON_CAPABILITY, EnumFacing.UP);
        if (storage != null) {
            storage.removeBeacon(pos);
        }

        this.active = false;
    }

    public boolean canSeeSky() {
        if (world.provider.isNether()) {
            BlockPos.MutableBlockPos pos;
            for (pos = new BlockPos.MutableBlockPos(getPos().up()); pos.getY() < 128; pos.setY(pos.getY() + 1)) {
                IBlockState state = world.getBlockState(pos);
                if (state.getBlock() == Blocks.BEDROCK)
                    return true;
                if (state.getBlock().getLightOpacity(state, world, pos) > 0)
                    return false;
            }
            return true;
        }
        return world.canBlockSeeSky(pos);
    }

    @SideOnly(Side.CLIENT)
    public float getBeamScale() {

        int i = (int) (this.world.getTotalWorldTime() - this.beamRenderCounter);
        this.beamRenderCounter = this.world.getTotalWorldTime();

        if (i > 1) {
            this.beamRenderScale -= (float) i / 40.0F;

            if (this.beamRenderScale < 0.0F) {
                this.beamRenderScale = 0.0F;
            }
        }

        this.beamRenderScale += 0.025F;

        if (this.beamRenderScale > 1.0F) {
            this.beamRenderScale = 1.0F;
        }

        return this.beamRenderScale;
    }

    private void calcSegments() {
        this.segments.clear();
        float[] color = ColorUtils.getColorFromBlock(world, getPos().up(), getPos());
        if (effect != null) {
            color = effect.getBaseBeaconBeamColor(pos);
        }

        BeamSegment segment = new BeamSegment(color);
        this.segments.add(segment);

        BlockPos.MutableBlockPos pos;
        for (pos = new BlockPos.MutableBlockPos(getPos().up()); pos.getY() < 256; pos.move(EnumFacing.UP)) {
            color = ColorUtils.getColorFromBlock(world, pos, getPos());
            if (!Arrays.equals(color, new float[]{1, 1, 1})) {
                color = ColorUtils.average(color, segment.getColors());
                if (Arrays.equals(color, segment.getColors())) {
                    segment.incrementHeight();
                } else {
                    segment = new BeamSegment(color);
                    segments.add(segment);
                }
            } else {
                segment.incrementHeight();
            }
        }
    }

    public List<BeamSegment> getSegments() {
        return segments;
    }


    public int calcLevel(BlockIngredient structureBlock) {
        IBlockState stateAtPos;
        int r;
        for (r = 1; r <= 4; r++) {
            for (int x = -r; x <= r; x++) {
                for (int z = -r; z <= r; z++) {
                    stateAtPos = world.getBlockState(pos.add(x, -r, z));
                    if (!structureBlock.apply(world, pos, stateAtPos)) {
                        return r - 1;
                    }
                }
            }
        }
        return r - 1;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {

        //TODO - This should all be in a class related to SpawnBeaconEffect not here
        NBTTagCompound tag = new NBTTagCompound();
        NBTUtil.writeBlockState(tag, type);
        compound.setTag("type", tag);

        if (SpawnBeaconEffect.SPAWN_LIST.containsKey(this.getPos())) {
            NBTTagList list = new NBTTagList();
            for (SpawnBeaconEffect.BindingPoint point : SpawnBeaconEffect.SPAWN_LIST.get(this.getPos())) {
                list.appendTag(point.serializeNBT());
            }
            compound.setTag("spawns", list);
        }

        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        //TODO - This should all be in a class related to SpawnBeaconEffect not here

        NBTTagCompound type = (NBTTagCompound) compound.getTag("type");
        this.type = NBTUtil.readBlockState(type);
        if (compound.hasKey("spawns")) {
            NBTTagList list = compound.getTagList("spawns", 10);
            HashSet<SpawnBeaconEffect.BindingPoint> points = Sets.newHashSet();
            for (Iterator<NBTBase> iter = list.iterator(); iter.hasNext(); ) {
                NBTTagCompound tag = (NBTTagCompound) iter.next();
                points.add(new SpawnBeaconEffect.BindingPoint(tag));
            }
            SpawnBeaconEffect.SPAWN_LIST.put(this.getPos(), points);
        }
        super.readFromNBT(compound);
    }

    @SuppressWarnings("deprecation")
    public boolean processInteraction(World world, EntityPlayer player, ItemStack stack) {
        if (player.isCreative() && !stack.isEmpty()) {
            if (stack.getItem() instanceof ItemBlock) {
                Block block = ((ItemBlock) stack.getItem()).getBlock();
                IBlockState state = block.getStateFromMeta(stack.getMetadata());
                if (HCBeacons.isValidBeaconBase(state)) {
                    int r;
                    for (r = 1; r <= 4; r++) {
                        for (int x = -r; x <= r; x++) {
                            for (int z = -r; z <= r; z++) {
                                this.world.setBlockState(getPos().add(x, -r, z), state);
                            }
                        }
                    }
                }
            }
        }

        if (!world.isRemote) {
            if (effect != null) {
                boolean interacted = this.effect.onPlayerInteracted(world, getPos(), getLevels() - 1, player, player.getActiveHand(), stack);
                if (interacted)
                    this.world.playBroadcastSound(1023, getPos(), 0);
                return interacted;
            }
        }
        return false;
    }

    @Override
    public int getLevels() {
        return currentLevel;
    }

    public boolean isEnabled() {
        return active;
    }


    public void onRemoved() {
        if (effect != null) {
            this.effect.onBeaconBreak(world, pos, currentLevel);
        }

        SpawnBeaconEffect.removeAll(getPos());
        CapabilityBeacon storage = world.getCapability(CapabilityBeacon.BEACON_CAPABILITY, EnumFacing.UP);
        if (storage != null) {
            storage.removeBeacon(pos);
        }
    }


    public class BeamSegment extends net.minecraft.tileentity.TileEntityBeacon.BeamSegment {
        public BeamSegment(float[] colorsIn) {
            super(colorsIn);
        }

        @Override
        protected void incrementHeight() {
            super.incrementHeight();
        }
    }
}
