package xu_mod.SSCXuAddon.utils.Misc;

import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MiscAction {
    public static void WaterExplosion(@NotNull LivingEntity explosionOwner, @Nullable LivingEntity Owner, double Range, float BaseDamage, float ExtraDamage, float KnockPower, int Power, boolean ForceDamage) {
        if (Owner == null) {
            Owner = explosionOwner;
        }
        Vec3d explosionPos = explosionOwner.getPos();
        @Nullable LivingEntity finalOwner = Owner;
        for (LivingEntity entity : explosionOwner.getWorld().getEntitiesByClass(LivingEntity.class, explosionOwner.getBoundingBox().expand(Range), e -> e != explosionOwner && e != finalOwner)) {
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
                entity.lastDamageTaken = 0;
            }
            entity.damage(explosionOwner.getDamageSources().explosion(explosionOwner, Owner), distanceMultiplier * ExtraDamage + BaseDamage);
            entity.takeKnockback(KnockPower * distanceMultiplier, direction.x, direction.z);
        }
        for (int i = 0; i < Power; i++) {
            float x, y, z;
            x = (float) (explosionPos.x + (float) (Math.random() * 2f - 1) * Range);
            y = (float) (explosionPos.y + (float) (Math.random() * 2f - 1) * Range);
            z = (float) (explosionPos.z + (float) (Math.random() * 2f - 1) * Range);
            explosionOwner.getWorld().addParticle(ParticleTypes.UNDERWATER, x, y, z, 0, 0, 0);
        }
    }
}
