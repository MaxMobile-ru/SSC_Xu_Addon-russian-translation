package xu_mod.SSCXuAddon.powers;

import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.onixary.shapeShifterCurseFabric.minion.IPlayerEntityMinion;
import xu_mod.SSCXuAddon.SSCXuAddon;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SpiderMinionShieldPower extends MinionShieldPower {
    private final List<Identifier> minionsIDs;
    private final float damageMulti;

    public SpiderMinionShieldPower(PowerType<?> type, LivingEntity entity, List<Identifier> minionIDs, float damageMulti) {
        super(type, entity, null, 1.0f, 1.0f);
        this.minionsIDs = minionIDs == null ? new ArrayList<>() : minionIDs;
        this.damageMulti = damageMulti;
    }

    @Override
    public float modifyDamageTaken(DamageSource source, float amount, Entity attacker) {
        float finalDamage = amount;
        float healthPercent = entity.getHealth() / entity.getMaxHealth();
        float canDecreaseDamage = amount * Math.min(1.0f, 2f * (1 - healthPercent));

        if (entity instanceof IPlayerEntityMinion ipem && entity.getWorld() instanceof ServerWorld serverWorld) {
            ConcurrentHashMap<Identifier, ArrayList<UUID>> allMinion = ipem.shape_shifter_curse$getAllMinions();
            done:
            for (Identifier id : minionsIDs) {
                if (allMinion.containsKey(id)) {
                    List<UUID> uuids = allMinion.get(id);
                    Random random = entity.getRandom();
                    while (!uuids.isEmpty() && canDecreaseDamage > 0) {
                        int index = random.nextInt(uuids.size());
                        UUID uuid = uuids.get(index);
                        Entity minion = serverWorld.getEntity(uuid);
                        if (minion instanceof LivingEntity livingEntity) {
                            float finalDecDamage = Math.min(livingEntity.getHealth(), canDecreaseDamage * damageMulti);
                            if (finalDecDamage <= 0) {
                                break done;
                            }
                            livingEntity.damage(source, finalDecDamage);
                            float finalDecPDamage = finalDecDamage / damageMulti;
                            canDecreaseDamage -= finalDecPDamage;
                            finalDamage -= finalDecPDamage;
                        }
                    }
                    if (canDecreaseDamage <= 0) {
                        break;
                    }
                }
            }
        }
        return finalDamage;
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                SSCXuAddon.identifier("minion_shield_spider"),
                new SerializableData()
                        .add("minion_ids", SerializableDataTypes.IDENTIFIERS, null)
                        .add("damage_multi", SerializableDataTypes.FLOAT, 1.0f),
                (data) -> (type, entity) ->
                        new SpiderMinionShieldPower(type, entity, data.get("minion_ids"), data.get("damage_multi"))
        );
    }
}
