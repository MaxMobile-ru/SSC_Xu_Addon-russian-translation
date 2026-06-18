package xu_mod.SSCXuAddon.data.item.trinket;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.items.accessory.AccessoryItem;
import net.onixary.shapeShifterCurseFabric.util.Accessory.AccessoryUtils;
import xu_mod.SSCXuAddon.init.Init_Item;
import xu_mod.SSCXuAddon.utils.ShieldUtils;

import java.util.List;
import java.util.function.Predicate;

public class ShieldRing extends AccessoryItem {
    static {
        ShieldUtils.maxShieldCountModifiers.add(
                new Pair<Predicate<PlayerEntity>, Integer>(
                        playerEntity -> {
                            List<ItemStack> rings = AccessoryUtils.getEntitySlot(playerEntity, "auto", "hand", "ring");
                            if (rings != null) {
                                return rings.stream().anyMatch(itemStack -> itemStack.isOf(Init_Item.SHIELD_RING));
                            }
                            return false;
                        },
                        3
                )
        );
        ShieldUtils.ShieldStrengthModifiers.add(
                new Pair<Predicate<PlayerEntity>, Integer>(
                        playerEntity -> {
                            List<ItemStack> rings = AccessoryUtils.getEntitySlot(playerEntity, "auto", "hand", "ring");
                            if (rings != null) {
                                return rings.stream().anyMatch(itemStack -> itemStack.isOf(Init_Item.SHIELD_RING));
                            }
                            return false;
                        },
                        5
                )
        );
    }

    public ShieldRing(Settings settings) {
        super(settings);
    }

    @Override
    public void accessoryTick(ItemStack stack, LivingEntity accessoryOwner, SlotData slotData) {
        if (accessoryOwner instanceof PlayerEntity player) {
            if (!player.getItemCooldownManager().isCoolingDown(Init_Item.SHIELD_RING)) {
                ShieldUtils.addShield(player, 1);
                player.getItemCooldownManager().set(Init_Item.SHIELD_RING, 200);
            }
        }
    }

    @Override
    public void onEquip(ItemStack stack, LivingEntity accessoryOwner, SlotData slotData) {
        if (accessoryOwner instanceof PlayerEntity player) {
            ShieldUtils.recalcShieldData(player);
        }
    }

    @Override
    public void onUnequip(ItemStack stack, LivingEntity accessoryOwner, SlotData slotData) {
        if (accessoryOwner instanceof PlayerEntity player) {
            ShieldUtils.recalcShieldData(player);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.ssc_xu_addon.shield_ring.tooltip").formatted(Formatting.YELLOW));
        tooltip.add(Text.translatable("message.ssc_xu_addon.max_shield_count.p", 3).formatted(Formatting.BLUE));
        tooltip.add(Text.translatable("message.ssc_xu_addon.shield_strength.p", 5).formatted(Formatting.BLUE));
        tooltip.add(Text.translatable("message.ssc_xu_addon.shield_regen.tooltip.p", 1, 10).formatted(Formatting.BLUE));
    }
}
