package xu_mod.SSCXuAddon.utils;

import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.swing.text.html.parser.Entity;

public class ParticleUtils {
    public static void spawnParticle(World world, ParticleEffect particle, Vec3d pos, int count, float speed, Vec3d spread) {
        if(count <= 0) {
            return;
        }
        if(!(world instanceof ServerWorld serverWorld) || world.isClient) {
            return;
        }
        double deltaX = spread.x;
        double deltaY = spread.y;
        double deltaZ = spread.z;
        serverWorld.spawnParticles(particle, pos.getX(), pos.getY(), pos.getZ(), count, deltaX, deltaY, deltaZ, speed);
    }

    public static void spawnParticle(LivingEntity entity, ParticleEffect particle, int count, float speed, Vec3d spread, float offsetY) {
        if(entity.getWorld().isClient) {
            return;
        }
        ServerWorld serverWorld = (ServerWorld) entity.getWorld();
        if(count <= 0)
            return;
        float deltaX = (float) (entity.getWidth() * spread.x);
        float deltaY = (float) (entity.getHeight() * spread.y);
        float deltaZ = (float) (entity.getWidth() * spread.z);
        float offsetYF = entity.getHeight() * offsetY;
        serverWorld.spawnParticles(particle, entity.getX(), entity.getY() + offsetYF, entity.getZ(), count, deltaX, deltaY, deltaZ, speed);
    }
}
