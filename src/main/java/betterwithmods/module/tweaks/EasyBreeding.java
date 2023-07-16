package betterwithmods.module.tweaks;

import betterwithmods.BWMod;
import betterwithmods.common.BWMItems;
import betterwithmods.common.entity.EntityIngredientRelationRegistry;
import betterwithmods.common.entity.ai.eat.EntityAIAnimalEat;
import betterwithmods.module.Feature;
import betterwithmods.module.hardcore.creatures.chicken.HCChickens;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreIngredient;

/**
 * Created by primetoxinz on 4/20/17.
 */
public class EasyBreeding extends Feature {

    public static EntityIngredientRelationRegistry REGISTRY = new EntityIngredientRelationRegistry();

    @Override
    public String getFeatureDescription() {
        return "Animals will pick up breeding items off of the ground as necessary, some animals will also breed with more items.";
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        REGISTRY.addBreedingEntry(new HCChickens.LayerIngredientRelation());
        //only called if the layer entry fails
        REGISTRY.addPredicateEntry(new ResourceLocation(BWMod.MODID, "chicken"), e -> e instanceof EntityChicken).addIngredient(new OreIngredient("seed"));

        REGISTRY.addPredicateEntry(new ResourceLocation(BWMod.MODID, "pig"), e -> e instanceof EntityPig).addIngredient(Ingredient.fromItems(BWMItems.CHOCOLATE, Items.CARROT, Items.POTATO, Items.BEETROOT, Items.WHEAT, BWMItems.KIBBLE));
        REGISTRY.addPredicateEntry(new ResourceLocation(BWMod.MODID, "herd"), e -> e instanceof EntitySheep || e instanceof EntityCow).addIngredient(Ingredient.fromStacks(new ItemStack(Items.WHEAT)));
    }

    @SubscribeEvent
    public void addEntityAI(EntityJoinWorldEvent event) {
        if (!event.getWorld().isRemote && event.getEntity() instanceof EntityLivingBase) {
            EntityLivingBase entity = (EntityLivingBase) event.getEntity();
            if (entity instanceof EntityAnimal) {
                EntityAnimal animal = ((EntityAnimal) entity);
                Ingredient ingredient = REGISTRY.findIngredient(animal);
                if (ingredient != null) {
                    animal.tasks.addTask(3, new EntityAIAnimalEat(animal, ingredient, 5));
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.isCanceled())
            return;

        if (event.getTarget() instanceof EntityLivingBase) {
            EntityLivingBase entity = (EntityLivingBase) event.getTarget();

            if (entity instanceof EntityAnimal) {
                EntityAnimal animal = ((EntityAnimal) entity);
                Ingredient ingredient = REGISTRY.findIngredient(animal);
                if (ingredient != null) {
                    if (animal.isChild()) {
                        event.setCanceled(true);
                        event.setCancellationResult(EnumActionResult.FAIL);
                    }

                    EntityPlayer player = event.getEntityPlayer();
                    EnumHand hand = event.getHand();
                    ItemStack itemstack = player.getHeldItem(hand);


                    if ((ingredient.apply(itemstack) || animal.isBreedingItem(itemstack)) && animal.getGrowingAge() == 0 && !animal.isInLove()) {
                        if (!player.capabilities.isCreativeMode) {
                            itemstack.shrink(1);
                        }
                        animal.setInLove(player);
                        event.setCanceled(true);
                        event.setCancellationResult(EnumActionResult.SUCCESS);
                    }
                }
            }
        }
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

    @Override
    public String[] getIncompatibleMods() {
        return new String[]{"easybreeding", "animania"};
    }

}
