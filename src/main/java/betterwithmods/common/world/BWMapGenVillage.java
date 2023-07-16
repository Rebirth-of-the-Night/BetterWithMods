package betterwithmods.common.world;

import betterwithmods.common.world.gen.village.AbandonedVillagePiece;
import betterwithmods.common.world.gen.village.Well;
import betterwithmods.common.world.gen.village.field.BWFieldBase;
import betterwithmods.module.GlobalConfig;
import betterwithmods.module.hardcore.world.HCVillages;
import com.google.common.collect.Lists;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import net.minecraft.world.gen.structure.StructureVillagePieces;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static net.minecraft.world.gen.structure.StructureVillagePieces.Road;
import static net.minecraft.world.gen.structure.StructureVillagePieces.Torch;

/**
 * Created by primetoxinz on 5/21/17.
 */
public class BWMapGenVillage extends MapGenVillage {
    public static List<StructureVillagePieces.PieceWeight> getPiecesAbandoned(Random random, int size) {
        List<StructureVillagePieces.PieceWeight> list = Lists.<StructureVillagePieces.PieceWeight>newArrayList();
        net.minecraftforge.fml.common.registry.VillagerRegistry.addExtraVillageComponents(list, random, size);

        list.removeIf(pieceWeight -> (pieceWeight).villagePiecesLimit == 0);
        return list.stream().filter(piece -> AbandonedVillagePiece.class.isAssignableFrom(piece.villagePieceClass) || Well.class.isAssignableFrom(piece.villagePieceClass)).collect(Collectors.toList());
    }

