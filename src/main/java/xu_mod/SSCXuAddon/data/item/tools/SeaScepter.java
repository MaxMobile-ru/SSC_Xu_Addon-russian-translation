package xu_mod.SSCXuAddon.data.item.tools;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

public class SeaScepter extends SwordItem {
    public SeaScepter(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    public static int getChargeValue(ItemStack stack) {
        return stack.getOrCreateNbt().getInt("charge");
    }

    public static void setChargeValue(ItemStack stack, int value) {
        stack.getOrCreateNbt().putInt("charge", value);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        int charge = getChargeValue(stack);
        if (charge > 0) {
            target.lastDamageTaken = 0;
            target.damage(attacker.getDamageSources().magic(), charge > 10 ? 12.0f : 8.0f);
            setChargeValue(stack, charge - 1);
        }
        stack.damage(1, attacker, (e) -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        return true;
    }
}
