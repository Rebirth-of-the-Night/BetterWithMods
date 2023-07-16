package betterwithmods.module.tweaks;

import betterwithmods.common.entity.EntityJungleSpider;
import betterwithmods.common.registry.block.recipe.BlockIngredient;
import betterwithmods.common.registry.block.recipe.BlockMaterialIngredient;
import betterwithmods.module.Feature;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.util.Iterator;
import java.util.List;

/**
 * Created by primetoxinz on 4/20/17.
 */
public class MobSpawning extends Feature {
    public static final SpawnWhitelist NETHER = new SpawnWhitelist();
    public static final SpawnWhitelist SLIME = new SpawnWhitelist();
    
    private boolean slime;
    private boolean nether;
    private boolean witches;
    private boolean jungleSpiders;

    @Override
    public void init(FMLInitializationEvent event) {
        if (nether) {
            NETHER.addBlock(Blocks.NETHERRACK);
            NETHER.addBlock(Blocks.NETHER_BRICK);
            NETHER.addBlock(Blocks.SOUL_SAND);
            NETHER.addBlock(Blocks.GRAVEL);
            NETHER.addBlock(Blocks.QUARTZ_BLOCK);
        }

        if (slime) {
            SLIME.addIngredient(new BlockMaterialIngredient(Material.GRASS, Material.ROCK, Material.GROUND));
        }

        Iterator<Biome> iterator = Biome.REGISTRY.iterator();
        while (iterator.hasNext()) {
            Biome biome = iterator.next();
            if (jungleSpiders && BiomeDictionary.hasType(biome, BiomeDictionary.Type.JUNGLE))
                EntityRegistry.addSpawn(EntityJungleSpider.class, 100, 1, 3, EnumCreatureType.MONSTER, biome);
            if (witches && !BiomeDictionary.hasType(biome, BiomeDictionary.Type.SWAMP))
                EntityRegistry.removeSpawn(EntityWitch.class, EnumCreatureType.MONSTER, biome);
        }
    }

    @Override
    public String getFeatureDescription() {
        return "Nether Mobs can only spawn on nether blocks and Slimes can only spawn on natural blocks. Also adjusts whether witches only spawn in swamps and if jungle spiders spawn in jungles.";
    }

    @Override
    public void setupConfig() {
        slime = loadPropBool("Limit Slime Spawning", "Slimes can only spawn on natural blocks", true);
        nether = loadPropBool("Limit Nether Spawning", "Nether Mobs can only spawn on nether blocks", true);
        witches = loadPropBool("Limit Witch Spawning", "Witches can only spawn in swamps", true);
        jungleSpiders = loadPropBool("Jungle Spider Spawning", "Jungle Spiders can spawn in jungles", true);
    }

    @SubscribeEvent
    public void denySlimeSpawns(LivingSpawnEvent.CheckSpawn event) {
        if (event.getResult() == Event.Result.ALLOW)
            return;
        if (!slime)
            return;
        World world = event.getWorld();
        if (world != null && world.provider.getDimensionType() == DimensionType.OVERWORLD) {
            if (event.getEntityLiving() instanceof EntitySlime) {
                BlockPos pos = new BlockPos(event.getEntity().posX, event.getEntity().posY - 1, event.getEntity().posZ);
                if (!SLIME.contains(world, pos, world.getBlockState(pos)))
                    event.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    public void denyNetherSpawns(LivingSpawnEvent.CheckSpawn event) {
        if (event.getResult() == Event.Result.ALLOW)
            return;
        if (!nether)
            return;
        World world = event.getWorld();
        if (world != null && world.provider.getDimensionType() == DimensionType.NETHER) {
            if (event.getEntityLiving().isCreatureType(EnumCreatureType.MONSTER, false)) {
                double monX = event.getEntity().posX;
                double monY = event.getEntity().posY;
                double monZ = event.getEntity().posZ;
                int x = MathHelper.floor(monX);
                int y = MathHelper.floor(monY);
                int z = MathHelper.floor(monZ);
                BlockPos pos = new BlockPos(x, y - 1, z);
                IBlockState state = world.getBlockState(pos);
                if (!world.isAirBlock(pos) && !NETHER.contains(world, pos, state))
                    event.setResult(Event.Result.DENY);
            }
        }
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

    public static class SpawnWhitelist {
        private final List<BlockIngredient> WHITELIST = Lists.newArrayList();

        public void addIngredient(BlockIngredient ingredient) {
            WHITELIST.add(ingredient);
        }

        public void addBlock(Block block) {
            WHITELIST.add(new BlockIngredient(Ingredient.fromItem(Item.getItemFromBlock(block))));
        }

        public void addBlock(ItemStack stack) {
            WHITELIST.add(new BlockIngredient(stack));
        }

        public boolean contains(World world, BlockPos pos, IBlockState state) {
            return WHITELIST.stream().anyMatch(i -> i.apply(world, pos, state));
        }
    }

}
