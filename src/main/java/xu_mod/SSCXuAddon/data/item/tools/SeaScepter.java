package xu_mod.SSCXuAddon.data.item.tools;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xu_mod.SSCXuAddon.data.entity.projectiles.SummonTrident;

import java.util.List;
import java.util.Optional;

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

    public UseAction getUseAction(ItemStack stack) {
        return getChargeValue(stack) > 10 ? UseAction.BOW : UseAction.NONE;
    }

    public int getMaxUseTime(ItemStack stack) {
        return 24;
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        int charge = getChargeValue(stack);
        if (user instanceof PlayerEntity player && !world.isClient && charge > 10) {
            SummonTrident project = new SummonTrident(user, 2f, new Vec3d(0d,0d,0d));
            world.spawnEntity(project);
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0f, 1.0f);
            setChargeValue(stack, charge - 5);
        }
        return stack;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        // Debug Use
        if (entity.age % 100 == 0 && entity instanceof PlayerEntity player && !world.isClient) {
            setChargeValue(stack, 20);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        // TODO 能量值文本
    }
}
