package betterwithmods.module.gameplay.breeding_harness.models;

import net.minecraft.client.model.ModelQuadruped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelSheepHarness extends ModelQuadruped {
    private float headRotationAngleX;

    public ModelSheepHarness(float scale) {
        super(12, scale);
        this.head = new ModelRenderer(this, 0, 0);
        this.head.addBox(-3.0F, -4.0F, -6.0F, 6, 6, 8, scale);
        this.head.setRotationPoint(0.0F, 6.0F, -8.0F);
        this.body = new ModelRenderer(this, 28, 8);
        this.body.addBox(-4.0F, -10.0F, -7.0F, 8, 16, 6, scale);
        this.body.setRotationPoint(0.0F, 5.0F, 2.0F);
    }

    public void setLivingAnimations(EntityLivingBase entityLivingBase, float limbSwing, float limbSwingAmount, float partialTickTime) {
        super.setLivingAnimations(entityLivingBase, limbSwing, limbSwingAmount, partialTickTime);
        this.head.rotationPointY = 6.0F + ((EntitySheep) entityLivingBase).getHeadRotationPointY(partialTickTime) * 9.0F;
        this.headRotationAngleX = ((EntitySheep) entityLivingBase).getHeadRotationAngleX(partialTickTime);
    }


    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
        super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
        this.head.rotateAngleX = this.headRotationAngleX;
    }
}