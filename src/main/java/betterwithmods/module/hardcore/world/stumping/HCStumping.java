package betterwithmods.module.hardcore.world.stumping;

import betterwithmods.BWMod;
import betterwithmods.api.util.IWood;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWMItems;
import betterwithmods.common.BWOreDictionary;
import betterwithmods.module.Feature;
import betterwithmods.module.hardcore.world.HCBonemeal;
import betterwithmods.network.BWNetwork;
import betterwithmods.network.messages.MessagePlaced;
import betterwithmods.util.item.ToolsManager;
import betterwithmods.util.player.PlayerHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.Set;

import static net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;

/**
 * Created by primetoxinz on 4/20/17.
 */
public class HCStumping extends Feature {
    private static final ResourceLocation PLACED_LOG = new ResourceLocation(BWMod.MODID, "placed_log");
    public static boolean CTM;
    public static boolean SPEED_UP_WITH_TOOLS;
    public static float STUMP_BREAK_SPEED;
    public static float ROOT_BREAK_SPEED;
    public static Set<Block> STUMP_BLACKLIST = Sets.newHashSet(BWMBlocks.BLOOD_LOG);
    public static String[] BLACKLIST_CONFIG;

    public static boolean isStump(World world, BlockPos pos) {
        return isLog(world.getBlockState(pos)) && !isPlaced(world, pos) && isSoil(world.getBlockState(pos.down()), world, pos);
    }

    public static boolean isRoots(World world, BlockPos pos) {
        return isLog(world.getBlockState(pos.up())) && !isPlaced(world, pos.up()) && isSoil(world.getBlockState(pos), world, pos);
    }

    public static boolean isLog(IBlockState state) {
        if (!STUMP_BLACKLIST.contains(state.getBlock()) && state.getBlock() instanceof BlockLog) {
            if (state.getPropertyKeys().contains(BlockLog.LOG_AXIS)) {
                return state.getValue(BlockLog.LOG_AXIS).equals(BlockLog.EnumAxis.Y);
            }
            return true;
        }
        return BWOreDictionary.getWoodFromState(state) != null;
    }

    public static boolean isSoil(IBlockState state, World world, BlockPos pos) {
        return state.isSideSolid(world, pos, EnumFacing.UP) && (state.getMaterial() == Material.GROUND || state.getMaterial() == Material.GRASS);
    }

    public static PlacedCapability getCapability(World world) {
        if (world.hasCapability(PlacedCapability.PLACED_CAPABILITY, null)) {
            return world.getCapability(PlacedCapability.PLACED_CAPABILITY, null);
        }
        return null;
    }

    public static boolean addPlacedLog(World world, EntityPlayerMP player, BlockPos pos) {
        PlacedCapability capability = getCapability(world);
        if (capability != null) {
            if (capability.addPlaced(pos)) {
                BWNetwork.sendTo(new MessagePlaced(pos), player);
                return true;
            }
        }
        return false;
    }

    public static boolean isPlaced(World world, BlockPos pos) {
        PlacedCapability capability = getCapability(world);
        if (capability != null) {
            return capability.isPlaced(pos);
        }
        return false;
    }


    @Override
    public String getFeatureDescription() {
        return "Makes the bottom block of trees into stumps which cannot be removed by hand, making your mark on the world more obvious";
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        CapabilityManager.INSTANCE.register(PlacedCapability.class, new PlacedCapability.Storage(), PlacedCapability::new);
    }


    @Override
    public boolean requiresMinecraftRestartToEnable() {
        return true;
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

    @Override
    public void setupConfig() {
        BLACKLIST_CONFIG = loadPropStringList("Stump Blacklist", "Logs which do not create stumps", new String[0]);
        for (String block : BLACKLIST_CONFIG) {
            STUMP_BLACKLIST.add(Block.REGISTRY.getObject(new ResourceLocation(block)));
        }
        SPEED_UP_WITH_TOOLS = loadPropBool("Speed up with tool", "Speed up Stump mining with tools", true);
        STUMP_BREAK_SPEED = (float) loadPropDouble("Stump Break speed", "Base break speed of stumps, scaled by tool speed option", 0.03f);
        ROOT_BREAK_SPEED = (float) loadPropDouble("Root Break speed", "Base break speed of roots, scaled by tool speed option", 0.01f);
        CTM = loadPropBool("CTM Support", "Use ConnectedTextureMod to show the stumps", true) && enabled;
    }

    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.PlaceEvent event) {
        World world = event.getWorld();
        if (world.isRemote || !(event.getPlayer() instanceof EntityPlayerMP))
            return;

        if (PlayerHelper.isHolding(event.getPlayer(), HCBonemeal.FERTILIZERS))
            return;

        if (isLog(event.getState())) {
            addPlacedLog(world, (EntityPlayerMP) event.getPlayer(), event.getPos());
        }
    }

    @SubscribeEvent
    public void getHarvest(BreakSpeed event) {
        World world = event.getEntityPlayer().getEntityWorld();
        if (isStump(world, event.getPos())) {
            float scale = SPEED_UP_WITH_TOOLS ? ToolsManager.getSpeed(event.getEntityPlayer().getHeldItemMainhand(), event.getState()) : 1;
            event.setNewSpeed(STUMP_BREAK_SPEED * scale);
        }
        if (isRoots(world, event.getPos())) {
            float scale = SPEED_UP_WITH_TOOLS ? ToolsManager.getSpeed(event.getEntityPlayer().getHeldItemMainhand(), event.getState()) : 1;
            event.setNewSpeed(ROOT_BREAK_SPEED * scale);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onHarvest(BlockEvent.HarvestDropsEvent event) {
        if (isStump(event.getWorld(), event.getPos())) {
            IWood wood = BWOreDictionary.getWoodFromState(event.getState());
            if (wood != null) {
                event.getDrops().clear();
                event.getDrops().addAll(Lists.newArrayList(wood.getSawdust(1), wood.getBark(1)));
            }
        }
        if (isRoots(event.getWorld(), event.getPos())) {
            IWood wood = BWOreDictionary.getWoodFromState(event.getWorld().getBlockState(event.getPos().up()));
            if (wood != null) {
                event.setResult(Event.Result.DENY);
                event.getDrops().clear();
                event.getDrops().addAll(Lists.newArrayList(new ItemStack(BWMItems.DIRT_PILE, 2), wood.getSawdust(1), wood.getBark(1)));
            }
        }
    }

    @SubscribeEvent
    public void attachWorldCapability(AttachCapabilitiesEvent<World> evt) {
        evt.addCapability(PLACED_LOG, new PlacedCapability());
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            PlacedCapability capability = getCapability(event.player.world);
            if (capability != null) {
                BWNetwork.sendTo(new MessagePlaced(capability.getPlaced().toArray(new BlockPos[0])), (EntityPlayerMP) event.player);
            }
        }
    }
}
