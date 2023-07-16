package betterwithmods.client.render;

import betterwithmods.BWMod;
import net.minecraft.client.renderer.entity.RenderCaveSpider;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.util.ResourceLocation;

public class RenderJungleSpider extends RenderCaveSpider {
    private static final ResourceLocation JUNGLE_SPIDER_TEXTURES = new ResourceLocation(BWMod.MODID,"textures/entity/jungle_spider.png");

    public RenderJungleSpider(RenderManager renderManagerIn) {
        super(renderManagerIn);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityCaveSpider entity) {
        return JUNGLE_SPIDER_TEXTURES;
    }
}
