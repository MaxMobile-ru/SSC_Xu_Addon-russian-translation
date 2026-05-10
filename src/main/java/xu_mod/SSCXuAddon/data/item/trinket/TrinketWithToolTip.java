package xu_mod.SSCXuAddon.data.item.trinket;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.annotation.Nullable;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.items.accessory.AccessoryItem;

import java.util.List;

public class TrinketWithToolTip extends AccessoryItem {
    private final List<Text> tooltips;

    public TrinketWithToolTip(Settings settings, Text... tooltip) {
        super(settings);
        this.tooltips = List.of(tooltip);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.addAll(this.tooltips);
    }
}