    public static List<StructureVillagePieces.PieceWeight> getPiecesSemi(Random random, int size) {
        List<StructureVillagePieces.PieceWeight> list = Lists.<StructureVillagePieces.PieceWeight>newArrayList();
        list.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.House4Garden.class, 4, MathHelper.getInt(random, 2 + size, 4 + size * 2)));
        list.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.Church.class, 20, MathHelper.getInt(random, 0 + size, 1 + size)));
        list.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.House1.class, 20, MathHelper.getInt(random, 0 + size, 2 + size)));
        list.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.WoodHut.class, 3, MathHelper.getInt(random, 2 + size, 5 + size * 3)));
        list.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.Hall.class, 15, MathHelper.getInt(random, 0 + size, 2 + size)));
        list.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.House2.class, 15, MathHelper.getInt(random, 0, 1 + size)));
        list.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.House3.class, 8, MathHelper.getInt(random, 0 + size, 3 + size * 2)));

        net.minecraftforge.fml.common.registry.VillagerRegistry.addExtraVillageComponents(list, random, size);

        list.removeIf(pieceWeight -> (pieceWeight).villagePiecesLimit == 0);
        return list.stream().filter(piece -> BWFieldBase.class.isAssignableFrom(piece.villagePieceClass)).collect(Collectors.toList());
    }

    public static List<StructureVillagePieces.PieceWeight> getPiecesNormal(Random random, int size) {
        List<StructureVillagePieces.PieceWeight> list = Lists.<StructureVillagePieces.PieceWeight>newArrayList();
        list.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.House4Garden.class, 4, MathHelper.getInt(random, 2 + size, 4 + size * 2)));
        list.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.Church.class, 20, MathHelper.getInt(random, 0 + size, 1 + size)));
        list.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.House1.class, 20, MathHelper.getInt(random, 0 + size, 2 + size)));
        list.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.WoodHut.class, 3, MathHelper.getInt(random, 2 + size, 5 + size * 3)));
        list.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.Hall.class, 15, MathHelper.getInt(random, 0 + size, 2 + size)));
        list.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.Field1.class, 3, MathHelper.getInt(random, 1 + size, 4 + size)));
        list.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.Field2.class, 3, MathHelper.getInt(random, 2 + size, 4 + size * 2)));
        list.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.House2.class, 15, MathHelper.getInt(random, 0, 1 + size)));
        list.add(new StructureVillagePieces.PieceWeight(StructureVillagePieces.House3.class, 8, MathHelper.getInt(random, 0 + size, 3 + size * 2)));
        net.minecraftforge.fml.common.registry.VillagerRegistry.addExtraVillageComponents(list, random, size);

        list.removeIf(pieceWeight -> (pieceWeight).villagePiecesLimit == 0);
        return list.stream().filter(piece -> !AbandonedVillagePiece.class.isAssignableFrom(piece.villagePieceClass) && !Well.class.isAssignableFrom(piece.villagePieceClass)).collect(Collectors.toList());
    }

    @Override
    protected StructureStart getStructureStart(int x, int z) {
        VillageStatus status = VillageStatus.getStatus(x, z);
        if (GlobalConfig.debug)
            System.out.printf("%s <%s,%s> /tp %s ~ %s\n", status.name().toLowerCase(), x, z, x * 16, z * 16);
        return new AbandonedStart(this.world, this.rand, x, z, this.size, status);
    }

    public enum VillageStatus {
        NORMAL(HCVillages.normalRadius.get() / 16),
        SEMIABANDONED(HCVillages.semiabandonedRadius.get() / 16),
        ABANDONED(0);
        public static final VillageStatus[] VALUES = values();
        private int radius;

        VillageStatus(int radius) {
            this.radius = radius;
        }

        public static VillageStatus getStatus(int x, int z) {
            for (VillageStatus status : VALUES) {
                if (status.inRadius(x, z))
                    return status;
            }
            return NORMAL;
        }

        public boolean inRadius(int x, int z) {
            if (GlobalConfig.debug)
                System.out.printf("Checking %s @a <%s,%s> r:%s\n", this.name(), x, z, radius);
            return Math.abs(x) >= radius || Math.abs(z) >= radius;
        }
    }

    public static class AbandonedStart extends StructureStart {
        private boolean hasMoreThanTwoComponents;
        private VillageStatus status;

        public AbandonedStart() {
        }

        public AbandonedStart(World worldIn, Random rand, int x, int z, int size, VillageStatus status) {
            super(x, z);
            this.status = status;
            List<StructureVillagePieces.PieceWeight> pieceWeights;
            switch (status) {
                case NORMAL:
                    pieceWeights = getPiecesNormal(rand, size);
                    break;
                case SEMIABANDONED:
                    pieceWeights = getPiecesSemi(rand, size);
                    break;
                default:
                    pieceWeights = getPiecesAbandoned(rand, size);
                    break;
            }
            Well start = new Well(status, worldIn.getBiomeProvider(), 0, rand, (x << 4) + 2, (z << 4) + 2, pieceWeights, size);
            this.components.add(start);
            start.buildComponent(start, this.components, rand);
            List<StructureComponent> roads = start.pendingRoads;
            List<StructureComponent> houses = start.pendingHouses;
            while (!roads.isEmpty()) {
                int j = rand.nextInt(roads.size());
                StructureComponent road = roads.remove(j);
                road.buildComponent(start, this.components, rand);
            }
            while (!houses.isEmpty()) {
                int i = rand.nextInt(houses.size());
                StructureComponent house = houses.remove(i);
                if (house instanceof Torch)
                    continue;
                house.buildComponent(start, this.components, rand);
            }

            this.updateBoundingBox();
            int k = 0;

            for (StructureComponent piece : this.components) {
                if (!(piece instanceof Road)) {
                    ++k;
                }
            }

            this.hasMoreThanTwoComponents = k > 2;

        }

        /**
         * currently only defined for Villages, returns true if Village has more than 2 non-road components
         */
        public boolean isSizeableStructure() {
            return this.hasMoreThanTwoComponents;
        }

        public void writeToNBT(NBTTagCompound tagCompound) {
            super.writeToNBT(tagCompound);
            tagCompound.setBoolean("Valid", this.hasMoreThanTwoComponents);
            tagCompound.setInteger("Status", this.status.ordinal());
        }

        public void readFromNBT(NBTTagCompound tagCompound) {
            super.readFromNBT(tagCompound);
            this.hasMoreThanTwoComponents = tagCompound.getBoolean("Valid");
            this.status = VillageStatus.VALUES[tagCompound.getInteger("Status")];
        }
    }

}


