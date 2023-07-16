package betterwithmods.module.hardcore.world;

import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWMItems;
import betterwithmods.common.blocks.mechanical.BlockMechMachines;
import betterwithmods.common.items.ItemMaterial;
import betterwithmods.module.CompatFeature;
import betterwithmods.util.item.Stack;
import betterwithmods.util.item.StackMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;

/**
 * @author Koward
 */
public class HCBuoy extends CompatFeature {
    public static final StackMap<Float> buoyancy = new StackMap<>(-1.0F);

    public HCBuoy() {
        super("hardcorebuoy");
    }

    public static float getBuoyancy(ItemStack stack) {
        return buoyancy.get(stack);
    }

    public static void initBuoyancy() {
        //Blocks
        buoyancy.put(BWMBlocks.WOODEN_AXLE, 1.0F);
        buoyancy.put(BWMBlocks.PUMP, 0.0F);
        buoyancy.put(BWMBlocks.SAW, 1.0F);
        buoyancy.put(BWMBlocks.PLATFORM, 1.0F);
        buoyancy.put(BWMBlocks.WOLF, 1.0F);
        buoyancy.put(BWMBlocks.HEMP, 1.0F);
        buoyancy.put(BWMBlocks.ROPE, 1.0F);
        buoyancy.put(BWMBlocks.WOODEN_GEARBOX, 1.0F);
        buoyancy.put(BWMBlocks.BELLOWS, 1.0F);
        buoyancy.put(BWMBlocks.VASE, 1.0F);
        buoyancy.put(BWMBlocks.GRATE, 1.0F);
        buoyancy.put(BWMBlocks.URN, 1.0F);
        buoyancy.put(BlockMechMachines.getStack(BlockMechMachines.EnumType.HOPPER), 1.0F);
        buoyancy.put(BlockMechMachines.getStack(BlockMechMachines.EnumType.PULLEY), 1.0F);

        //Items
        buoyancy.put(BWMItems.MATERIAL, ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.HEMP).getMetadata(), 1.0F);
        buoyancy.put(BWMItems.MATERIAL, ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.HEMP_FIBERS).getMetadata(), 1.0F);
        buoyancy.put(BWMItems.MATERIAL, ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.SCOURED_LEATHER).getMetadata(), 1.0F);
        buoyancy.put(BWMItems.MATERIAL, ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.DUNG).getMetadata(), 1.0F);
        buoyancy.put(BWMItems.MATERIAL, ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.TANNED_LEATHER).getMetadata(), 1.0F);
        buoyancy.put(BWMItems.MATERIAL, ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.LEATHER_STRAP).getMetadata(), 1.0F);
        buoyancy.put(BWMItems.MATERIAL, ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.LEATHER_BELT).getMetadata(), 1.0F);
        buoyancy.put(BWMItems.MATERIAL, ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.WOOD_BLADE).getMetadata(), 1.0F);
        buoyancy.put(BWMItems.MATERIAL, ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.GLUE).getMetadata(), 0.0F);
        buoyancy.put(BWMItems.MATERIAL, ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.TALLOW).getMetadata(), 1.0F);
        buoyancy.put(BWMItems.MATERIAL, ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.HAFT).getMetadata(), 1.0F);
        buoyancy.put(BWMItems.MATERIAL, ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.LEATHER_CUT).getMetadata(), 1.0F);
        buoyancy.put(BWMItems.MATERIAL, ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.TANNED_LEATHER_CUT).getMetadata(), 1.0F);
        buoyancy.put(BWMItems.MATERIAL, ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.SCOURED_LEATHER_CUT).getMetadata(), 1.0F);
        buoyancy.put(BWMItems.MATERIAL, ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.SOUL_FLUX).getMetadata(), 1.0F);
        buoyancy.put(BWMItems.MATERIAL, ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.SAWDUST).getMetadata(), 1.0F);
        buoyancy.put(BWMItems.MATERIAL, ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.SOUL_DUST).getMetadata(), 1.0F);
        buoyancy.put(BWMItems.MATERIAL, ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.NETHER_SLUDGE).getMetadata(), 1.0F);
        buoyancy.put(BWMItems.DYNAMITE, 1.0F);
        buoyancy.put(BWMItems.DYNAMITE_BUNDLE, 1.0F);
        buoyancy.put(BWMItems.STUMP_REMOVER, 1.0F);
        buoyancy.put(BWMItems.CREEPER_OYSTER, 1.0F);
        buoyancy.put(BWMItems.AXLE_GENERATOR, 1.0F);
        buoyancy.put(BWMItems.DONUT, 1.0F);
        buoyancy.put(BWMItems.ARCANE_SCROLL, 1.0F);

        

    }

    @Override
    public String getFeatureDescription() {
        return "Add values for BWM items to the Hardcore Buoy mod.";
    }

    @Override
    public void init(FMLInitializationEvent event) {
        initBuoyancy();

        for(Stack stack: buoyancy.keySet()) {
            NBTTagCompound tag = new NBTTagCompound();
            NBTTagCompound s = stack.getItemStack().serializeNBT();
            tag.setTag("stack",s);
            tag.setFloat("value",buoyancy.get(stack));
            FMLInterModComms.sendMessage("hardcorebuoy","buoy", tag);
        }
    }


    @Override
    public boolean requiresMinecraftRestartToEnable() {
        return true;
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }
}
