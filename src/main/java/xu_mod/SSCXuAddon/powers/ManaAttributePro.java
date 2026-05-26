package xu_mod.SSCXuAddon.powers;

import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.onixary.shapeShifterCurseFabric.mana.ManaUtils;
import org.jetbrains.annotations.Nullable;
import xu_mod.SSCXuAddon.SSCXuAddon;

public class ManaAttributePro extends Power {
    private final @Nullable Identifier modifierID;
    private final @Nullable Identifier conditionID;
    private final ManaUtils.@Nullable Modifier maxManaModifier;
    private final ManaUtils.@Nullable Modifier regenManaModifier;
    private final boolean playerSide;
    public ManaAttributePro(PowerType<?> type, LivingEntity entity, @Nullable Identifier modifierID, @Nullable Identifier conditionID, ManaUtils.@Nullable Modifier maxManaModifier, ManaUtils.@Nullable Modifier manaRegenModifier, boolean playerSide) {
        super(type, entity);
        this.modifierID = modifierID;
        this.conditionID = conditionID;
        this.maxManaModifier = maxManaModifier;
        this.regenManaModifier = manaRegenModifier;
        this.playerSide = playerSide;
    }

    public void onAdded() {
        if (this.modifierID != null && conditionID != null) {
            LivingEntity var2 = this.entity;
            if (var2 instanceof ServerPlayerEntity) {
                ServerPlayerEntity playerEntity = (ServerPlayerEntity)var2;
                if (this.maxManaModifier != null) {
                    ManaUtils.addMaxManaModifier(playerEntity, this.modifierID, this.conditionID, this.maxManaModifier, this.playerSide);
                }

                if (this.regenManaModifier != null) {
                    ManaUtils.addRegenManaModifier(playerEntity, this.modifierID, this.conditionID, this.regenManaModifier, this.playerSide);
                }
            }

        }
    }

    public void onLost() {
        if (this.modifierID != null) {
            LivingEntity var2 = this.entity;
            if (var2 instanceof ServerPlayerEntity) {
                ServerPlayerEntity playerEntity = (ServerPlayerEntity)var2;
                if (this.maxManaModifier != null) {
                    ManaUtils.removeMaxManaModifier(playerEntity, this.modifierID, this.playerSide);
                }

                if (this.regenManaModifier != null) {
                    ManaUtils.removeRegenManaModifier(playerEntity, this.modifierID, this.playerSide);
                }
            }

        }
    }

    public void onRespawn() {
        this.onAdded();
    }

    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                SSCXuAddon.identifier("mana_attribute"),
                new SerializableData()
                        .add("modifierID", SerializableDataTypes.IDENTIFIER, null)
                        .add("conditionID", SerializableDataTypes.IDENTIFIER, null)
                        .add("max_mana_modifier", ManaUtils.SDT_ManaModifier, null)
                        .add("regen_mana_modifier", ManaUtils.SDT_ManaModifier, null)
                        .add("player_side", SerializableDataTypes.BOOLEAN, false),
                (data) -> (type, entity) -> new ManaAttributePro(type, entity, data.get("modifierID"), data.get("conditionID"), data.get("max_mana_modifier"), data.get("regen_mana_modifier"), data.get("player_side"))
        );
    }
}
