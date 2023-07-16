package betterwithmods.common.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class BWPotion extends Potion {
    private ResourceLocation icon;
    private boolean beneficial;
    public BWPotion(String name, boolean b, int potionColor) {
        super(false, potionColor);
        this.beneficial = b;
        setRegistryName(name);
        this.setPotionName("potion." + name);
    }
    @SideOnly(Side.CLIENT)
    public boolean isBeneficial() {
        return this.beneficial;//decides top or bottom row
    }

    public ResourceLocation getIcon() {
        return icon;
    }
    public void setIcon(ResourceLocation icon) {
        this.icon = icon;
    }

    public void tick(EntityLivingBase entity) {}

    @Override
    public Potion setIconIndex(int p_76399_1_, int p_76399_2_) {
        return super.setIconIndex(p_76399_1_, p_76399_2_);
    }
}