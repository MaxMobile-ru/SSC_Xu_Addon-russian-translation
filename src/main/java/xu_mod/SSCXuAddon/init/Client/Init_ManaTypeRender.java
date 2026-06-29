package xu_mod.SSCXuAddon.init.Client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.onixary.shapeShifterCurseFabric.mana.ManaRegistriesClient;
import net.onixary.shapeShifterCurseFabric.mana.ManaUtils;
import xu_mod.SSCXuAddon.SSCXuAddon;
import xu_mod.SSCXuAddon.data.manaType.*;
import xu_mod.SSCXuAddon.init.Init_ManaType;

public class Init_ManaTypeRender {
    static {
        ManaRegistriesClient.registerManaTypeRender(Init_ManaType.FamiliarFoxPurifyMana, new FamiliarFoxPurifyManaRender());
        ManaRegistriesClient.registerManaTypeRender(Init_ManaType.BatBloodResource, new DefaultResourceBarRender(SSCXuAddon.identifier("textures/gui/bat_blood_bar.png"), false, true, true, 0xFFCC0000));
        ManaRegistriesClient.registerManaTypeRender(Init_ManaType.AllayResource, new DefaultResourceBarRender(SSCXuAddon.identifier("textures/gui/allay_bar.png"), false, true, true, 0xFF7F00FF));
        ManaRegistriesClient.registerManaTypeRender(Init_ManaType.OcelotStaminaResource, new DefaultResourceBarRender(SSCXuAddon.identifier("textures/gui/stamina_bar.png"), false, true, true, () -> {
            if (MinecraftClient.getInstance().player != null) {
                return ManaUtils.getPlayerManaRegen(MinecraftClient.getInstance().player) == 0 ? 0xFF7F7F7F : 0xFF00CFFF;
            } else {
                return 0xFF00CFFF;
            }
        }));
        ManaRegistriesClient.registerManaTypeRender(Init_ManaType.AxolotlWaterResource, new DefaultResourceBarRender(SSCXuAddon.identifier("textures/gui/water_bar.png"), false, true, true, 0xFF70F3FF));

    }

    public static void init() {}
}
