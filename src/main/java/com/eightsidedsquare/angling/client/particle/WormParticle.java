package com.eightsidedsquare.angling.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class WormParticle extends TextureSheetParticle {

    private final SpriteSet spriteProvider;

    protected WormParticle(ClientLevel clientWorld, double x, double y, double z, SpriteSet spriteProvider) {
        super(clientWorld, x, y, z);
        this.spriteProvider = spriteProvider;
        this.quadSize = 0.3f;
        setLifetime(200);
    }

    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Vec3 vec3d = camera.getPosition();
        float currentX = (float)(Mth.lerp(tickDelta, this.xo, this.x) - vec3d.x());
        float currentY = (float)(Mth.lerp(tickDelta, this.yo, this.y) - vec3d.y());
        float currentZ = (float)(Mth.lerp(tickDelta, this.zo, this.z) - vec3d.z());
        Quaternionf quaternion = Axis.YP.rotationDegrees(-camera.getYRot());
        Quaternionf flip = Axis.YP.rotationDegrees(180 - camera.getYRot());

        float size = this.getQuadSize(tickDelta);
        float minU = this.getU0();
        float maxU = this.getU1();
        float minV = this.getV0();
        float maxV = this.getV1();
        int light = this.getLightColor(tickDelta);

        renderFace(vertexConsumer, quaternion, size, currentX, currentY, currentZ, minU, maxU, minV, maxV, light);
        renderFace(vertexConsumer, flip, size, currentX, currentY, currentZ, minU, maxU, minV, maxV, light);
    }

    private void renderFace(VertexConsumer vertexConsumer, Quaternionf quaternion, float size, float x, float y, float z, float minU, float maxU, float minV, float maxV, int light) {
        Vector3f[] vec3fs = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};

        for(int k = 0; k < 4; ++k) {
            Vector3f vec3f2 = vec3fs[k];
            vec3f2.rotate(quaternion);
            vec3f2.mul(size);
            vec3f2.add(x, y, z);
        }
        vertexConsumer.vertex(vec3fs[0].x, vec3fs[0].y, vec3fs[0].z).uv(maxU, maxV).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        vertexConsumer.vertex(vec3fs[1].x, vec3fs[1].y, vec3fs[1].z).uv(maxU, minV).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        vertexConsumer.vertex(vec3fs[2].x, vec3fs[2].y, vec3fs[2].z).uv(minU, minV).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
        vertexConsumer.vertex(vec3fs[3].x, vec3fs[3].y, vec3fs[3].z).uv(minU, maxV).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(light).endVertex();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        if(age <= 10) {
            setParticleSpeed(0, 0.023f, 0);
        }else if(age >= lifetime - 10) {
            setParticleSpeed(0, -0.023f, 0);
        }else {
            setParticleSpeed(0, 0, 0);
        }
        super.tick();
        if(!Block.isFaceFull(level.getBlockState(BlockPos.containing(x, y - 0.5d, z)).getBlockSupportShape(level, BlockPos.containing(x, y - 0.5d, z)), Direction.UP)) {
            remove();
        }
        setSpriteFromAge(this.spriteProvider);
    }

    public record Factory(
            SpriteSet spriteProvider) implements ParticleProvider<SimpleParticleType> {

        @Override
        public Particle createParticle(SimpleParticleType parameters, ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            WormParticle particle = new WormParticle(world, x, y, z, this.spriteProvider);
            particle.pickSprite(this.spriteProvider);
            return particle;
        }
    }
}
