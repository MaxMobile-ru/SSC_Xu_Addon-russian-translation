package xu_mod.SSCXuAddon.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xu_mod.SSCXuAddon.utils.ClientUtils;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "renderHealthBar", at = @At("HEAD"))
    private void renderShield(DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking, CallbackInfo ci) {
        int shieldCount = ClientUtils.getShieldCount();
        if (shieldCount <= 0) return;
        shieldCount = Math.min(shieldCount, 10);
        int StartX = x - 1;
        int IconY = y - 1;
        for (int i = 0; i < shieldCount; i++) {
            ClientUtils.ShieldIcon.draw(context, StartX + i * 8, IconY);
        }
    }
}