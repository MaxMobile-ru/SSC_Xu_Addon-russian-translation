package xu_mod.SSCXuAddon.data.item.tools;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xu_mod.SSCXuAddon.data.entity.projectiles.SummonTrident;
import xu_mod.SSCXuAddon.utils.Misc.MiscAction;

import java.util.List;

public class SeaScepter extends SwordItem {
    public static final long OverChargeStartLostTime = 300; // 15s
    public static final long OverChargeLoseRate = 100; // 5s

    public int maxCharge = 10;
    public int startCharge = 10;
    public boolean isVirtual = false;

    public SeaScepter(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings, int maxCharge, int startCharge, boolean isVirtual) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
        this.maxCharge = maxCharge;
        this.startCharge = startCharge;
        this.isVirtual = isVirtual;
    }

    public static int getChargeValue(ItemStack stack) {
        if (!stack.getOrCreateNbt().contains("charge")) {
            Item item = stack.getItem();
            if (item instanceof SeaScepter seaScepter) {
                setChargeValue(stack, seaScepter.startCharge);
                stack.getOrCreateNbt().putLong("lastChargeTime", -1);
            }
        }
        return stack.getOrCreateNbt().getInt("charge");
    }

    public static void setChargeValue(ItemStack stack, int value) {
        stack.getOrCreateNbt().putInt("charge", value);
    }

    public static void modChargeValue(ItemStack stack, int value) {
        setChargeValue(stack, getChargeValue(stack) + value);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        int charge = getChargeValue(stack);
        if (charge > 0 || this.isVirtual) {
            target.lastDamageTaken = 0;
            target.damage(attacker.getDamageSources().magic(), charge > this.maxCharge ? 8.0f : 4.0f);
            if (charge > this.maxCharge) {
                MiscAction.WaterExplosion(target, attacker, attacker, 2.0d, 3.0f, 3.0f, 0.5f, 8, true, false);
            }
            setChargeValue(stack, charge - 1);
            if (charge < 1 && this.isVirtual) {
                stack.damage(1000000, attacker, (e) -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
            }
        }
        stack.damage(1, attacker, (e) -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        return true;
    }

    public UseAction getUseAction(ItemStack stack) {
        return getChargeValue(stack) > this.maxCharge || this.isVirtual ? UseAction.BOW : UseAction.NONE;
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
        if (user instanceof PlayerEntity player && !world.isClient && (charge > this.maxCharge || this.isVirtual)) {
            SummonTrident project = new SummonTrident(user, 2f, new Vec3d(0d,0d,0d), this.isVirtual ? 1 : 2);
            world.spawnEntity(project);
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0f, 1.0f);
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_AXOLOTL_SPLASH, SoundCategory.PLAYERS, 1.0f, 1.0f);
            setChargeValue(stack, charge - 5);
            if (charge < 5 && this.isVirtual) {
                stack.damage(1000000, user, (e) -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
            }
        }
        return stack;
    }

    public static void charge(ItemStack stack, World world, int value, boolean overCharge) {
        Item item = stack.getItem();
        if (item instanceof SeaScepter seaScepter) {
            int charge = getChargeValue(stack);
            if (charge < seaScepter.maxCharge) {
                charge = seaScepter.maxCharge;
            }
            setChargeValue(stack, charge + value);
            stack.getOrCreateNbt().putLong("lastChargeTime", world.getTime());
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (entity.age % 20 == 0) {
            NbtCompound nbt = stack.getOrCreateNbt();
            long lastChargeTime = nbt.getLong("lastChargeTime");
            long lastLoseChargeTime = nbt.getLong("lastLoseChargeTime");
            int charge = getChargeValue(stack);
            if (lastChargeTime > world.getTime() || lastChargeTime <= 0) {
                nbt.putLong("lastChargeTime", world.getTime());
            }
            if (lastLoseChargeTime > world.getTime()) {
                nbt.putLong("lastLoseChargeTime", world.getTime());
            }
            if (charge > this.maxCharge && lastChargeTime < world.getTime() - OverChargeStartLostTime) {
                if (lastLoseChargeTime < world.getTime() - OverChargeLoseRate) {
                    modChargeValue(stack, -1);
                    nbt.putLong("lastLoseChargeTime", world.getTime());
                }
            }
        }
    }

    public void selfExplosion(ItemStack stack, World world, LivingEntity user, EquipmentSlot hand) {
        // 只有虚拟的有爆炸触发 但是还是写上正常版本的自爆代码吧
        if (this.isVirtual) {
            MiscAction.WaterExplosion(user, user, user, 3.75f, 6f, 12f, 1.25f, 24, true, true);
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1.0f, 1.0f);
            stack.damage(1000000, user, (e) -> e.sendEquipmentBreakStatus(hand));
        } else {
            MiscAction.WaterExplosion(user, user, user, 4f, 6f, 18f, 1.5f, 24, true, true);
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 1.0f, 1.0f);
            setChargeValue(stack, 0);
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        // TODO 能量值文本
    }
}
