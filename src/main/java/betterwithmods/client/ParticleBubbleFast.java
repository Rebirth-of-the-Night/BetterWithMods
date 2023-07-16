package betterwithmods.client;

import net.minecraft.block.material.Material;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ParticleBubbleFast extends Particle {
    public ParticleBubbleFast(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn) {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        this.particleRed = 1.0F;
        this.particleGreen = 1.0F;
        this.particleBlue = 1.0F;
        this.setParticleTextureIndex(32);
        this.setSize(0.02F, 0.02F);
        this.particleScale *= this.rand.nextFloat() * 0.6F + 0.2F;
        this.motionX = xSpeedIn * 0.5D + (Math.random() * 2.0D - 1.0D) * 0.02D;
        this.motionY = ySpeedIn * 0.5D + (Math.random() * 2.0D - 1.0D) * 0.02D;
        this.motionZ = zSpeedIn * 0.5D + (Math.random() * 2.0D - 1.0D) * 0.02D;
        this.particleMaxAge = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
    }

    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY += 0.002D;
        this.move(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.95;
        this.motionY *= 0.95;
        this.motionZ *= 0.95;

        if (this.world.getBlockState(new BlockPos(this.posX, this.posY, this.posZ)).getMaterial() != Material.WATER) {
            this.setExpired();
        }

        if (this.particleMaxAge-- <= 0) {
            this.setExpired();
        }
    }
}
