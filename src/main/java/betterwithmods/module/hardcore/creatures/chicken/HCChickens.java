package betterwithmods.module.hardcore.creatures.chicken;

import betterwithmods.BWMod;
import betterwithmods.common.entity.EntityIngredientRelation;
import betterwithmods.module.Feature;
import betterwithmods.util.WorldUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreIngredient;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static betterwithmods.module.hardcore.creatures.chicken.EggLayer.EGG_LAYER_CAP;

/**
 * Created by primetoxinz on 5/13/17.
 */
public class HCChickens extends Feature {

    public static final ResourceLocation EGG_LAYER = new ResourceLocation(BWMod.MODID, "egglayer");
    public static Ingredient SEEDS = new OreIngredient("seed");

    @Nullable
    public static EggLayer getLayer(@Nonnull Entity entity) {
        return entity.getCapability(EGG_LAYER_CAP, EnumFacing.DOWN);
    }

    @Override
    public void setupConfig() {
    }

    @Override
    public String getFeatureDescription() {
        return "Rework chicken breeding. Chickens don't breed in pairs. You feed a single chicken 1 seed, and it craps out an egg that can be thrown. The egg either makes a chicken, or drops raw egg.";
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        CapabilityManager.INSTANCE.register(EggLayer.class, new EggLayer.CapabilityEggLayer(), EggLayer::new);
    }


    @SubscribeEvent
    public void onAttachCap(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityChicken) {
            event.addCapability(EGG_LAYER, new EggLayer(new ItemStack(Items.EGG), SEEDS));
        }
    }

    @SubscribeEvent
    public void onEntityTick(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entityLiving = event.getEntityLiving();
        if (entityLiving.world.isRemote)
            return;
        if (entityLiving instanceof EntityChicken) {
            EntityChicken chicken = (EntityChicken) entityLiving;
            //Stops vanilla egg dropping mechanic
            chicken.timeUntilNextEgg = 6000000;
        }
        if (entityLiving.hasCapability(EGG_LAYER_CAP, EnumFacing.DOWN)) {
            EggLayer layer = getLayer(entityLiving);
            if (layer != null) {
                if (layer.isFeed()) {
                    layer.setTicks(layer.getTicks() - 1);

                    if (WorldUtils.isTimeFrame(entityLiving.world, WorldUtils.TimeFrame.DAWN)) {
                        if (layer.canLayEgg()) {
                            layer.lay(entityLiving);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!event.getWorld().isRemote && SEEDS.apply(event.getItemStack()) && event.getTarget() instanceof EntityLiving) {
            EggLayer layer = getLayer(event.getTarget());
            if (layer != null) {
                event.setCanceled(true);
                event.setCancellationResult(EnumActionResult.SUCCESS);
                layer.feed((EntityLiving) event.getTarget(), event.getItemStack());
            }
        }
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

    public static class LayerIngredientRelation implements EntityIngredientRelation {

        @Override
        public ResourceLocation getName() {
            return EGG_LAYER;
        }

        @Override
        public Ingredient getIngredient(Entity entity) {
            EggLayer layer = HCChickens.getLayer(entity);
            if (layer != null)
                return layer.getFeedItems();
            return null;
        }
    }

}
