package betterwithmods.event;

import betterwithmods.BWMod;
import betterwithmods.common.BWMBlocks;
import betterwithmods.common.BWRegistry;
import betterwithmods.common.items.ItemEnderSpectacles;
import betterwithmods.common.potion.BWPotion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod.EventBusSubscriber(modid = BWMod.MODID)
public class PotionEventHandler {


    @SubscribeEvent
    public static void onEndermanLook(LivingSetAttackTargetEvent event) {
        EntityLivingBase target = event.getTarget();
        EntityLivingBase living = event.getEntityLiving();
        if (living instanceof EntityEnderman && target instanceof EntityPlayer) {
            if (target.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof ItemEnderSpectacles) {
                ((EntityEnderman) living).setAttackTarget(null);
            }
        }
    }


    @SubscribeEvent
    public static void onRenderFireOverlay(RenderBlockOverlayEvent e) {
        if (e.getOverlayType() == RenderBlockOverlayEvent.OverlayType.FIRE) {
            if (e.getPlayer().getEntityWorld().getBlockState(e.getBlockPos()).getBlock() == BWMBlocks.STOKED_FLAME) {
                renderFireInFirstPerson();
                e.setCanceled(true);
            }
        }
    }

    private static void renderFireInFirstPerson() {
        Minecraft mc = Minecraft.getMinecraft();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);
        GlStateManager.depthFunc(519);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);

        for (int i = 0; i < 2; ++i) {
            GlStateManager.pushMatrix();
            TextureAtlasSprite textureatlassprite = mc.getTextureMapBlocks()
                    .getTextureExtry(BWMod.MODID + ":blocks/stoked_fire_layer_0");
            if (textureatlassprite == null)
                textureatlassprite = mc.getTextureMapBlocks().getMissingSprite();
            mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            float f1 = textureatlassprite.getMinU();
            float f2 = textureatlassprite.getMaxU();
            float f3 = textureatlassprite.getMinV();
            float f4 = textureatlassprite.getMaxV();
            GlStateManager.translate((float) (-(i * 2 - 1)) * 0.24F, -0.3F, 0.0F);
            GlStateManager.rotate((float) (i * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
            vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
            vertexbuffer.pos(-0.5D, -0.5D, -0.5D).tex((double) f2, (double) f4).endVertex();
            vertexbuffer.pos(0.5D, -0.5D, -0.5D).tex((double) f1, (double) f4).endVertex();
            vertexbuffer.pos(0.5D, 0.5D, -0.5D).tex((double) f1, (double) f3).endVertex();
            vertexbuffer.pos(-0.5D, 0.5D, -0.5D).tex((double) f2, (double) f3).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onHarvestBlock(BlockEvent.HarvestDropsEvent event) {
        if (event.getHarvester() != null && !event.isSilkTouching() && event.getHarvester().isPotionActive(BWRegistry.POTION_FORTUNE)) {
            PotionEffect effect = event.getHarvester().getActivePotionEffect(BWRegistry.POTION_FORTUNE);
            int level = effect.getAmplifier() + 1;
            if (event.getFortuneLevel() < level) {
                event.getDrops().clear();
                NonNullList<ItemStack> l = NonNullList.create();
                event.getState().getBlock().getDrops(l, event.getWorld(), event.getPos(), event.getState(), level);
                event.getDrops().addAll(l);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LootingLevelEvent event) {
        if (event.getEntityLiving() != null) {
            PotionEffect effect = event.getEntityLiving().getActivePotionEffect(BWRegistry.POTION_LOOTING);
            if (effect != null) {
                int level = effect.getAmplifier() + 1;
                if (event.getLootingLevel() < level) {
                    event.setLootingLevel(level);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerUpdate(TickEvent.PlayerTickEvent e) {
        if (e.phase == TickEvent.Phase.START)
            return;

        EntityPlayer player = e.player;
        for (PotionEffect potion : player.getActivePotionEffects()) {
            if (potion.getPotion() instanceof BWPotion) {
                ((BWPotion) potion.getPotion()).tick(player);
            }
        }
    }


    @SubscribeEvent
    public static void saveSoup(LivingEntityUseItemEvent.Finish event) {
        ItemStack item = event.getItem();
        if (item.getItem() instanceof ItemSoup) {
            ItemStack result = event.getResultStack();
            ItemStack copy = item.copy();
            copy.shrink(1);
            event.setResultStack(copy);
            if (event.getEntityLiving() instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) event.getEntityLiving();
                if (!player.inventory.addItemStackToInventory(result)) {
                    player.dropItem(result, false);
                }
            }
        }
    }
}
