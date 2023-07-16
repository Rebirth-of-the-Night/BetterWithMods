package betterwithmods.client.render;

import betterwithmods.common.entity.EntityDynamite;
import betterwithmods.common.items.ItemDynamite;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class RenderDynamite extends RenderSnowball<EntityDynamite> {
    public RenderDynamite(RenderManager renderManagerIn, RenderItem itemRendererIn) {
        super(renderManagerIn, Items.APPLE, itemRendererIn);
    }

    @Override
    public void doRender(EntityDynamite entity, double x, double y, double z, float entityYaw, float partialTicks) {
        ItemDynamite.setFuseOverride(entity.getFuseSlide());
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        ItemDynamite.resetFuseOverride();
    }

    @Override
    public ItemStack getStackToRender(EntityDynamite entity) {
        return entity.getDynamiteStack();
    }
}
