package betterwithmods.module.tweaks;

import betterwithmods.BWMod;
import betterwithmods.module.Feature;
import betterwithmods.util.InvUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.minecart.MinecartUpdateEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Predicate;

public class HopperMinecarts extends Feature {

    private static final ResourceLocation COUNTER = new ResourceLocation(BWMod.MODID, "counter");
    private static final int STACK_SIZE = 1;
    private static final Predicate<TileEntity> IGNORE_INVENTORIES_THAT_PULL = tile -> !(tile instanceof TileEntityHopper);
    @CapabilityInject(Counter.class)
    private static Capability<Counter> CAPABILITY_COUNTER;

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        CapabilityManager.INSTANCE.register(Counter.class, new Counter.Storage(), Counter::new);
    }

    @Override
    public String getFeatureDescription() {
        return "Allow Hopper Minecarts to output to inventories below them";
    }

    @SubscribeEvent
    public void onCapabilityAttach(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityMinecartHopper)
            event.addCapability(COUNTER, new Counter());
    }

    public Counter getCounter(EntityMinecartHopper entity) {
        return entity.getCapability(CAPABILITY_COUNTER, null);
    }

    @SubscribeEvent
    public void onTick(MinecartUpdateEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof EntityMinecartHopper) {
            EntityMinecartHopper cart = (EntityMinecartHopper) event.getEntity();
            World world = cart.getWorld();
            IItemHandler cartInv = cart.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
            Optional<IItemHandler> inv = InvUtils.getItemHandler(world, cart.getPosition().down(), EnumFacing.UP, IGNORE_INVENTORIES_THAT_PULL);

            if (!world.isRemote && entity.isEntityAlive() && cartInv != null && inv != null) {
                Counter c = getCounter(cart);
                if (c != null) {
                    int ejectCounter = c.getCounter();

                    if (ejectCounter > 2) {
                        int slot = InvUtils.getFirstOccupiedStackInRange(cartInv, 0, 4);
                        if (slot != -1) {
                            ItemStack stack = cartInv.getStackInSlot(slot);
                            if (inv.isPresent()) {
                                if (InvUtils.canInsert(inv.get(), stack, STACK_SIZE)) {
                                    ItemStack insert = InvUtils.insert(inv.get(), stack, STACK_SIZE, false);
                                    InvUtils.consumeItemsInInventory(cartInv, stack, STACK_SIZE - insert.getCount(), false);
                                }
                            }
                        }
                        c.reset();
                    } else {
                        c.increase();
                    }
                }
            }
        }
    }

    public static class Counter implements ICapabilitySerializable<NBTTagCompound> {

        private int counter;

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return CAPABILITY_COUNTER == capability;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            if (hasCapability(capability, facing))
                return CAPABILITY_COUNTER.cast(this);
            return null;
        }

        @Override
        public NBTTagCompound serializeNBT() {

            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("counter", counter);
            return tag;
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            counter = nbt.getInteger("counter");
        }

        public int getCounter() {
            return counter;
        }

        public void setCounter(int counter) {
            this.counter = counter;
        }

        public void increase() {
            setCounter(getCounter() + 1);
        }

        public void reset() {
            setCounter(0);
        }

        public static class Storage implements Capability.IStorage<Counter> {

            @Nullable
            @Override
            public NBTBase writeNBT(Capability<Counter> capability, Counter instance, EnumFacing side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<Counter> capability, Counter instance, EnumFacing side, NBTBase nbt) {
                instance.deserializeNBT((NBTTagCompound) nbt);
            }
        }
    }
}
