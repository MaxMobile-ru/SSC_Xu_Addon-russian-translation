package xu_mod.SSCXuAddon.data.item.trinket;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.items.accessory.AccessoryItem;
import net.onixary.shapeShifterCurseFabric.mana.ManaUtils;
import net.onixary.shapeShifterCurseFabric.player_form.PlayerFormBase;
import net.onixary.shapeShifterCurseFabric.player_form.ability.FormAbilityManager;
import xu_mod.SSCXuAddon.init.Init_Form;

import java.util.List;

public class MoistureKeptCharm extends AccessoryItem {
    public int MaxManaStore = 300;
    public float ManaRegenStartPercent = 0.55f;
    public int ManaRegenRate = 2;
    public int ManaRegenInWater = 5;
    public static final String StoreManaTag = "axolotl_mana_store";

    public MoistureKeptCharm(Settings settings) {
        super(settings);
    }

    @Override
    public void accessoryTick(ItemStack stack, LivingEntity accessoryOwner, SlotData slotData) {
        // 涉及到大量数据读取 而且没法缓存 所以减缓一下执行速度
        if (accessoryOwner.age % 20 == 0 && accessoryOwner instanceof PlayerEntity player) {
            PlayerFormBase form = FormAbilityManager.getForm(player); // 之前整幻化功能时发现了这个API
            if (!Init_Form.AxolotlSeaKing.equals(form)) {
                return;
            }
            double mana_max = ManaUtils.getPlayerMaxMana(player);
            int mana_store = this.getManaStore(stack);
            if (ManaUtils.getPlayerMana(player) < mana_max * ManaRegenStartPercent) {
                int regenValue = Math.min(ManaRegenRate, mana_store);
                ManaUtils.gainPlayerMana(player, regenValue);
                this.addManaStore(stack, -regenValue);
            }
            if (player.isSubmergedInWater()) {
                this.addManaStore(stack, ManaRegenInWater);
            }
        }
    }

    public int getManaStore(ItemStack stack) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        if (nbtCompound.contains(StoreManaTag)) {
            return nbtCompound.getInt(StoreManaTag);
        }
        this.setManaStore(stack, this.MaxManaStore);
        return this.MaxManaStore;
    }

    public void setManaStore(ItemStack stack, int manaStore) {
        stack.getOrCreateNbt().putInt(StoreManaTag, manaStore);
    }

    public void addManaStore(ItemStack stack, int manaStore) {
        this.setManaStore(stack, Math.max(0, Math.min(this.MaxManaStore, this.getManaStore(stack) + manaStore)));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.ssc_xu_addon.moisture_kept_charm.tooltip").formatted(Formatting.AQUA));
        tooltip.add(Text.translatable("item.ssc_xu_addon.moisture_kept_charm.count", this.getManaStore(stack), this.MaxManaStore).formatted(Formatting.AQUA));
    }
}
