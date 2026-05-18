package xu_mod.SSCXuAddon.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xu_mod.SSCXuAddon.init.Init_Apoli;

@Mixin(StatusEffectUtil.class)
public class StatusEffectUtilMixin {
    // 一直挂一个水下呼吸buff有点难看 还是直接Mixin吧
    @Inject(method = "hasWaterBreathing", at = @At("HEAD"), cancellable = true)
    private static void hasWaterBreathingMixin(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (Init_Apoli.WaterBreathing.isActive(entity)) {
            cir.setReturnValue(true);
            return;
        }
    }
}
