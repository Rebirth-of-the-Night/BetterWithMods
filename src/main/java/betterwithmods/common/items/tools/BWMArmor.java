package betterwithmods.common.items.tools;

import betterwithmods.BWMod;
import betterwithmods.api.client.IColorable;
import betterwithmods.client.BWCreativeTabs;
import betterwithmods.client.ColorHandlers;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class BWMArmor extends ItemArmor implements IColorable {
    private String name;

    public BWMArmor(ArmorMaterial material, EntityEquipmentSlot equipmentSlotIn, String name) {
        super(material, 2, equipmentSlotIn);
        this.name = name;
        this.setCreativeTab(BWCreativeTabs.BWTAB);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        return new ResourceLocation(BWMod.MODID, String.format("textures/models/armor/%s_layer_%s%s.png", this.name, (this.armorType.getSlotIndex() == 2 ? "2" : "1"), type != null ? "_" + type : "")).toString();
    }

    @Override
    public int getColor(ItemStack stack) {
        NBTTagCompound nbttagcompound = stack.getTagCompound();
        if (nbttagcompound != null && nbttagcompound.hasKey("display")) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");
            if (nbttagcompound1.hasKey("color", 3)) {
                return nbttagcompound1.getInteger("color");
            }
        }
        return  0x00FFFFFF;
//        return 0xa06540;
    }

    @Override
    public void removeColor(ItemStack stack) {
        NBTTagCompound nbttagcompound = stack.getTagCompound();
        if (nbttagcompound != null) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");
            if (nbttagcompound1.hasKey("color")) {
                nbttagcompound1.removeTag("color");
            }
        }
    }

    @Override
    public void setColor(ItemStack stack, int color) {
        NBTTagCompound nbttagcompound = stack.getTagCompound();
        if (nbttagcompound == null) {
            nbttagcompound = new NBTTagCompound();
            stack.setTagCompound(nbttagcompound);
        }
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("display");
        if (!nbttagcompound.hasKey("display", 10)) {
            nbttagcompound.setTag("display", nbttagcompound1);
        }
        nbttagcompound1.setInteger("color", color);
    }

    @Override
    public boolean hasColor(ItemStack stack) {
        NBTTagCompound nbttagcompound = stack.getTagCompound();
        return nbttagcompound != null && nbttagcompound.hasKey("display", 10) ? nbttagcompound.getCompoundTag("display").hasKey("color", 3) : false;
    }

    @Override
    public boolean hasOverlay(ItemStack stack) {
        return getColor(stack) != 0x00FFFFFF;
    }

    @Override
    public IItemColor getColorHandler() {
        return ColorHandlers.armor;
    }
}
