package xu_mod.SSCXuAddon.data.item.trinket;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Vanishable;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.items.accessory.AccessoryItem;

import java.util.List;

public class MoistureKeptCharm extends AccessoryItem implements Vanishable {
    public int MaxManaStore = 100;
    public static final String StoreManaTag = "axolotl_mana_store";

    public MoistureKeptCharm(Settings settings) {
        super(settings);
    }

    @Override
    public void accessoryTick(ItemStack stack, LivingEntity accessoryOwner, SlotData slotData) {
        // TODO
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
        this.setManaStore(stack, Math.min(this.MaxManaStore, this.getManaStore(stack) + manaStore));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.ssc_xu_addon.moisture_kept_charm.tooltip").formatted(Formatting.AQUA));
        tooltip.add(Text.translatable("item.ssc_xu_addon.moisture_kept_charm.count", this.getManaStore(stack), this.MaxManaStore).formatted(Formatting.AQUA));
    }
}
