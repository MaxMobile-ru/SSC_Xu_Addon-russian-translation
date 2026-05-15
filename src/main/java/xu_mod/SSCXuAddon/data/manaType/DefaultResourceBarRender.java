package xu_mod.SSCXuAddon.data.manaType;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.mana.IManaRender;
import net.onixary.shapeShifterCurseFabric.mana.ManaUtils;
import net.onixary.shapeShifterCurseFabric.util.UIPositionUtils;
import org.jetbrains.annotations.Nullable;

public class DefaultResourceBarRender implements IManaRender {
    private static final MinecraftClient minecraftClient = MinecraftClient.getInstance();
    public final Identifier barTexture;
    public final boolean overrideInstinctBar;
    public final boolean enableTextDisplay;
    public final boolean enableNumberDisplay;

    public DefaultResourceBarRender(Identifier barTexture, boolean overrideInstinctBar, boolean enableTextDisplay, boolean enableNumberDisplay) {
        this.barTexture = barTexture;
        this.overrideInstinctBar = overrideInstinctBar;
        this.enableTextDisplay = enableTextDisplay;
        this.enableNumberDisplay = enableNumberDisplay;
    }

    public DefaultResourceBarRender(Identifier barTexture) {
        this(barTexture, false, true, true);
    }

    @Override
    public boolean OverrideInstinctBar() {
        return overrideInstinctBar;
    }
    public void render(DrawContext context, float tickDelta) {
        if (!minecraftClient.options.hudHidden) {
            Pair<Integer, Integer> pos = UIPositionUtils.getCorrectPosition(ShapeShifterCurseFabric.clientConfig.manaBarPosType, ShapeShifterCurseFabric.clientConfig.manaBarPosOffsetX, ShapeShifterCurseFabric.clientConfig.manaBarPosOffsetY);
            this.renderBar(context, tickDelta, (Integer)pos.getLeft(), (Integer)pos.getRight());
        }
    }

    private void renderBar(DrawContext context, float tickDelta, int x, int y) {
        if (minecraftClient.player == null) {
            return;
        }
        double mana = ManaUtils.getPlayerMana(minecraftClient.player);
        double maxMana = ManaUtils.getPlayerMaxMana(minecraftClient.player);
        double manaRegen = ManaUtils.getPlayerManaRegen(minecraftClient.player);
        int remainTicks = -1;

        if (enableNumberDisplay) {
            if (manaRegen > (double) 0.0F) {
                remainTicks = (int) Math.ceil((maxMana - mana) / manaRegen);
            } else if (manaRegen < (double) 0.0F) {
                remainTicks = (int) Math.ceil((mana) / (-manaRegen));
            } else {
                remainTicks = 0;
            }
        }

        int manaWidth = (int)Math.ceil((double)80.0F * ManaUtils.getManaPercent(mana, maxMana, (double)0.0F));
        context.drawTexture(barTexture, x, y, 0.0f, 0, 80, 5, 80, 10);
        context.drawTexture(barTexture, x, y, 0.0f, 5, manaWidth, 5, 80, 10);
        if (enableTextDisplay) {
            StringBuilder manaString = new StringBuilder();
            manaString.append((int) mana).append("/").append((int) maxMana);
            if (enableNumberDisplay) {
                if (remainTicks > 0) {
                    manaString.append(" (").append(remainTicks).append(")");
                } else if (remainTicks < 0) {
                    manaString.append(" (").append("?").append(")");
                } else {
                    manaString.append(" (").append("∞").append(")");
                }
            }
            Text manaText = Text.literal(manaString.toString());
            context.drawText(minecraftClient.textRenderer, manaText, x, y - 8, 0xFFCC0000, false);
        }
    }
}
