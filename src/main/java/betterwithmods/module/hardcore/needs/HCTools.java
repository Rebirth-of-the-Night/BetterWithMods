package betterwithmods.module.hardcore.needs;

import betterwithmods.common.BWMItems;
import betterwithmods.common.BWMRecipes;
import betterwithmods.module.ConfigHelper;
import betterwithmods.module.Feature;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by primetoxinz on 4/20/17.
 */
public class HCTools extends Feature {


    public static final HashMap<Item.ToolMaterial, ToolMaterialOverride> OVERRIDES = Maps.newHashMap();
    public static boolean removeLowTools, perToolOverrides;
    public static int noHungerThreshold;
    public static int noDamageThreshold;

    private static Set<Item> TOOLS;

    private static void removeLowTierToolRecipes() {
        BWMRecipes.removeRecipe(new ItemStack(Items.WOODEN_AXE, OreDictionary.WILDCARD_VALUE));
        BWMRecipes.removeRecipe(new ItemStack(Items.WOODEN_HOE, OreDictionary.WILDCARD_VALUE));
        BWMRecipes.removeRecipe(new ItemStack(Items.WOODEN_SWORD, OreDictionary.WILDCARD_VALUE));
        BWMRecipes.removeRecipe(new ItemStack(Items.STONE_HOE, OreDictionary.WILDCARD_VALUE));
        BWMRecipes.removeRecipe(new ItemStack(Items.STONE_SWORD, OreDictionary.WILDCARD_VALUE));
    }

    @Override
    public String getFeatureDescription() {
        return "Overhaul the durability of tools to be more rewarding when reaching the next level. Completely disables wooden tools (other than pick) by default.";
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        if (removeLowTools) {
            removeLowTierToolRecipes();
        }
    }

    @Override
    public void init(FMLInitializationEvent event) {

        TOOLS = new HashSet<>(Sets.newHashSet(
                BWMItems.STEEL_AXE, BWMItems.STEEL_BATTLEAXE, BWMItems.STEEL_HOE, BWMItems.STEEL_SWORD, BWMItems.STEEL_PICKAXE, BWMItems.STEEL_SWORD, BWMItems.STEEL_MATTOCK,
                Items.DIAMOND_PICKAXE, Items.DIAMOND_AXE, Items.DIAMOND_SWORD, Items.DIAMOND_SHOVEL, Items.DIAMOND_HOE,
                Items.IRON_PICKAXE, Items.IRON_AXE, Items.IRON_SWORD, Items.IRON_SHOVEL, Items.IRON_HOE,
                Items.STONE_PICKAXE, Items.STONE_AXE, Items.STONE_SWORD, Items.STONE_SHOVEL, Items.STONE_HOE,
                Items.GOLDEN_PICKAXE, Items.GOLDEN_AXE, Items.GOLDEN_SWORD, Items.GOLDEN_SHOVEL, Items.GOLDEN_HOE,
                Items.WOODEN_PICKAXE, Items.WOODEN_AXE, Items.WOODEN_SWORD, Items.WOODEN_SHOVEL, Items.WOODEN_HOE
        ));

        OVERRIDES.put(Item.ToolMaterial.WOOD, new ToolMaterialOverride("wood", 1, 1.01F, 0).addClassOverride("shovel", 10));
        OVERRIDES.put(Item.ToolMaterial.STONE, new ToolMaterialOverride("stone", 6, 1.01F, 5).addClassOverride("shovel", 50).addClassOverride("axe", 50));
        OVERRIDES.put(Item.ToolMaterial.IRON, new ToolMaterialOverride("iron", 500, 6.0F, 14));
        OVERRIDES.put(Item.ToolMaterial.DIAMOND, new ToolMaterialOverride("diamond", 1561, 8.0F, 14));
        OVERRIDES.put(Item.ToolMaterial.GOLD, new ToolMaterialOverride("gold", 32, 12.0F, 22));
        OVERRIDES.put(BWMItems.SOULFORGED_STEEL, new ToolMaterialOverride(BWMItems.SOULFORGED_STEEL));

        TOOLS.forEach(this::loadToolMaterialOverride);
    }

    private Item.ToolMaterial getMaterial(Item tool) {
        if (tool instanceof ItemTool) {
            return ((ItemTool) tool).toolMaterial;
        } else if (tool instanceof ItemHoe) {
            return ((ItemHoe) tool).toolMaterial;
        } else if (tool instanceof ItemSword) {
            return ((ItemSword) tool).material;
        }
        return null;
    }


    private Set<String> getToolClass(ItemStack stack) {
        Item item = stack.getItem();
        Set<String> classes = Sets.newHashSet();
        classes.addAll(item.getToolClasses(stack));
        if (item instanceof ItemSword) {
            classes.add("sword");
        } else if (item instanceof ItemHoe) {
            classes.add("hoe");
        }
        return classes;
    }

