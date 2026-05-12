package xu_mod.SSCXuAddon.data.entity.projectiles;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.world.World;

public class SummonTrident extends TridentEntity {
    public SummonTrident(EntityType<? extends TridentEntity> entityType, World world) {
        super(entityType, world);
    }
}
