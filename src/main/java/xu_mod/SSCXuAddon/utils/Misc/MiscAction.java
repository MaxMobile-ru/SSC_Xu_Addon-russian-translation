package xu_mod.SSCXuAddon.utils.Misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MiscAction {
    public static void WaterExplosion(@Nullable Entity targetEntity, @NotNull Entity explosionOwner, @Nullable Entity Owner, double Range, float BaseDamage, float ExtraDamage, float KnockPower, int ParticleCount, boolean ForceDamage, boolean highSound) {
        if (Owner == null) {
            Owner = explosionOwner;
        }
        if (targetEntity == null) {
            targetEntity = explosionOwner;
        }
        Vec3d explosionPos = targetEntity.getPos();
        @Nullable Entity finalOwner = Owner;
        for (LivingEntity entity : explosionOwner.getWorld().getEntitiesByClass(LivingEntity.class, targetEntity.getBoundingBox().expand(Range), e -> e != explosionOwner && e != finalOwner)) {
            Vec3d direction = entity.getPos().subtract(explosionPos);
            double distance = direction.length();
            if (distance > Range) {
                continue;
            }
            float distanceMultiplier = 1;
            if (distance > 0) {
                distanceMultiplier -= (float) (distance / Range);
            }
            if (ForceDamage) {
                entity.timeUntilRegen = 0;
                entity.lastDamageTaken = 0;
            }
            entity.damage(explosionOwner.getDamageSources().explosion(explosionOwner, Owner), distanceMultiplier * ExtraDamage + BaseDamage);
            entity.takeKnockback(KnockPower * distanceMultiplier, -direction.x, -direction.z);
        }
        if (explosionOwner.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(ParticleTypes.SPLASH, explosionPos.x, explosionPos.y, explosionPos.z, ParticleCount, Range * 0.5f, Range * 0.1f, Range * 0.5f, 0.2f);
        }
        if (highSound) {
            explosionOwner.getWorld().playSound(explosionPos.x, explosionPos.y, explosionPos.z, SoundEvents.ENTITY_AXOLOTL_SPLASH, explosionOwner.getSoundCategory(), 0.5f, 0.8f, false);
        } else {
            explosionOwner.getWorld().playSound(explosionPos.x, explosionPos.y, explosionPos.z, SoundEvents.ENTITY_AXOLOTL_SPLASH, explosionOwner.getSoundCategory(), 0.35f, 0.5f, false);
        }
    }
}
