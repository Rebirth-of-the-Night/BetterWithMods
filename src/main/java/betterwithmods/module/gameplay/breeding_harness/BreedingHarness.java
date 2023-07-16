package betterwithmods.module.gameplay.breeding_harness;

import betterwithmods.BWMod;
import betterwithmods.client.model.render.RenderUtils;
import betterwithmods.common.BWMItems;
import betterwithmods.common.items.ItemBreedingHarness;
import betterwithmods.module.Feature;
import betterwithmods.module.gameplay.breeding_harness.models.ModelCowHarness;
import betterwithmods.module.gameplay.breeding_harness.models.ModelSheepHarness;
import betterwithmods.network.BWNetwork;
import betterwithmods.network.messages.MessageHarness;
import betterwithmods.util.InvUtils;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelPig;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Set;

public class BreedingHarness extends Feature {

    public BreedingHarness() {
    }

    @Override
    public String getFeatureDescription() {
        return "Add the Breeding Harness, which can be put on most domesticated animals and making their legs immobile, they are still able to eat food and breed while restrained.";
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        BWMItems.registerItem(BWMItems.BREEDING_HARNESS);
        CapabilityManager.INSTANCE.register(CapabilityHarness.class, new CapabilityHarness.Storage(), CapabilityHarness::new);
    }



    @SideOnly(Side.CLIENT)
    private static <T extends EntityLiving> void addLayer(Class<T> entity, ModelBase model, ResourceLocation texture) {
        RenderLiving<T> render = RenderUtils.getRender(entity);
        LayerHarness<T> layer = new LayerHarness<>(model, render, texture);
        render.addLayer(layer);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void postInitClient(FMLPostInitializationEvent event) {
        addLayer(EntityCow.class, new ModelCowHarness(0.5f), new ResourceLocation(BWMod.MODID, "textures/entity/cow_harness.png"));
        addLayer(EntityPig.class, new ModelPig(0.5f), new ResourceLocation(BWMod.MODID, "textures/entity/pig_harness.png"));
        addLayer(EntitySheep.class, new ModelSheepHarness(0.5f), new ResourceLocation(BWMod.MODID, "textures/entity/sheep_harness.png"));
    }

    private static final ResourceLocation CAPABILITY = new ResourceLocation(BWMod.MODID, "harness");

    private static void sendPacket(Entity entity) {
        CapabilityHarness cap = getCapability(entity);
        if (cap != null) {
            BWNetwork.sendToAllAround(new MessageHarness(entity.getEntityId(), cap.getHarness()), entity.getEntityWorld(),entity.getPosition());
        }
    }

    @SubscribeEvent
    public void onAttach(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if (harnessEntity(entity)) {
            event.addCapability(CAPABILITY, new CapabilityHarness());
        }
    }

    @SubscribeEvent
    public void onTrack(PlayerEvent.StartTracking event) {
        if (event.getEntityPlayer().world.isRemote)
            return;
        Entity entity = event.getTarget();
        sendPacket(entity);
    }

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getWorld().isRemote)
            return;
        Entity entity = event.getTarget();

        CapabilityHarness cap = getCapability(entity);
        if (cap != null) {
            ItemStack hand = event.getItemStack();
            ItemStack harness = cap.getHarness();
            if (harness.isEmpty() && !event.getEntityPlayer().isSneaking()) {
                if (hand.getItem() instanceof ItemBreedingHarness) {
                    cap.setHarness(InvUtils.setCount(hand.copy(), 1));
                    if(!event.getEntityPlayer().capabilities.isCreativeMode)
                        hand.shrink(1);
                    event.getWorld().playSound(null, entity.getPosition(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.NEUTRAL, 0.5f, 1.3f);
                    event.getWorld().playSound(null, entity.getPosition(), SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, SoundCategory.NEUTRAL, 0.5f, 1.3f);
                    event.getEntityPlayer().swingArm(EnumHand.MAIN_HAND);
                    if (entity instanceof EntitySheep) {
                        ((EntitySheep) entity).setSheared(true);
                    }
                    sendPacket(entity);
                }
            } else if (!harness.isEmpty() && event.getEntityPlayer().isSneaking() && hand.isEmpty()) {
                ItemHandlerHelper.giveItemToPlayer(event.getEntityPlayer(), harness.copy());
                harness.shrink(1);
                event.getWorld().playSound(null, entity.getPosition(), SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.NEUTRAL, 1, 1);
                event.getWorld().playSound(null, entity.getPosition(), SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, SoundCategory.NEUTRAL, 1, 1f);
                event.getEntityPlayer().swingArm(EnumHand.MAIN_HAND);
                sendPacket(entity);
            }

        }

    }


    @SubscribeEvent
    public void onLivingTick(LivingEvent.LivingUpdateEvent e) {
        EntityLivingBase entity = e.getEntityLiving();
        if(harnessEntity(entity)) {
            if (hasHarness(entity)) {
                entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(-1);
                if (entity instanceof EntitySheep && !((EntitySheep) entity).getSheared()) {
                    ((EntitySheep) entity).setSheared(true);
                }
            } else {
                entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
            }
        }
    }


    public static CapabilityHarness getCapability(Entity entity) {
        return entity.getCapability(CapabilityHarness.HARNESS_CAPABILITY, null);
    }

    public static boolean hasHarness(Entity entity) {
        CapabilityHarness cap = getCapability(entity);
        return cap != null && cap.getHarness().getItem() instanceof ItemBreedingHarness;
    }

    public static final Set<Class<? extends EntityAnimal>> HARNESS_ANIMALS = Sets.newHashSet();
    private static Object2BooleanMap<Class<? extends Entity>> HARNESS_CACHE = new Object2BooleanOpenHashMap<>();
    static {
        HARNESS_ANIMALS.add(EntityCow.class);
        HARNESS_ANIMALS.add(EntitySheep.class);
        HARNESS_ANIMALS.add(EntityPig.class);
        for (Class<? extends EntityAnimal> c : HARNESS_ANIMALS){
            HARNESS_CACHE.put(c, true);
        }
    }
    public static boolean harnessEntity(Entity entity) {
        if (!(entity instanceof EntityAnimal)){
            return false;
        }
        Class<? extends Entity> c = entity.getClass();
        Boolean harness = HARNESS_CACHE.get(c);
        if (harness != null){
            return harness;
        }
        boolean canHarness = false;
        for (Class<? extends EntityAnimal> cTest : HARNESS_ANIMALS){
            if (cTest.isAssignableFrom(c)){
                canHarness = true;
                break;
            }
        }
        return HARNESS_CACHE.put(c, canHarness);
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }
}
