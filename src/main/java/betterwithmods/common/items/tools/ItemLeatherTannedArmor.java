package betterwithmods.common.items.tools;

import betterwithmods.client.BWCreativeTabs;
import betterwithmods.common.BWOreDictionary;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.oredict.OreDictionary;

public class ItemLeatherTannedArmor extends BWMArmor {
    private static final ArmorMaterial LEATHER_TANNED = EnumHelper.addArmorMaterial("leather_tanned", "betterwithmods:leather_tanned", 10, new int[]{1, 2, 3, 1}, 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F);

    public ItemLeatherTannedArmor(EntityEquipmentSlot equipmentSlotIn) {
        super(LEATHER_TANNED, equipmentSlotIn, "leather_tanned");
        this.setCreativeTab(BWCreativeTabs.BWTAB);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return BWOreDictionary.listContains(repair, OreDictionary.getOres("hideTanned")) || super.getIsRepairable(toRepair, repair);
    }

}