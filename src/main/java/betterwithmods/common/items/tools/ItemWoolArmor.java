package betterwithmods.common.items.tools;

import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.util.EnumHelper;

public class ItemWoolArmor extends BWMArmor {
    private static final ArmorMaterial WOOL = EnumHelper.addArmorMaterial("wool", "betterwithmods:wool", 5, new int[]{1, 2, 3, 0}, 0, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F);

    public ItemWoolArmor(EntityEquipmentSlot equipmentSlotIn) {
        super(WOOL, equipmentSlotIn, "wool");
    }
}
