package betterwithmods.common.blocks.tile;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Created by primetoxinz on 5/4/17.
 */
public class TileBasic extends TileEntity {



    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        markDirty();
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeToNBT(nbtTag);
        return new SPacketUpdateTileEntity(getPos(), -999, nbtTag);
    }


    @SideOnly(Side.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        super.onDataPacket(net,packet);
        this.readFromNBT(packet.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    public void onPlacedBy(EntityLivingBase placer, @Nullable EnumFacing face, ItemStack stack, float hitX, float hitY, float hitZ) {

    }

    public void onBreak() {

    }

    public int value(NBTTagCompound tag, String key, int defaultValue) {
        return tag.hasKey(key) ? tag.getInteger(key) : defaultValue;
    }

    public String value(NBTTagCompound tag, String key, String defaultValue) {
        return tag.hasKey(key) ? tag.getString(key) : defaultValue;
    }

    public boolean value(NBTTagCompound tag, String key, boolean defaultValue) {
        return tag.hasKey(key) ? tag.getBoolean(key) : defaultValue;
    }
}