    private void loadToolMaterialOverride(Item tool) {
        Item.ToolMaterial material = getMaterial(tool);
        if (material != null) {
            ToolMaterialOverride override = OVERRIDES.get(material);
            material.maxUses = override.getMaxUses();
            material.efficiency = override.getEfficiency();
            material.enchantability = override.getEnchantability();
            if (tool instanceof ItemTool) {
                ((ItemTool) tool).efficiency = material.getEfficiency();
            }

            ItemStack stack = new ItemStack(tool);

            Set<String> classes = getToolClass(stack);
            if (!classes.isEmpty()) {
                classes.stream().findFirst().ifPresent(c -> tool.setMaxDamage(override.getMaxUses(c)));
            } else {
                tool.setMaxDamage(override.getMaxUses());
            }
        }

    }

    @Override
    public void setupConfig() {
        removeLowTools = loadPropBool("Remove cheapest tools", "The minimum level of the hoe and the sword is iron, and the axe needs at least stone.", true);
        noHungerThreshold = loadPropInt("No Exhaustion Harvest Level", "When destroying a 0 hardness block with a tool of this harvest level or higher, no exhaustion is applied", Item.ToolMaterial.IRON.getHarvestLevel());
        noDamageThreshold = loadPropInt("No Durability Damage Harvest Level", "When destroying a 0 hardness block with a tool of this harvest level or higher, no durability damage is applied", Item.ToolMaterial.DIAMOND.getHarvestLevel());
        perToolOverrides = loadPropBool("Change Durability per Tool", "Allow configuring tool durability for each class", true);
    }

    @Override
    public boolean requiresMinecraftRestartToEnable() {
        return true;
    }

    @SubscribeEvent
    public void onHitEntity(LivingAttackEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            ItemStack stack = player.getHeldItemMainhand();
            breakTool(stack, player);
        }
    }

    @SubscribeEvent
    public void onUseHoe(UseHoeEvent event) {
        breakTool(event.getCurrent(), event.getEntityPlayer());
    }

    @SubscribeEvent
    public void onBreaking(BlockEvent.BreakEvent event) {
        EntityPlayer player = event.getPlayer();
        ItemStack stack = player.getHeldItemMainhand();
        breakTool(stack, player);
    }

    private void breakTool(ItemStack stack, EntityPlayer player) {
        if (stack.isEmpty()) return;
        if (stack.getMaxDamage() == 1) {
            destroyItem(stack, player);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void harvestGarbage(BlockEvent.BreakEvent event) {
        EntityPlayer player = event.getPlayer();
        if (event.isCanceled() || player == null || player.isCreative())
            return;
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        IBlockState state = world.getBlockState(pos);
        ItemStack stack = player.getHeldItemMainhand();
        String tooltype = state.getBlock().getHarvestTool(state);
        if (tooltype != null && state.getBlockHardness(world, pos) <= 0 && stack.getItem().getHarvestLevel(stack, tooltype, player, state) < noDamageThreshold)
            stack.damageItem(1, player); //Make 0 hardness blocks damage tools that are not over some harvest level
    }

    private void destroyItem(ItemStack stack, EntityLivingBase entity) {
        int damage = stack.getMaxDamage();
        stack.damageItem(damage, entity);
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

    public class ToolMaterialOverride {
        private final String name;
        public Map<String, Integer> toolClassOverrides = Maps.newHashMap();
        private int maxUses;
        private float efficiencyOnProperMaterial;
        private int enchantability;

        public ToolMaterialOverride(Item.ToolMaterial material) {
            this(material.name().toLowerCase(), material.getMaxUses(), material.getEfficiency(), material.getEnchantability());
        }

        ToolMaterialOverride(String name, int maxUses, float efficiencyOnProperMaterial, int enchantability) {
            this.name = name;
            this.maxUses = ConfigHelper.loadPropInt("Max Durability", configCategory + "." + name, "", maxUses);
            this.efficiencyOnProperMaterial = efficiencyOnProperMaterial;
            this.enchantability = enchantability;
        }

        int getMaxUses(String toolClass) {
            int use = getMaxUses();
            if (perToolOverrides) {
                use = ConfigHelper.loadPropInt("Max Durability: " + toolClass, configCategory + "." + name, "", toolClassOverrides.getOrDefault(toolClass, use));
            }
            return Math.max(1, use - 1); //subtract one from the max durability because the tool doesn't break until -1
        }

        int getMaxUses() {
            return maxUses;
        }

        float getEfficiency() {
            return efficiencyOnProperMaterial;
        }

        int getEnchantability() {
            return enchantability;
        }

        ToolMaterialOverride addClassOverride(String toolClass, int durability) {
            toolClassOverrides.put(toolClass, durability);
            return this;
        }

    }

}
