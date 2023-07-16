package betterwithmods.module.hardcore.creatures;

import betterwithmods.library.event.EntitySetEquipmentEvent;
import betterwithmods.module.CompatFeature;
import com.google.common.collect.Maps;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;

public class HCMobEquipment extends CompatFeature {
    public static final EntityEquipmentSlot[] ARMOR_SLOTS = new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
    public static final ItemStack[] IRON_ARMOR = new ItemStack[]{new ItemStack(Items.IRON_HELMET), new ItemStack(Items.IRON_CHESTPLATE), new ItemStack(Items.IRON_LEGGINGS), new ItemStack(Items.IRON_BOOTS)};
    public static final ItemStack[] GOLD_ARMOR = new ItemStack[]{new ItemStack(Items.GOLDEN_HELMET), new ItemStack(Items.GOLDEN_CHESTPLATE), new ItemStack(Items.GOLDEN_LEGGINGS), new ItemStack(Items.GOLDEN_BOOTS)};


    private static Map<ResourceLocation, Equipment> entityMap = Maps.newHashMap();

    public HCMobEquipment() {
        super("betterwithlib");
        enabledByDefault = false;
    }

    private static void pigman(EntityLivingBase entity) {
        if (entity.getRNG().nextFloat() < 0.05F) {
            entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
        }
        armor(entity, GOLD_ARMOR);
    }


    private static void skeleton(EntityLivingBase entity) {
        entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
    }

    private static void witherSkeleton(EntityLivingBase entity) {
        ItemStack stoneAxe = new ItemStack(Items.STONE_AXE);
        stoneAxe.addAttributeModifier("generic.attackDamage", new AttributeModifier("generic.attackDamage", 2, 0), EntityEquipmentSlot.MAINHAND);
        entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stoneAxe);
    }


    private static void zombie(EntityLivingBase entity) {
        armor(entity, IRON_ARMOR);
        if (entity.getRNG().nextFloat() < 0.05F) {
            int i = entity.getRNG().nextInt(3);
            if (i == 0) {
                entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD).copy());
            } else {
                entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL).copy());
            }
        }
    }

    private static void armor(EntityLivingBase entity, ItemStack[] armor) {
        if (entity.getRNG().nextFloat() < 0.018) {
            boolean flag = true;
            for (int s = 0; s < ARMOR_SLOTS.length; s++) {
                EntityEquipmentSlot slot = ARMOR_SLOTS[s];
                ItemStack current = entity.getItemStackFromSlot(slot);
                if (current.isEmpty()) {
                    if (!flag && entity.getRNG().nextFloat() < 0.1) {
                        continue;
                    }
                    flag = false;
                    entity.setItemStackToSlot(slot, armor[s].copy());
                }
            }
        }
    }

    @Override
    public void init(FMLInitializationEvent event) {
        addEquipmentOverride(new ResourceLocation("minecraft:zombie"), HCMobEquipment::zombie);
        addEquipmentOverride(new ResourceLocation("minecraft:zombie_villager"), HCMobEquipment::zombie);
        addEquipmentOverride(new ResourceLocation("minecraft:husk"), HCMobEquipment::zombie);
        addEquipmentOverride(new ResourceLocation("minecraft:skeleton"), HCMobEquipment::skeleton);
        addEquipmentOverride(new ResourceLocation("minecraft:zombie_pigman"), HCMobEquipment::pigman);
        addEquipmentOverride(new ResourceLocation("minecraft:wither_skeleton"), HCMobEquipment::witherSkeleton);
    }

    public void addEquipmentOverride(ResourceLocation mob, Equipment equipment) {
        if (loadPropBool("Override Equipment for " + mob.toString(), "", true)) {
            entityMap.put(mob, equipment);
        }
    }

    @SubscribeEvent
    public void onSetEquipment(EntitySetEquipmentEvent event) {
        EntityLivingBase entity = (EntityLivingBase) event.getEntity();
        ResourceLocation key = EntityList.getKey(entity);

        Equipment equipment = entityMap.get(key);
        if (equipment != null) {
            event.setCanceled(true);
            equipment.equip(entity);
        }
    }

    @Override
    public String getFeatureDescription() {
        return "Change the equipment that mobs spawn with";
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

    @FunctionalInterface
    private interface Equipment {
        void equip(EntityLivingBase entity);
    }
}
