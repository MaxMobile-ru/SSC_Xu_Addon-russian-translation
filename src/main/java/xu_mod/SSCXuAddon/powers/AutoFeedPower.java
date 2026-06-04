package xu_mod.SSCXuAddon.powers;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.onixary.shapeShifterCurseFabric.util.Accessory.AccessoryUtils;
import xu_mod.SSCXuAddon.SSCXuAddon;

import java.util.function.Predicate;

public class AutoFeedPower extends Power {
    private final boolean canOverEat;
    private final int belowHunger;
    private final Predicate<ItemStack> condition;

    public AutoFeedPower(PowerType<?> type, LivingEntity entity, boolean canOverEat, int belowHunger, Predicate<ItemStack> condition) {
        super(type, entity);
        this.setTicking();
        this.canOverEat = canOverEat;
        this.belowHunger = belowHunger;
        this.condition = condition;
    }

    public void tick() {
        if (this.entity instanceof PlayerEntity player && this.entity.age % 20 == 0) {
            ItemStack foodStack = AccessoryUtils.getEntitySlot(player, "auto", "hand", "extra_hand", 0);
            if (foodStack != null && foodStack.isFood() && (this.condition == null || this.condition.test(foodStack))) {
                int PlayerHunger = player.getHungerManager().getFoodLevel();
                int FoodHunger = -1;
                if (foodStack.getItem().getFoodComponent() != null) {
                    FoodHunger = foodStack.getItem().getFoodComponent().getHunger();
                }
                if (FoodHunger > 0 && PlayerHunger < this.belowHunger && (canOverEat || PlayerHunger + FoodHunger <= 20)) {
                    player.eatFood(player.getWorld(), foodStack);
                }
                AccessoryUtils.setEntitySlot(player, "auto", "hand", "extra_hand", 0, foodStack);
            }
        }
    }


    public static PowerFactory<?> createFactory() {
        return new PowerFactory<>(
                SSCXuAddon.identifier("auto_feed"),
                new SerializableData()
                        .add("can_over_eat", SerializableDataTypes.BOOLEAN, false)
                        .add("below_hunger", SerializableDataTypes.INT, 0)
                        .add("item_condition", ApoliDataTypes.ITEM_CONDITION, null),
                data -> (type, entity) -> new AutoFeedPower(type, entity, data.getBoolean("can_over_eat"), data.getInt("below_hunger"), data.get("item_condition"))
        ).allowCondition();
    }
}
