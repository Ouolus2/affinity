package io.wispforest.affinity.client.particle;

import io.wispforest.affinity.particle.BezierItemEmitterParticleEffect;
import io.wispforest.affinity.particle.BezierItemParticleEffect;
import io.wispforest.owo.util.VectorRandomUtils;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class BezierItemEmitterParticle extends NoRenderParticle {

    private static final Map<Vec3d, BezierItemEmitterParticle> ACTIVE_PARTICLES = new HashMap<>();

    private final Vec3d position;
    private final ItemStack stack;
    private final Vec3d endpoint;
    private final int travelDuration;

    protected BezierItemEmitterParticle(ClientWorld world, double x, double y, double z, ItemStack stack, Vec3d endpoint, int emitterDuration, int travelDuration) {
        super(world, x, y, z);
        this.endpoint = endpoint;

        this.position = new Vec3d(this.x, this.y, this.z);
        this.maxAge = emitterDuration;
        this.gravityStrength = 0;
        this.stack = stack;
        this.travelDuration = travelDuration;

        ACTIVE_PARTICLES.put(this.position, this);
    }

    @Override
    public void tick() {
        var offset = VectorRandomUtils.getRandomOffset(this.world, Vec3d.ZERO, .25);

        this.world.addParticle(new BezierItemParticleEffect(stack, this.endpoint, this.travelDuration),
                this.x + offset.x, this.y + offset.y, this.z + offset.z, 0, 0, 0);
        this.world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, this.stack),
                this.x, this.y, this.z, offset.x, offset.y, offset.z);

//        offset = VectorRandomUtils.getRandomOffset(this.world, Vec3d.ZERO, .25);
//        this.world.addParticle(new BezierItemParticleEffect(stack, this.endpoint),
//                this.x + offset.x, this.y + offset.y, this.z + offset.z, 0, 0, 0);

        super.tick();
    }

    @Override
    public void markDead() {
        super.markDead();
        ACTIVE_PARTICLES.remove(this.position);
    }

    public static void removeParticleAt(Vec3d position) {
        if (!ACTIVE_PARTICLES.containsKey(position)) return;

        var particle = ACTIVE_PARTICLES.get(position);
        particle.age = particle.maxAge;
    }

    public static class Factory implements ParticleFactory<BezierItemEmitterParticleEffect> {
        @Nullable
        @Override
        public Particle createParticle(BezierItemEmitterParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new BezierItemEmitterParticle(world, x, y, z, parameters.stack(), parameters.splineEndpoint(), parameters.emitterDuration(), parameters.travelDuration());
        }
    }
}
