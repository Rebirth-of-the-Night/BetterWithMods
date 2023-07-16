package betterwithmods.client;

import betterwithmods.api.block.IRenderRotationPlacement;
import betterwithmods.client.gui.GuiStatus;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/**
 * Created by primetoxinz on 6/7/17.
 */
public class ClientEventHandler {
    public static Minecraft mc = Minecraft.getMinecraft();

    public static void renderBlock(IBlockState state, BlockPos pos, World world) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        mc.getBlockRendererDispatcher().renderBlock(state, pos, world, buffer);
        tessellator.draw();
    }

    public static void renderBasicGrid(World world, Block block, BlockPos pos, ItemStack stack, EntityPlayer player, EnumFacing side, RayTraceResult target, double partial) {
        double dx = (player.lastTickPosX + (player.posX - player.lastTickPosX) * partial);
        double dy = (player.lastTickPosY + (player.posY - player.lastTickPosY) * partial);
        double dz = (player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partial);

        GlStateManager.pushMatrix();

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.setTranslation(-dx, -dy, -dz);
        buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
        AxisAlignedBB AABB = Block.FULL_BLOCK_AABB.grow(0.0020000000949949026D);
        double min, max, offset;
        switch (side.getAxis()) {
            case X:
                min = Math.max(AABB.minZ, AABB.minY);
                max = Math.min(AABB.maxZ, AABB.maxY);
                offset = ((side == EnumFacing.EAST) ? AABB.maxX : AABB.minX) + pos.getX();
                buffer.pos(offset, min + pos.getY(), min + pos.getZ()).color(0F, 0F, 0f, 0.0F).endVertex();
                buffer.pos(offset, min + pos.getY(), min + pos.getZ()).color(0F, 0F, 0f, 0.4F).endVertex();
                buffer.pos(offset, max + pos.getY(), max + pos.getZ()).color(0F, 0F, 0f, 0.4F).endVertex();
                buffer.pos(offset, min + pos.getY(), max + pos.getZ()).color(0F, 0F, 0f, 0.0F).endVertex();
                buffer.pos(offset, max + pos.getY(), min + pos.getZ()).color(0F, 0F, 0f, 0.4F).endVertex();
                buffer.pos(offset, max + pos.getY(), min + pos.getZ()).color(0F, 0F, 0f, 0.0F).endVertex();
                break;
            case Y:
                min = Math.max(AABB.minX, AABB.minZ);
                max = Math.min(AABB.maxX, AABB.maxZ);
                offset = ((side == EnumFacing.UP) ? AABB.maxY : AABB.minY) + pos.getY();
                buffer.pos(min + pos.getX(), offset, min + pos.getZ()).color(0F, 0F, 0f, 0.0F).endVertex();
                buffer.pos(min + pos.getX(), offset, min + pos.getZ()).color(0F, 0F, 0f, 0.4F).endVertex();
                buffer.pos(max + pos.getX(), offset, max + pos.getZ()).color(0F, 0F, 0f, 0.4F).endVertex();
                buffer.pos(min + pos.getX(), offset, max + pos.getZ()).color(0F, 0F, 0f, 0.0F).endVertex();
                buffer.pos(max + pos.getX(), offset, min + pos.getZ()).color(0F, 0F, 0f, 0.4F).endVertex();
                buffer.pos(max + pos.getX(), offset, min + pos.getZ()).color(0F, 0F, 0f, 0.0F).endVertex();
                break;
            case Z:
                min = Math.max(AABB.minX, AABB.minY);
                max = Math.min(AABB.maxX, AABB.maxY);
                offset = ((side == EnumFacing.SOUTH) ? AABB.maxZ : AABB.minZ) + pos.getZ();
                buffer.pos(min + pos.getX(), min + pos.getY(), offset).color(0F, 0F, 0f, 0.0F).endVertex();
                buffer.pos(min + pos.getX(), min + pos.getY(), offset).color(0F, 0F, 0f, 0.4F).endVertex();
                buffer.pos(max + pos.getX(), max + pos.getY(), offset).color(0F, 0F, 0f, 0.4F).endVertex();
                buffer.pos(max + pos.getX(), min + pos.getY(), offset).color(0F, 0F, 0f, 0.0F).endVertex();
                buffer.pos(min + pos.getX(), max + pos.getY(), offset).color(0F, 0F, 0f, 0.4F).endVertex();
                buffer.pos(min + pos.getX(), max + pos.getY(), offset).color(0F, 0F, 0f, 0.0F).endVertex();
                break;
        }


        tessellator.draw();
        buffer.setTranslation(0, 0, 0);

        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void renderMiniBlock(World world, Block block, BlockPos pos, ItemStack stack, EntityPlayer player, EnumFacing side, RayTraceResult target, double partial) {
        Vec3d vec = target.hitVec.add(-target.getBlockPos().getX(),-target.getBlockPos().getY(),-target.getBlockPos().getZ());
        float x = (float) vec.x , y = (float) vec.y, z = (float) vec.z;
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(4.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        pos = pos.offset(side);
        if (world.mayPlace(block,pos,true,side,player) && world.getWorldBorder().contains(pos)) {
            double dx = (player.lastTickPosX + (player.posX - player.lastTickPosX) * partial);
            double dy = (player.lastTickPosY + (player.posY - player.lastTickPosY) * partial);
            double dz = (player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partial);

            AxisAlignedBB box = ((IRenderRotationPlacement) block).getBounds(world, pos, side, x, y, z, stack, player).grow(0.002D).offset(pos).offset(-dx, -dy, -dz);
            RenderGlobal.drawSelectionBoundingBox(box, 0.0F, 0.0F, 0.0F, 0.4F);
        }

        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void putTooltip(ItemTooltipEvent e) {

    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onBlockHighlight(DrawBlockHighlightEvent event) {
        EntityPlayer player = event.getPlayer();
        ItemStack stack = ItemStack.EMPTY;
        if ((player.getHeldItem(EnumHand.MAIN_HAND).isEmpty() || !(player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBlock)) && !player.getHeldItem(EnumHand.OFF_HAND).isEmpty()) {
            stack = player.getHeldItem(EnumHand.OFF_HAND);
        } else if (!player.getHeldItem(EnumHand.MAIN_HAND).isEmpty()) {
            stack = player.getHeldItem(EnumHand.MAIN_HAND);
        }
        Block block = Block.getBlockFromItem(stack.getItem());
        if (event.getTarget().typeOfHit == RayTraceResult.Type.BLOCK && block instanceof IRenderRotationPlacement) {
            World world = player.getEntityWorld();
            EnumFacing side = event.getTarget().sideHit;
            BlockPos pos = event.getTarget().getBlockPos();
            if (world.getWorldBorder().contains(pos)) {
                ((IRenderRotationPlacement) block).getRenderFunction().render(world, block, pos, stack, player, side, event.getTarget(), event.getPartialTicks());
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void renderStatus(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
            GuiStatus.INSTANCE.draw();
        }
    }
}

