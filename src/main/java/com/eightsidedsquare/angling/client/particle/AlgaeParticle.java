package com.eightsidedsquare.angling.client.particle;

import com.google.common.collect.ImmutableList;
import net.minecraft.Util;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.tags.FluidTags;
import org.jetbrains.annotations.Nullable;

public class AlgaeParticle extends TextureSheetParticle {
    protected AlgaeParticle(ClientLevel clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        super(clientWorld, x, y, z, velocityX, velocityY, velocityZ);
        this.xd = velocityX;
        this.yd = velocityY;
        this.zd = velocityZ;
        this.lifetime = this.random.nextIntBetweenInclusive(60, 120);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.removed && !this.level.getFluidState(BlockPos.containing(this.x, this.y, this.z)).is(FluidTags.WATER)) {
            this.remove();
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType>{

        private final SpriteSet spriteProvider;

        public Factory(SpriteSet spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            AlgaeParticle particle = new AlgaeParticle(world, x, y, z, velocityX, velocityY, velocityZ);
            particle.pickSprite(this.spriteProvider);
            int color = Util.getRandom(ImmutableList.of(0x79ab25, 0x5d9621, 0x41801c, 0x246a17), world.random);
            float r = ((color >> 16) & 0xFF) / 255f;
            float g = ((color >> 8) & 0xFF) / 255f;
            float b = (color & 0xFF) / 255f;
            particle.setColor(r, g, b);
            particle.setAlpha(0.5f);
            return particle;
        }
    }
}
