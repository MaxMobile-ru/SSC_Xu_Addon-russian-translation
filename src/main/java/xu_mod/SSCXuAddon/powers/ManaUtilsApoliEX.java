package xu_mod.SSCXuAddon.powers;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.mana.ManaUtils;
import xu_mod.SSCXuAddon.SSCXuAddon;
import xu_mod.SSCXuAddon.utils.Utils;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

// 等我有空就把这里的Actions合进主仓库 我觉得还是有点通用性
public class ManaUtilsApoliEX {
    public static HashMap<UUID, HashMap<Identifier, Double>> lastManaConsumed = new HashMap<>();

    public static void consumeManaWithCooldown(PlayerEntity player, Identifier cooldownID, int cooldown, double mana) {
        double cooldownPercent = Utils.getCoolDownPassPercent(player, cooldownID);
        double lastMana = lastManaConsumed.computeIfAbsent(player.getUuid(), k -> new HashMap<>()).getOrDefault(cooldownID, 0.0);
        double finalMana = mana;
        if (cooldownPercent >= 1.0) {
            lastMana = 0.0d;
        }
        finalMana -= lastMana;
        if (finalMana > 0.0) {
            ManaUtils.consumePlayerMana(player, finalMana);
            lastManaConsumed.computeIfAbsent(player.getUuid(), k -> new HashMap<>()).put(cooldownID, mana);
            Utils.triggerLocalCoolDown(player, cooldownID, cooldown);
        }
    }


    public static void registerActions(Consumer<ActionFactory<Entity>> ActionRegister, Consumer<ActionFactory<Pair<Entity, Entity>>> BIActionRegister) {
        ActionRegister.accept(new ActionFactory<Entity>(
                SSCXuAddon.identifier("set_mana_percent"),
                new SerializableData()
                        .add("mana_percent", SerializableDataTypes.DOUBLE, 0.0d),
                (data, e) -> {
                    if (e instanceof ServerPlayerEntity playerEntity) {
                        double mana_percent = data.get("mana_percent");
                        double mana_max = ManaUtils.getPlayerMaxMana(playerEntity);
                        double mana = mana_max * mana_percent;
                        ManaUtils.setPlayerMana(playerEntity, mana);
                    }
                })
        );
        ActionRegister.accept(new ActionFactory<Entity>(
                SSCXuAddon.identifier("gain_mana_percent"),
                new SerializableData()
                        .add("mana_percent", SerializableDataTypes.DOUBLE, 0.0d),
                (data, e) -> {
                    if (e instanceof ServerPlayerEntity playerEntity) {
                        double mana_percent = data.get("mana_percent");
                        double mana_max = ManaUtils.getPlayerMaxMana(playerEntity);
                        double mana = mana_max * mana_percent;
                        ManaUtils.gainPlayerMana(playerEntity, mana);
                    }
                })
        );
        ActionRegister.accept(new ActionFactory<Entity>(
                SSCXuAddon.identifier("consume_mana_percent"),
                new SerializableData()
                        .add("mana_percent", SerializableDataTypes.DOUBLE, 0.0d),
                (data, e) -> {
                    if (e instanceof ServerPlayerEntity playerEntity) {
                        double mana_percent = data.get("mana_percent");
                        double mana_max = ManaUtils.getPlayerMaxMana(playerEntity);
                        double mana = mana_max * mana_percent;
                        ManaUtils.consumePlayerMana(playerEntity, mana);
                    }
                })
        );

        ActionRegister.accept(new ActionFactory<Entity>(
                SSCXuAddon.identifier("consume_mana_with_cooldown"),
                new SerializableData()
                        .add("mana", SerializableDataTypes.DOUBLE, 0.0d)
                        .add("cooldown_id", SerializableDataTypes.IDENTIFIER, null)
                        .add("cooldown", SerializableDataTypes.INT, 0),
                (data, e) -> {
                    if (e instanceof ServerPlayerEntity playerEntity) {
                        double mana = data.get("mana");
                        Identifier cooldown_id = data.get("cooldown_id");
                        int cooldown = data.get("cooldown");
                        if (cooldown_id != null && cooldown > 0 && mana > 0) {
                            consumeManaWithCooldown(playerEntity, cooldown_id, cooldown, mana);
                        }
                    }
                })
        );
        ActionRegister.accept(new ActionFactory<Entity>(
                SSCXuAddon.identifier("consume_mana_percent_with_cooldown"),
                new SerializableData()
                        .add("mana_percent", SerializableDataTypes.DOUBLE, 0.0d)
                        .add("cooldown_id", SerializableDataTypes.IDENTIFIER, null)
                        .add("cooldown", SerializableDataTypes.INT, 0),
                (data, e) -> {
                    if (e instanceof ServerPlayerEntity playerEntity) {
                        double mana_percent = data.get("mana_percent");
                        Identifier cooldown_id = data.get("cooldown_id");
                        int cooldown = data.get("cooldown");
                        if (cooldown_id != null && cooldown > 0 && mana_percent > 0) {
                            if (Utils.getCoolDownPassPercent(playerEntity, cooldown_id) < 1.0) {
                                return;
                            }
                            double mana_max = ManaUtils.getPlayerMaxMana(playerEntity);
                            double mana = mana_max * mana_percent;
                            ManaUtils.consumePlayerMana(playerEntity, mana);
                            Utils.triggerLocalCoolDown(playerEntity, cooldown_id, cooldown);
                        }
                    }
                })
        );
    }
}
