package betterwithmods.module.tweaks;

import betterwithmods.BWMod;
import betterwithmods.common.BWMItems;
import betterwithmods.common.entity.EntityIngredientRelationRegistry;
import betterwithmods.common.entity.ai.eat.EntityAITempt;
import betterwithmods.module.Feature;
import betterwithmods.util.WorldUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.*;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreIngredient;

public class MoreTempting extends Feature {

    public static EntityIngredientRelationRegistry REGISTRY = new EntityIngredientRelationRegistry();

    @Override
    public String getFeatureDescription() {
        return "Add more valid items for tempting animals to follow. Sheep and cows follow Tall Grass or Wheat." +
                " Chickens follow most seeds." +
                " Pigs will follow Wheat, Potatoes, Beets, Chocolate";
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        REGISTRY.addPredicateEntry(new ResourceLocation(BWMod.MODID, "chicken"), e -> e instanceof EntityChicken)
                .addIngredient(new OreIngredient("seed"));
        REGISTRY.addPredicateEntry(new ResourceLocation(BWMod.MODID, "pig"), e -> e instanceof EntityPig)
                .addIngredient(Ingredient.fromItems(BWMItems.CHOCOLATE, Items.CARROT, Items.POTATO, Items.BEETROOT, Items.WHEAT));
        REGISTRY.addPredicateEntry(new ResourceLocation(BWMod.MODID, "herd"), e -> e instanceof EntitySheep || e instanceof EntityCow)
                .addIngredient(Ingredient.fromStacks(new ItemStack(Items.WHEAT), new ItemStack(Blocks.TALLGRASS)));
    }

    @SubscribeEvent
    public void addEntityAI(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityLivingBase) {
            EntityLivingBase entity = (EntityLivingBase) event.getEntity();
            if (entity instanceof EntityAnimal) {
                EntityAnimal animal = ((EntityAnimal) entity);
                Ingredient ingredient = REGISTRY.findIngredient(animal);
                if (ingredient != null) {
                    WorldUtils.removeTask(animal, net.minecraft.entity.ai.EntityAITempt.class);
                    animal.tasks.addTask(3, new EntityAITempt(animal, 1.5, false, ingredient));
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
        return new String[]{"easybreeding"};
    }

}
