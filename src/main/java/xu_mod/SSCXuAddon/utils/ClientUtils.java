package xu_mod.SSCXuAddon.utils;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import xu_mod.SSCXuAddon.SSCXuAddon;

public class ClientUtils {
    public static final Identifier ICON = SSCXuAddon.identifier("textures/gui/icon.png");
    public static final int ICON_WIDTH = 256;
    public static final int ICON_HEIGHT = 256;

    public record IconEntry(int x, int y, int width, int height) {
        public void draw(DrawContext context, int x, int y) {
            context.drawTexture(ICON, x, y, this.x, this.y, width, height, ICON_WIDTH, ICON_HEIGHT);
        }
    }

    public static IconEntry ShieldIcon = new IconEntry(0, 0, 11, 11);
}
