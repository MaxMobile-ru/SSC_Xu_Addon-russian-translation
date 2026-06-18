package xu_mod.SSCXuAddon.utils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xu_mod.SSCXuAddon.data.cca.AddonDataComponent;
import xu_mod.SSCXuAddon.init.Init_CCA;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

// Common Side
public class ShieldUtils {
    // Server Side
    public static List<Pair<Predicate<PlayerEntity>, Integer>> maxShieldCountModifiers = new ArrayList<>();
    public static List<Pair<Predicate<PlayerEntity>, Integer>> ShieldStrengthModifiers = new ArrayList<>();
    public static List<Consumer<PlayerEntity>> onShieldBreakCallBack = new ArrayList<>();

    public static int getShieldCount(@Nullable PlayerEntity player) {
        if (player == null) return 0;
        return Init_CCA.AddonData.get(player).shieldCount;
    }

    public static void recalcShieldData(@Nullable PlayerEntity player) {
        if (player == null) return;
        AddonDataComponent addonData = Init_CCA.AddonData.get(player);
        addonData.shieldCount = 0;
        addonData.shieldMax = 0;
        addonData.shieldStrength = 0;
        maxShieldCountModifiers.forEach(pair -> {
            if (pair.getLeft().test(player)) {
                addonData.shieldMax += pair.getRight();
            }
        });
        ShieldStrengthModifiers.forEach(pair -> {
            if (pair.getLeft().test(player)) {
                addonData.shieldStrength += pair.getRight();
            }
        });
        if (addonData.shieldCount > addonData.shieldMax) {
            addonData.shieldCount = addonData.shieldMax;
        }
        Init_CCA.AddonData.sync(player);
    }


    // 每当受到 当前生命值/5 以上的伤害时 消耗1层护盾 减少 当前生命值/5 + shieldStrength数的伤害 之后再将伤害进行计算
    // 示例值: 护盾戒指
    // shieldMax += 3
    // shieldStrength += 5
    // 假设受到10点伤害 当前血量为20 则消耗1层护盾 减少(20 / 5) + 5 = 9点伤害 受到1点伤害 小于 (20 / 5) 停止计算 最终伤害为1 消耗1层护盾
    // 假设受到20点伤害 当前血量为20 则先消耗1层护盾 减少(20 / 5) + 5 = 9点伤害 受到11点伤害 再消耗1层护盾 减少(20 / 5) + 5 = 9点伤害 受到2点伤害 小于 (20 / 5) 停止计算 最终伤害为2 消耗2层护盾
    public static float calcShield(@NotNull PlayerEntity player, float damage) {
        AddonDataComponent addonData = Init_CCA.AddonData.get(player);
        int shieldCount = addonData.shieldCount;
        if (shieldCount <= 0) return damage;
        float playerHealth = player.getHealth();
        float playerHealthPerFive = playerHealth / 5;
        float nowDamage = damage;
        while (shieldCount > 0 && nowDamage >= playerHealthPerFive) {
            int shieldStrength = addonData.shieldStrength;
            float shieldBlockDamage = playerHealthPerFive + shieldStrength;
            nowDamage = Math.max(0, nowDamage - shieldBlockDamage);
            shieldCount --;
            onShieldBreakCallBack.forEach(callBack -> callBack.accept(player));
        }
        addonData.shieldCount = shieldCount;
        Init_CCA.AddonData.sync(player);
        return nowDamage;
    }

    public static float addShield(@NotNull PlayerEntity player, float shield) {
        AddonDataComponent addonData = Init_CCA.AddonData.get(player);
        int shieldCount = addonData.shieldCount;
        int shieldMax = addonData.shieldMax;
        if (shieldCount >= shieldMax) return shield;
        int addShieldCount = Math.min(shieldMax - shieldCount, (int) shield);
        addonData.shieldCount += addShieldCount;
        Init_CCA.AddonData.sync(player);
        return shield - addShieldCount;
    }
}
