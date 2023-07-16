package betterwithmods.module.hardcore.world;

import betterwithmods.BWMod;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.world.BWMapGenVillage;
import betterwithmods.common.world.gen.village.*;
import betterwithmods.common.world.gen.village.field.BWField1;
import betterwithmods.common.world.gen.village.field.BWField2;
import betterwithmods.module.Feature;
import net.minecraft.block.BlockPlanks;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.terraingen.BiomeEvent;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by primetoxinz on 5/21/17.
 */
public class HCVillages extends Feature {
	public static AtomicInteger semiabandonedRadius = new AtomicInteger(), normalRadius = new AtomicInteger();

	public static boolean disableAllComplexBlocks;
	public static boolean disableVillagerSpawning;
	public static boolean disableIronGolems;

	@Override
	public String getFeatureDescription() {
		return "Makes it so villages with in the reaches of the spawn zone are abandoned and gradually gain more resources the further out. What this means to be gained by the player.";
	}

	@Override
	public void setupConfig() {
		semiabandonedRadius.set(loadPropInt("Semi-Abandoned Village Radius", "Block radius from 0,0 at which villages are now semi-abandoned, all villages inside this radius are abandoned", 2000));
		normalRadius.set(loadPropInt("Normal Village Radius", "Block radius from 0,0 at which villages are now normal, all villages in between this and semi-abandoned are semi-abandoned", 3000));
		disableAllComplexBlocks = loadPropBool("Disable All Complex Blocks", "Removes any and all useful blocks from villages, including ladders, stairs, tables and more", false);
		disableVillagerSpawning = loadPropBool("Replace Villager Spawning with Nitwits", "Replaces all villager spawns with Nitwits, which have no trades", true);
		disableIronGolems = loadPropBool("Disable Village Iron Golem Spawns", "WARNING: Stops all non-player created Iron Golem Spawns", true);
	}

	@Override
	public boolean requiresMinecraftRestartToEnable() {
		return true;
	}

	@Override
	public void init(FMLInitializationEvent event) {

		VillagerRegistry.instance().registerVillageCreationHandler(new BWField1());
		VillagerRegistry.instance().registerVillageCreationHandler(new BWField2());
		VillagerRegistry.instance().registerVillageCreationHandler(new Butchery());
		VillagerRegistry.instance().registerVillageCreationHandler(new Blacksmiths());
		VillagerRegistry.instance().registerVillageCreationHandler(new Church());
		VillagerRegistry.instance().registerVillageCreationHandler(new House());
		VillagerRegistry.instance().registerVillageCreationHandler(new Hut());
		VillagerRegistry.instance().registerVillageCreationHandler(new Hut2());
		VillagerRegistry.instance().registerVillageCreationHandler(new Library());

		MapGenStructureIO.registerStructure(BWMapGenVillage.AbandonedStart.class, new ResourceLocation(BWMod.MODID, "BWAbandonedStart").toString());
		MapGenStructureIO.registerStructureComponent(BWField1.class, new ResourceLocation(BWMod.MODID, "BWField1").toString());
		MapGenStructureIO.registerStructureComponent(BWField2.class, new ResourceLocation(BWMod.MODID, "BWField2").toString());
		MapGenStructureIO.registerStructureComponent(Blacksmiths.class, new ResourceLocation(BWMod.MODID, "Blacksmiths").toString());
		MapGenStructureIO.registerStructureComponent(Butchery.class, new ResourceLocation(BWMod.MODID, "Butchery").toString());
		MapGenStructureIO.registerStructureComponent(Church.class, new ResourceLocation(BWMod.MODID, "Church").toString());
		MapGenStructureIO.registerStructureComponent(House.class, new ResourceLocation(BWMod.MODID, "House").toString());
		MapGenStructureIO.registerStructureComponent(Hut.class, new ResourceLocation(BWMod.MODID, "Hut").toString());
		MapGenStructureIO.registerStructureComponent(Hut2.class, new ResourceLocation(BWMod.MODID, "Hut2").toString());
		MapGenStructureIO.registerStructureComponent(Library.class, new ResourceLocation(BWMod.MODID, "Library").toString());
		MapGenStructureIO.registerStructureComponent(Well.class, new ResourceLocation(BWMod.MODID, "Well").toString());

	}

	@SubscribeEvent
	public void biomeSpecificVillage(BiomeEvent.GetVillageBlockID event) {
		if (event.getOriginal() == BWMBlocks.WOOD_TABLE.getDefaultState()) {
			event.setReplacement(event.getOriginal().withProperty(BlockPlanks.VARIANT, plankFromBiome(event.getBiome())));
		}
		if (event.getOriginal() == Blocks.DIRT) {
			if (BiomeDictionary.hasType(event.getBiome(), BiomeDictionary.Type.SANDY)) {
				event.setReplacement(Blocks.SAND.getDefaultState());
			}
		}
	}

	public BlockPlanks.EnumType plankFromBiome(Biome biome) {
		if (biome == null)
			return BlockPlanks.EnumType.OAK;
		else if (BiomeDictionary.areSimilar(biome, Biomes.TAIGA)) {
			return BlockPlanks.EnumType.SPRUCE;
		} else if (BiomeDictionary.areSimilar(biome, Biomes.FOREST)) {
			return BlockPlanks.EnumType.OAK;
		} else if (BiomeDictionary.areSimilar(biome, Biomes.ROOFED_FOREST)) {
			return BlockPlanks.EnumType.OAK;
		} else if (BiomeDictionary.areSimilar(biome, Biomes.JUNGLE)) {
			return BlockPlanks.EnumType.JUNGLE;
		} else if (BiomeDictionary.areSimilar(biome, Biomes.BIRCH_FOREST)) {
			return BlockPlanks.EnumType.BIRCH;
		} else if (BiomeDictionary.areSimilar(biome, Biomes.SAVANNA) || BiomeDictionary.areSimilar(biome, Biomes.DESERT)) {
			return BlockPlanks.EnumType.ACACIA;
		} else {

			return BlockPlanks.EnumType.OAK;
		}
	}

	@SubscribeEvent
	public void onGenerate(InitMapGenEvent event) {
		if (event.getType() == InitMapGenEvent.EventType.VILLAGE) {
			event.setNewGen(new BWMapGenVillage());
		}
	}

	//hack to stop iron golem spawning in villages, also will stop any other spawning
	@SubscribeEvent
	public void onEntityJoin(EntityJoinWorldEvent event) {
		if (!disableIronGolems)
			return;
		if (event.getEntity() instanceof EntityIronGolem) {
			EntityIronGolem golem = (EntityIronGolem) event.getEntity();
			if (!golem.isPlayerCreated()) {
				event.setCanceled(true);
			}
		}
	}

	@Override
	public boolean hasTerrainSubscriptions() {
		return true;
	}

	@Override
	public boolean hasSubscriptions() {
		return true;
	}
}


