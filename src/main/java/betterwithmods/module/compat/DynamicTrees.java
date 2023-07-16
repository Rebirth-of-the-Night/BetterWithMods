package betterwithmods.module.compat;

import betterwithmods.api.util.IWood;
import betterwithmods.api.util.IWoodProvider;
import betterwithmods.common.BWOreDictionary;
import betterwithmods.common.items.ItemBark;
import betterwithmods.common.registry.Wood;
import betterwithmods.module.CompatFeature;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class DynamicTrees extends CompatFeature {
    public DynamicTrees() {
        super("dynamictrees");
    }

    @GameRegistry.ObjectHolder("dynamictrees:oakbranch")
    private static Block oakbranch;

    @GameRegistry.ObjectHolder("dynamictrees:sprucebranch")
    private static Block sprucebranch;

    @GameRegistry.ObjectHolder("dynamictrees:birchbranch")
    private static Block birchbranch;

    @GameRegistry.ObjectHolder("dynamictrees:junglebranch")
    private static Block junglebranch;

    @GameRegistry.ObjectHolder("dynamictrees:acaciabranch")
    private static Block acaciabranch;

    @GameRegistry.ObjectHolder("dynamictrees:darkoakbranch")
    private static Block darkoakbranch;

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        BWOreDictionary.woodProviders.add(new DynamicWoodProvider(oakbranch, new ItemStack(Blocks.LOG, 1, 0), new ItemStack(Blocks.PLANKS, 1, 0), ItemBark.getStack("oak", 1)));
        BWOreDictionary.woodProviders.add(new DynamicWoodProvider(sprucebranch, new ItemStack(Blocks.LOG, 1, 1), new ItemStack(Blocks.PLANKS, 1, 1), ItemBark.getStack("spruce", 1)));
        BWOreDictionary.woodProviders.add(new DynamicWoodProvider(birchbranch, new ItemStack(Blocks.LOG, 1, 2), new ItemStack(Blocks.PLANKS, 1, 2), ItemBark.getStack("birch", 1)));
        BWOreDictionary.woodProviders.add(new DynamicWoodProvider(junglebranch, new ItemStack(Blocks.LOG, 1, 3), new ItemStack(Blocks.PLANKS, 1, 3), ItemBark.getStack("jungle", 1)));
        BWOreDictionary.woodProviders.add(new DynamicWoodProvider(acaciabranch, new ItemStack(Blocks.LOG2, 1, 0), new ItemStack(Blocks.PLANKS, 1, 4), ItemBark.getStack("acacia", 1)));
        BWOreDictionary.woodProviders.add(new DynamicWoodProvider(darkoakbranch, new ItemStack(Blocks.LOG2, 1, 1), new ItemStack(Blocks.PLANKS, 1, 5), ItemBark.getStack("dark_oak", 1)));
    }


    public static class DynamicWoodProvider implements IWoodProvider {

        private Block block;
        private ItemStack log;
        private ItemStack planks;
        private ItemStack bark;

        DynamicWoodProvider(Block block, ItemStack log, ItemStack planks, ItemStack bark) {
            this.block = block;
            this.log = log;
            this.planks = planks;
            this.bark = bark;
        }

        @Override
        public boolean match(IBlockState state) {
            return state.getBlock() == block;
        }

        @Override
        public IWood getWood(IBlockState state) {
            PropertyInteger radius = (PropertyInteger) state.getPropertyKeys().stream().filter(p -> p.getName().equalsIgnoreCase("radius")).findFirst().orElse(null);
            int r = 1;
            if (radius != null) {
                r = state.getValue(radius);
            }
            int finalR = r;
            return new Wood(log, planks, bark) {
                @Override
                public ItemStack getPlank(int count) {
                    int c = (finalR * count) / 4;
                    return super.getPlank(c);
                }
            };
        }
    }
}
