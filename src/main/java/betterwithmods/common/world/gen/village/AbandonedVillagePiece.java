package betterwithmods.common.world.gen.village;

import betterwithmods.common.world.BWMapGenVillage;
import betterwithmods.module.hardcore.world.HCVillages;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import java.util.Random;

/**
 * Created by primetoxinz on 5/21/17.
 */
public abstract class AbandonedVillagePiece extends StructureVillagePieces.Village implements VillagerRegistry.IVillageCreationHandler {
    @GameRegistry.ObjectHolder("minecraft:nitwit")
    public final static VillagerRegistry.VillagerProfession nitwit = null;
    protected BWMapGenVillage.VillageStatus status = BWMapGenVillage.VillageStatus.NORMAL;
    private int villagersSpawned;

    public AbandonedVillagePiece() {
    }


    public AbandonedVillagePiece(StructureVillagePieces.Start start, int type) {
        super(start, type);
        if (start instanceof Well) {
            this.status = ((Well) start).status;
        }
    }

    @Override
    protected void spawnVillagers(World worldIn, StructureBoundingBox structurebb, int x, int y, int z, int count) {

        if (this.villagersSpawned < count) {
            for (int i = this.villagersSpawned; i < count; ++i) {
                int j = this.getXWithOffset(x + i, z);
                int k = this.getYWithOffset(y);
                int l = this.getZWithOffset(x + i, z);

                if (!structurebb.isVecInside(new BlockPos(j, k, l))) {
                    break;
                }

                ++this.villagersSpawned;
                EntityVillager entityvillager = new EntityVillager(worldIn);
                entityvillager.setLocationAndAngles((double) j + 0.5D, (double) k, (double) l + 0.5D, 0.0F, 0.0F);
                entityvillager.setProfession(this.chooseForgeProfession(i, entityvillager.getProfessionForge()));
                entityvillager.finalizeMobSpawn(worldIn.getDifficultyForLocation(new BlockPos(entityvillager)), null, false);
                worldIn.spawnEntity(entityvillager);
            }
        }
    }

    @Override
    public void placeTorch(World p_189926_1_, EnumFacing p_189926_2_, int p_189926_3_, int p_189926_4_, int p_189926_5_, StructureBoundingBox p_189926_6_) {
        //NO-OP
    }

    @Override
    public abstract StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random, int size);

    @Override
    protected VillagerRegistry.VillagerProfession chooseForgeProfession(int count, VillagerRegistry.VillagerProfession prof) {
        if (HCVillages.disableVillagerSpawning) {
            switch (status) {
                case NORMAL:
                    return super.chooseForgeProfession(count, prof);
                case SEMIABANDONED:
                    VillagerRegistry.VillagerProfession profession = super.chooseForgeProfession(count, prof);
                    String name = profession.getRegistryName().toString();
                    if (name.equals("minecraft:priest") || name.equals("minecraft:librarian"))
                        return nitwit;
                    else
                        return profession;
                default:
                    return nitwit;

            }
        } else {
            return super.chooseForgeProfession(count, prof);
        }
    }


}
