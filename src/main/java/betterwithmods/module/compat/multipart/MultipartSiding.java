package betterwithmods.module.compat.multipart;

import betterwithmods.module.gameplay.miniblocks.ItemMini;
import betterwithmods.module.gameplay.miniblocks.blocks.BlockSiding;
import betterwithmods.module.gameplay.miniblocks.orientations.SidingOrientation;
import betterwithmods.module.gameplay.miniblocks.tiles.TileMini;
import mcmultipart.api.container.IPartInfo;
import mcmultipart.api.multipart.IMultipart;
import mcmultipart.api.multipart.IMultipartTile;
import mcmultipart.api.slot.EnumFaceSlot;
import mcmultipart.api.slot.IPartSlot;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class MultipartSiding implements IMultipart {

    private BlockSiding siding;

    public MultipartSiding(BlockSiding siding) {
        this.siding = siding;
    }

    @Override
    public Block getBlock() {
        return siding;
    }

    @Override
    public void onPartPlacedBy(IPartInfo part, EntityLivingBase placer, ItemStack stack) {
        TileMini tile = (TileMini) part.getTile().getTileEntity();
        ItemMini.setNBT(tile, tile.getWorld(), stack);
    }

    @Override
    public IPartSlot getSlotForPlacement(World world, BlockPos pos, IBlockState state, EnumFacing facing, float hitX, float hitY, float hitZ, EntityLivingBase placer) {
        SidingOrientation orientation = (SidingOrientation) SidingOrientation.getFromVec(new Vec3d(hitX, hitY, hitZ), facing);
        return EnumFaceSlot.fromFace(orientation.getFacing());
    }

    @Override
    public IPartSlot getSlotFromWorld(IBlockAccess world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileMini) {
            SidingOrientation orientation = (SidingOrientation) ((TileMini) tile).getOrientation();
            return EnumFaceSlot.fromFace(orientation.getFacing());
        }
        return EnumFaceSlot.NORTH;
    }

    @Override
    public IMultipartTile convertToMultipartTile(TileEntity tileEntity) {
        return new MultipartTileProxy(tileEntity);
    }
}
