package betterwithmods.module.tweaks;

import betterwithmods.BWMod;
import betterwithmods.common.world.BWMapGenMineshaft;
import betterwithmods.common.world.BWStructureMineshaftPieces;
import betterwithmods.module.Feature;
import net.minecraft.block.BlockNewLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockRail;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureMineshaftPieces;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MineshaftGeneration extends Feature {
    public static IMineshaftBlockState roomFloor;
    public static IMineshaftBlockState rail;
    public static IMineshaftBlockState planks;
    public static IMineshaftBlockState supports;

    @Override
    public String getFeatureDescription() {
        return "Mineshafts now generate with logs instead of fences";
    }

    @Override
    public boolean requiresMinecraftRestartToEnable() {
        return true;
    }

    @Override
    public boolean hasTerrainSubscriptions() {
        return true;
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

    @Override
    public void init(FMLInitializationEvent event) {
        MapGenStructureIO.registerStructure(BWMapGenMineshaft.BWStructureMineshaftStart.class, new ResourceLocation(BWMod.MODID, "BWMineshaftStart").toString());
        MapGenStructureIO.registerStructureComponent(BWStructureMineshaftPieces.Corridor.class, new ResourceLocation(BWMod.MODID, "BWMSCorridor").toString());
        MapGenStructureIO.registerStructureComponent(BWStructureMineshaftPieces.Cross.class, new ResourceLocation(BWMod.MODID, "BWMSCrossing").toString());
        MapGenStructureIO.registerStructureComponent(BWStructureMineshaftPieces.Room.class, new ResourceLocation(BWMod.MODID, "BWMSRoom").toString());
        MapGenStructureIO.registerStructureComponent(BWStructureMineshaftPieces.Stairs.class, new ResourceLocation(BWMod.MODID, "BWMSStairs").toString());

        roomFloor = piece -> Blocks.DIRT.getDefaultState();
        rail = piece -> Blocks.RAIL.getDefaultState().withProperty(BlockRail.SHAPE, BlockRailBase.EnumRailDirection.NORTH_SOUTH);
        planks = piece -> getPlankForType(piece.mineShaftType);
        supports = piece -> getSupportForType(piece.mineShaftType);
    }

    @SubscribeEvent
    public void onGenerate(InitMapGenEvent event) {
        if (event.getType() == InitMapGenEvent.EventType.MINESHAFT) {
            event.setNewGen(new BWMapGenMineshaft());
        }
    }

    public IBlockState getPlankForType(MapGenMineshaft.Type mineShaftType)
    {
        switch (mineShaftType)
        {
            case NORMAL:
            default:
                return Blocks.PLANKS.getDefaultState();
            case MESA:
                return Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, BlockPlanks.EnumType.DARK_OAK);
        }
    }

    public IBlockState getSupportForType(MapGenMineshaft.Type mineShaftType)
    {
        switch (mineShaftType)
        {
            case NORMAL:
            default:
                return Blocks.LOG.getDefaultState();
            case MESA:
                return Blocks.LOG2.getDefaultState().withProperty(BlockNewLog.VARIANT, BlockPlanks.EnumType.DARK_OAK);
        }
    }

    public interface IMineshaftBlockState
    {
        IBlockState getBlockState(StructureMineshaftPieces.Peice piece); //For evil plans
    }
}
