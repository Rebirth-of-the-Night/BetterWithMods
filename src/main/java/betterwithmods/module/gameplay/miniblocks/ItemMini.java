package betterwithmods.module.gameplay.miniblocks;

import betterwithmods.module.gameplay.miniblocks.blocks.BlockMini;
import betterwithmods.module.gameplay.miniblocks.tiles.TileMini;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemMini extends ItemBlock {

    public ItemMini(Block block) {
        super(block);
    }

    public static IBlockState getState(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemMini) {
            NBTTagCompound tag = stack.getSubCompound("texture");
            if (tag != null) {
                return NBTUtil.readBlockState(tag);
            }
        }
        return null;
    }

    public static boolean matches(ItemStack a, ItemStack b) {
        if ((a == null || b == null))
            return false;
        if (!(a.getItem() instanceof ItemMini && b.getItem() instanceof ItemMini))
            return false;

        ItemMini miniA = (ItemMini) a.getItem(), miniB = (ItemMini) b.getItem();
        if (miniA.getBlock() != miniB.getBlock())
            return false;
        IBlockState stateA = getState(a), stateB = getState(b);
        return stateA != null & stateB != null && stateA.equals(stateB);
    }

    public static boolean placeBlockAt(ItemMini item, ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        if (!world.setBlockState(pos, newState, 11)) return false;

        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == item.block) {
            setTileEntityNBT(world, player, pos, stack);
            TileEntity tile = world.getTileEntity(pos);
            if(tile instanceof TileMini)
                setNBT((TileMini) tile,world, stack);
            ((BlockMini) item.block).onBlockPlacedBy(world, pos, state, player, stack, side, hitX, hitY, hitZ);
            if (player instanceof EntityPlayerMP)
                CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP) player, pos, stack);
        }
        return true;
    }

    public static void setNBT(TileMini tile, World worldIn, ItemStack stackIn) {
        MinecraftServer minecraftserver = worldIn.getMinecraftServer();
        if (minecraftserver == null)
            return;

        NBTTagCompound data = stackIn.getSubCompound("miniblock");

        if (data != null) {
            if (!worldIn.isRemote && tile.onlyOpsCanSetNbt()) {
                return;
            }

            NBTTagCompound tileNBT = tile.writeToNBT(new NBTTagCompound());
            NBTTagCompound newNBT = tileNBT.copy();
            tileNBT.merge(data);

            if (!tileNBT.equals(newNBT)) {
                tile.readFromNBT(tileNBT);
                tile.markDirty();
            }
        }
    }

    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        return placeBlockAt(this, stack, player, world, pos, side, hitX, hitY, hitZ, newState);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        NBTTagCompound tag = stack.getSubCompound("texture");
        String type = I18n.format("bwm.unknown_mini.name").trim();
        if (tag != null) {
            IBlockState state = NBTUtil.readBlockState(tag);
            ItemStack block = new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));
            if (block.getItem() instanceof ItemBlock) {
                ItemBlock itemBlock = (ItemBlock) block.getItem();
                type = itemBlock.getItemStackDisplayName(block);
            }
        }
        return I18n.format(this.getTranslationKey(stack) + ".name", type);
    }

    @Override
    public int getItemBurnTime(ItemStack stack) {
        if (!(((ItemMini) stack.getItem()).getBlock() instanceof BlockMini))
            return super.getItemBurnTime(stack);

        BlockMini miniblock = (BlockMini) ((ItemMini) stack.getItem()).getBlock();

        int divisor = 2;
        MiniType type = MiniType.fromBlock(miniblock);

        if (type == MiniType.MOULDING)
            divisor = 4;
        else if (type == MiniType.CORNER)
            divisor = 8;

        if (super.getItemBurnTime(stack) != -1)
            return super.getItemBurnTime(stack) / divisor;

        NBTTagCompound tag = stack.getSubCompound("texture");
        if (tag != null) {
            IBlockState state = NBTUtil.readBlockState(tag);
            ItemStack derivedBlock = new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));
            return TileEntityFurnace.getItemBurnTime(derivedBlock) / divisor;
        }

        return -1;
    }
}
