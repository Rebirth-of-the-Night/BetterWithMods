package betterwithmods.module.tweaks;

import betterwithmods.BWMod;
import betterwithmods.common.BWRegistry;
import betterwithmods.common.items.ItemMaterial;
import betterwithmods.module.Feature;
import betterwithmods.util.StackIngredient;
import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreIngredient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

/**
 * Created by primetoxinz on 4/20/17.
 */
public class Dung extends Feature {
    private boolean wolvesOnly;
    public static final ResourceLocation DUNG_PRODUCER = new ResourceLocation(BWMod.MODID, "dung_producer");
    @CapabilityInject(DungProducer.class)
    public static Capability<DungProducer> DUNG_PRODUCER_CAP;

    @Override
    public String getFeatureDescription() {
        return "Animals will launch dung depending on their conditions, a useful material";
    }

    @Override
    public void setupConfig() {
        wolvesOnly = loadPropBool("Only Wolves", "Only Wolves will produce dung", true);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        CapabilityManager.INSTANCE.register(DungProducer.class, new Capability.IStorage<DungProducer>() {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<DungProducer> capability, DungProducer instance, EnumFacing side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<DungProducer> capability, DungProducer instance, EnumFacing side, NBTBase nbt) {
                instance.deserializeNBT((NBTTagCompound) nbt);
            }
        }, DungProducer::new);
        BWRegistry.CAULDRON.addUnstokedRecipe(Lists.newArrayList(StackIngredient.fromStacks(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.SCOURED_LEATHER)), new OreIngredient("dung")), Lists.newArrayList(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.TANNED_LEATHER)));
        BWRegistry.CAULDRON.addUnstokedRecipe(Lists.newArrayList(StackIngredient.fromStacks(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.SCOURED_LEATHER_CUT, 2)), new OreIngredient("dung")), Lists.newArrayList(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.TANNED_LEATHER_CUT, 2)));
    }

    @SubscribeEvent
    public void mobDungProduction(LivingEvent.LivingUpdateEvent evt) {
        if (evt.getEntityLiving().getEntityWorld().isRemote)
            return;
        if (evt.getEntityLiving() instanceof EntityAnimal) {
            EntityAnimal animal = (EntityAnimal) evt.getEntityLiving();
            if (wolvesOnly && !(animal instanceof EntityWolf))
                return;
            if (!animal.hasCapability(DUNG_PRODUCER_CAP, null))
                return;
            DungProducer dungProducer = animal.getCapability(DUNG_PRODUCER_CAP, null);
            if (dungProducer != null) {
                if (animal.isInLove() && dungProducer.nextPoop < 0) {
                    dungProducer.nextPoop = 12000;
                } else if (dungProducer.nextPoop > 0) {
                    Random rand = animal.getRNG();
                    int light = animal.getEntityWorld().getLight(animal.getPosition());
                    dungProducer.nextPoop = Math.max(0, dungProducer.nextPoop - (rand.nextInt(16) < light ? 1 : 2));
                    if (dungProducer.nextPoop == 0) {
                        EnumFacing poopDir = findSpaceForPoop(animal.world, animal.getPosition(), rand);
                        if (poopDir != null) {
                            BlockPos poopSpot = animal.getPosition().offset(poopDir);
                            EntityItem item = new EntityItem(animal.world, poopSpot.getX() + 0.5, poopSpot.getY() + 0.5, poopSpot.getZ() + 0.5, ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.DUNG));
                            item.motionX = poopDir.getXOffset() == 0 ? rand.nextDouble() * 0.25 - 0.125 : 0.7;
                            item.motionY = 0;
                            item.motionZ = poopDir.getZOffset() == 0 ? rand.nextDouble() * 0.25 - 0.125 : 0.7;
                            item.setDefaultPickupDelay();
                            animal.world.spawnEntity(item);
                        }
                        dungProducer.nextPoop = -1;
                    }
                }
            }
        }
    }

    private EnumFacing findSpaceForPoop(World world, BlockPos pos, Random random) {
        int dir = random.nextInt(4);
        for (int i = 0; i < 4; i++) {
            EnumFacing checkFacing = EnumFacing.byHorizontalIndex((dir + i) % 4);
            BlockPos checkPos = pos.offset(checkFacing);
            if (world.isAirBlock(checkPos) || world.getBlockState(checkPos).getBlock().isReplaceable(world, checkPos))
                return checkFacing;
        }
        return null;
    }

    public static class DungProducer implements ICapabilitySerializable<NBTTagCompound> {
        public int nextPoop = -1;

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == DUNG_PRODUCER_CAP;
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
            return hasCapability(capability, facing) ? DUNG_PRODUCER_CAP.cast(this) : null;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setInteger("NextPoop", nextPoop);
            return nbt;
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            nextPoop = nbt.getInteger("NextPoop");
        }
    }

    @SubscribeEvent
    public void dungCapabilityEvent(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if (entity instanceof EntityAnimal) {
            if (wolvesOnly && !(entity instanceof EntityWolf))
                return;
            event.addCapability(DUNG_PRODUCER, new DungProducer());
        }
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }
}
