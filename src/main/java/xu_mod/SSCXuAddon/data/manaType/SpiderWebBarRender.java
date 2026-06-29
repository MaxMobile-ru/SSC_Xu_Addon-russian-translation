package xu_mod.SSCXuAddon.data.manaType;

import io.github.apace100.apoli.component.PowerHolderComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.additional_power.ChargePower;
import net.onixary.shapeShifterCurseFabric.mana.IManaRender;
import net.onixary.shapeShifterCurseFabric.mana.ManaUtils;
import net.onixary.shapeShifterCurseFabric.util.UIPositionUtils;
import xu_mod.SSCXuAddon.SSCXuAddon;
import xu_mod.SSCXuAddon.init.Init_CCA;

public class SpiderWebBarRender implements IManaRender {
    private ChargePower powerTemp_1;
    private int powerTempTimer = 0;

    private static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final Identifier BarTexID = SSCXuAddon.identifier("textures/gui/web_bar.png");

    @Override
    public boolean OverrideInstinctBar() {
        return false;
    }

    public void render(DrawContext context, float tickDelta) {
        if (!mc.options.hudHidden) {
            Pair<Integer, Integer> pos = UIPositionUtils.getCorrectPosition(ShapeShifterCurseFabric.clientConfig.manaBarPosType, ShapeShifterCurseFabric.clientConfig.manaBarPosOffsetX, ShapeShifterCurseFabric.clientConfig.manaBarPosOffsetY);
            this.renderBar(context, tickDelta, (Integer)pos.getLeft(), (Integer)pos.getRight());
        }
    }

    public int getChargeLevel() {
        // 每帧查一次有点费性能 还是每60帧查一次吧(渲染帧)
        if (powerTempTimer > 60) {
            powerTemp_1 = null;
            for (ChargePower power : PowerHolderComponent.getPowers(mc.player, ChargePower.class)) {
                if (SSCXuAddon.identifier("web_charge").equals(power.chargePowerID)) {
                    powerTemp_1 = power;
                }
            }
            powerTempTimer = 0;
        }
        powerTempTimer++;
        int RTier = 0;
        if (powerTemp_1 != null) {
            RTier = Math.max(RTier, powerTemp_1.renderTier);
        }
        return RTier;
    }

    private void renderBar(DrawContext context, float tickDelta, int x, int y) {
        if (mc.player == null) {
            return;
        }
        double mana = ManaUtils.getPlayerMana(mc.player);
        double maxMana = ManaUtils.getPlayerMaxMana(mc.player);
        double manaRegen = ManaUtils.getPlayerManaRegen(mc.player);
        int remainTicks = -1;

        if (manaRegen > (double) 0.0F) {
            remainTicks = (int) Math.ceil((maxMana - mana) / manaRegen);
        } else if (manaRegen < (double) 0.0F) {
            remainTicks = (int) Math.ceil((mana) / (-manaRegen));
        } else {
            remainTicks = 0;
        }

        int manaWidth = (int)Math.ceil((double)80.0F * ManaUtils.getManaPercent(mana, maxMana, (double)0.0F));
        context.drawTexture(BarTexID, x, y, 0.0f, 0, 80, 5, 80, 26);
        context.drawTexture(BarTexID, x, y, 0.0f, 5, manaWidth, 5, 80, 26);

        StringBuilder manaString = new StringBuilder();
        manaString.append((int) mana).append("/").append((int) maxMana);
        if (remainTicks > 0) {
            manaString.append(" (").append(remainTicks).append(")");
        } else if (remainTicks < 0) {
            manaString.append(" (").append("?").append(")");
        } else {
            manaString.append(" (").append("∞").append(")");
        }
        Text manaText = Text.literal(manaString.toString());
        context.drawText(mc.textRenderer, manaText, x + 18, y - 8, 0xFF7F7F7F, false);

        int chargeLevel = this.getChargeLevel();
        context.drawTexture(BarTexID, x, y - 8, chargeLevel * 8f, 10f, 8, 8, 80, 26);

        // 不死能力冷却 Icon 2 min 8个阶段
        long nowCooldown = Init_CCA.AddonData.get(mc.player).getCooldown(SSCXuAddon.identifier("spider_undead_undying"), -1728000L);
        long cooldownPass = mc.player.getWorld().getTime() - nowCooldown;
        int cooldownStage = (int) Math.min(7, cooldownPass / 300);
        context.drawTexture(BarTexID, x + 8, y - 8, cooldownStage * 8f, 18f, 8, 8, 80, 26);
    }
}
