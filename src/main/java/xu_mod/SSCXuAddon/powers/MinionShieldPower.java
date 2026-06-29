package xu_mod.SSCXuAddon.powers;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.minion.IPlayerEntityMinion;
import xu_mod.SSCXuAddon.SSCXuAddon;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MinionShieldPower extends Power {
    private final List<Identifier> minionsIDs;
    private final float damageMulti;
    private final float damagePercent;

    public MinionShieldPower(PowerType<?> type, LivingEntity entity, List<Identifier> minionIDs, float damageMulti, float damagePercent) {
        super(type, entity);
        this.minionsIDs = minionIDs == null ? new ArrayList<>() : minionIDs;
        this.damageMulti = damageMulti;
        this.damagePercent = damagePercent;
    }

    public float modifyDamageTaken(DamageSource source, float amount, Entity attacker) {
        float finalDamage = amount;
        float canDecreaseDamage = amount * damagePercent;

        if (entity instanceof IPlayerEntityMinion ipem && entity.getWorld() instanceof ServerWorld serverWorld) {
            ConcurrentHashMap<Identifier, ArrayList<UUID>> allMinion = ipem.shape_shifter_curse$getAllMinions();
            done:
            for (Identifier id : minionsIDs) {
                if (allMinion.containsKey(id)) {
                    for (UUID uuid : allMinion.get(id)) {
                        Entity minion = serverWorld.getEntity(uuid);
                        if (minion instanceof LivingEntity livingEntity) {
                            float finalDecDamage = livingEntity.getHealth();
                            finalDecDamage = Math.min(finalDecDamage, canDecreaseDamage * damageMulti);
                            livingEntity.damage(source, finalDecDamage);
                            float finalDecPDamage = finalDecDamage / damageMulti;
                            canDecreaseDamage -= finalDecPDamage;
                            finalDamage -= finalDecPDamage;
                        }
                        if (canDecreaseDamage <= 0) {
                            break done;
                        }
                    }
                }
            }
        }
        return finalDamage;
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                SSCXuAddon.identifier("minion_shield"),
                new SerializableData()
                        .add("minion_ids", SerializableDataTypes.IDENTIFIERS, null)
                        .add("damage_multi", SerializableDataTypes.FLOAT, 1.0f)
                        .add("damage_percent", SerializableDataTypes.FLOAT, 1.0f),
                (data) -> (type, entity) ->
                        new MinionShieldPower(type, entity, data.get("minion_ids"), data.get("damage_multi"), data.get("damage_percent"))
        );
    }
}
