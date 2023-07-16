package betterwithmods.common.items;

import betterwithmods.api.IMultiLocations;
import betterwithmods.client.BWCreativeTabs;
import betterwithmods.common.BWMItems;
import betterwithmods.util.StackIngredient;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ItemMaterial extends Item implements IMultiLocations {
    public ItemMaterial() {
        super();
        this.setCreativeTab(BWCreativeTabs.BWTAB);
        this.setHasSubtypes(true);
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        if (EnumMaterial.VALUES[stack.getMetadata()] == EnumMaterial.DIAMOND_INGOT && playerIn != null) {
            BlockPos pos = playerIn.getPosition().up();
            worldIn.playSound(null, playerIn.getPosition(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 1.0f, 1.0f);
            worldIn.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 0.1f, false);
        }
        super.onCreated(stack, worldIn, playerIn);
    }

    @Override
    public int getItemBurnTime(ItemStack stack) {
        switch (EnumMaterial.VALUES[stack.getMetadata()]) {
            case GEAR:
                return 18;
            case NETHERCOAL:
                return 3200;
            case SAWDUST:
                return 25;
            case WINDMILL_BLADE:
                return 75;
            case WOOD_BLADE:
                return 37;
            case HAFT:
                return 150;
            default:
                return -1;
        }
    }

    public static ItemStack getMaterial(EnumMaterial material) {
        return getMaterial(material, 1);
    }

    public static ItemStack getMaterial(EnumMaterial material, int count) {
        return new ItemStack(BWMItems.MATERIAL, count, material.getMetadata());
    }


    public static Ingredient getIngredient(EnumMaterial material) {
        return getIngredient(material, 1);
    }

    public static Ingredient getIngredient(EnumMaterial material, int count) {
        return StackIngredient.fromStacks(getMaterial(material, count));
    }


    @Override
    public String[] getLocations() {
        List<String> names = new ArrayList<>();
        for (EnumMaterial material : EnumMaterial.values()) {
            names.add(material.getName());
        }
        return names.toArray(new String[EnumMaterial.values().length]);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (this.isInCreativeTab(tab))
            for (EnumMaterial material : EnumMaterial.values()) {
                items.add(getMaterial(material));
            }
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        return super.getTranslationKey() + "." + EnumMaterial.VALUES[stack.getMetadata()].getName();
    }


    public enum EnumMaterial {
        GEAR,
        NETHERCOAL,
        HEMP,
        HEMP_FIBERS,
        HEMP_CLOTH,
        DUNG,
        TANNED_LEATHER,
        SCOURED_LEATHER,
        LEATHER_STRAP,
        LEATHER_BELT,
        WOOD_BLADE,
        WINDMILL_BLADE,
        GLUE,
        TALLOW,
        INGOT_STEEL,
        GROUND_NETHERRACK,
        HELLFIRE_DUST,
        CONCENTRATED_HELLFIRE,
        COAL_DUST,
        FILAMENT,
        POLISHED_LAPIS,
        POTASH,
        SAWDUST,
        SOUL_DUST,
        SCREW,
        BRIMSTONE,
        NITER,
        ELEMENT,
        FUSE,
        BLASTING_OIL,
        NUGGET_STEEL,
        LEATHER_CUT,
        TANNED_LEATHER_CUT,
        SCOURED_LEATHER_CUT,
        REDSTONE_LATCH,
        NETHER_SLUDGE,
        HAFT,
        CHARCOAL_DUST,
        SOUL_FLUX,
        ENDER_SLAG,
        ENDER_OCULAR,
        PADDING,
        ARMOR_PLATE,
        BROADHEAD,
        COCOA_POWDER,
        DIAMOND_INGOT,
        DIAMOND_NUGGET,
        CHAIN_MAIL,
        STEEL_GEAR,
        STEEL_SPRING,
        SOAP,
        PLATE_STEEL,
        WITCH_WART,
        MYSTERY_GLAND,
        POISON_SAC;


        public final static EnumMaterial[] VALUES = values();

        public int getMetadata() {
            return this.ordinal();
        }

        public String getName() {
            return this.name().toLowerCase();
        }
    }
}
