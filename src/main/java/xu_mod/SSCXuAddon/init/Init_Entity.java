package xu_mod.SSCXuAddon.init;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.onixary.shapeShifterCurseFabric.minion.mobs.AnubisWolfMinionEntity;
import net.onixary.shapeShifterCurseFabric.util.EntityAttributeRegister;
import xu_mod.SSCXuAddon.SSCXuAddon;
import xu_mod.SSCXuAddon.data.entity.minion.SpiderMinion;
import xu_mod.SSCXuAddon.data.entity.projectiles.BloodThornEntity;
import xu_mod.SSCXuAddon.data.entity.projectiles.SummonTrident;

public class Init_Entity {
    public static final EntityType<BloodThornEntity> BLOOD_THORN = Registry.register(
            Registries.ENTITY_TYPE,
            SSCXuAddon.identifier("blood_thorn"),
            FabricEntityTypeBuilder.<BloodThornEntity>create(SpawnGroup.MISC, BloodThornEntity::new).dimensions(EntityDimensions.fixed(0.5f, 0.5f)).trackRangeChunks(10).trackedUpdateRate(1).build()
    );

    public static final EntityType<SummonTrident> SUMMON_TRIDENT = Registry.register(
            Registries.ENTITY_TYPE,
            SSCXuAddon.identifier("summon_trident"),
            FabricEntityTypeBuilder.<SummonTrident>create(SpawnGroup.MISC, SummonTrident::new).dimensions(EntityDimensions.fixed(0.5f, 0.5f)).trackRangeChunks(10).trackedUpdateRate(1).build()
    );

    public static final EntityType<SpiderMinion> SPIDER_MINION = Registry.register(
            Registries.ENTITY_TYPE,
            SSCXuAddon.identifier("spider_minion"),
            FabricEntityTypeBuilder.<SpiderMinion>create(SpawnGroup.MISC, SpiderMinion::new).dimensions(EntityDimensions.fixed(0.5f, 0.5f)).trackRangeChunks(10).trackedUpdateRate(1).build()
    );

    public static void init() {
        EntityAttributeRegister.register(SPIDER_MINION, SpiderMinion::createMobAttributes);
    }

}
