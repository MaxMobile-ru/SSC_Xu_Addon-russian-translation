package xu_mod.SSCXuAddon.powers;

import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import xu_mod.SSCXuAddon.SSCXuAddon;
import xu_mod.SSCXuAddon.data.item.tools.SeaScepter;
import xu_mod.SSCXuAddon.utils.Misc.MiscAction;

import java.util.function.Consumer;

public class AxolotlPower {
    // 改一下结构 使用内嵌Power 让power文件夹简洁一点
    public static void registerPower(Consumer<PowerFactory<?>> registerMethod) {

    }

    public static void registerActions(Consumer<ActionFactory<Entity>> ActionRegister, Consumer<ActionFactory<Pair<Entity, Entity>>> BIActionRegister) {
        ActionRegister.accept(new ActionFactory<>(
                SSCXuAddon.identifier("water_explosion"),
                new SerializableData()
                        .add("radius", SerializableDataTypes.DOUBLE, 3.0d)
                        .add("base_damage", SerializableDataTypes.DOUBLE, 4.0d)
                        .add("extra_damage", SerializableDataTypes.DOUBLE, 4.0d)
                        .add("knock_power", SerializableDataTypes.DOUBLE, 1.0d)
                        .add("particle_count", SerializableDataTypes.INT, 8)
                        .add("force_damage", SerializableDataTypes.BOOLEAN, false)
                        .add("high_sound", SerializableDataTypes.BOOLEAN, false),
                (data, e) -> {
                    if (e instanceof LivingEntity entity) {
                        if (e instanceof Ownable ownable) {
                            MiscAction.WaterExplosion(entity, entity, ownable.getOwner(), data.get("radius"), data.get("base_damage"), data.get("extra_damage"), data.get("knock_power"), data.get("particle_count"), data.get("force_damage"), data.get("high_sound"));
                        } else {
                            MiscAction.WaterExplosion(entity, entity, entity, data.get("radius"), data.get("base_damage"), data.get("extra_damage"), data.get("knock_power"), data.get("particle_count"), data.get("force_damage"), data.get("high_sound"));
                        }
                    }
                }
        ));
        ActionRegister.accept(new ActionFactory<>(
                SSCXuAddon.identifier("charge_scepter"),
                new SerializableData()
                        .add("value", SerializableDataTypes.INT, 5)
                        .add("over_charge", SerializableDataTypes.BOOLEAN, true),
                (data, e) -> {
                    if (e instanceof PlayerEntity player) {
                        ItemStack stack = player.getMainHandStack();
                        if (!(stack.getItem() instanceof SeaScepter)) {
                            stack = player.getOffHandStack();
                        }
                        if (stack.getItem() instanceof SeaScepter) {
                            SeaScepter.charge(stack, player.getWorld(), data.get("value"), data.get("over_charge"));
                        }
                    }
                }
        ));
    }

    public static void registerConditions(Consumer<ConditionFactory<Entity>> ConditionRegister) {

    }


}
