package betterwithmods.common.world.gen.village;

import betterwithmods.common.world.BWMapGenVillage;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;

import java.util.List;
import java.util.Random;

/**
 * Created by primetoxinz on 5/25/17.
 */
public class Well extends StructureVillagePieces.Start {
    protected BWMapGenVillage.VillageStatus status;

    public Well() {
    }

    public Well(BWMapGenVillage.VillageStatus status, BiomeProvider chunkManagerIn, int p_i2104_2_, Random rand, int p_i2104_4_, int p_i2104_5_, List<StructureVillagePieces.PieceWeight> p_i2104_6_, int p_i2104_7_) {
        super(chunkManagerIn, p_i2104_2_, rand, p_i2104_4_, p_i2104_5_, p_i2104_6_, p_i2104_7_);
        this.status = status;
    }

    @Override
    public void buildComponent(StructureComponent componentIn, List<StructureComponent> listIn, Random rand) {
        super.buildComponent(componentIn, listIn, rand);
    }

}