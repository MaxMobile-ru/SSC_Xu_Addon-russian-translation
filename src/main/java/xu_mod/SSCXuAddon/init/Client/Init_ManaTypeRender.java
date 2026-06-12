package xu_mod.SSCXuAddon.init.Client;

import net.onixary.shapeShifterCurseFabric.mana.ManaRegistriesClient;
import xu_mod.SSCXuAddon.SSCXuAddon;
import xu_mod.SSCXuAddon.data.manaType.*;
import xu_mod.SSCXuAddon.init.Init_ManaType;

public class Init_ManaTypeRender {
    static {
        ManaRegistriesClient.registerManaTypeRender(Init_ManaType.FamiliarFoxPurifyMana, new FamiliarFoxPurifyManaRender());
        ManaRegistriesClient.registerManaTypeRender(Init_ManaType.BatBloodResource, new DefaultResourceBarRender(SSCXuAddon.identifier("textures/gui/bat_blood_bar.png"), false, true, true, 0xFFCC0000));
        ManaRegistriesClient.registerManaTypeRender(Init_ManaType.AllayResource, new DefaultResourceBarRender(SSCXuAddon.identifier("textures/gui/allay_bar.png"), false, true, true, 0xFF7F00FF));
        ManaRegistriesClient.registerManaTypeRender(Init_ManaType.OcelotStaminaResource, new StaminaResourceRender());
        ManaRegistriesClient.registerManaTypeRender(Init_ManaType.AxolotlWaterResource, new DefaultResourceBarRender(SSCXuAddon.identifier("textures/gui/water_bar.png"), false, true, true, 0xFF70F3FF));
    }

    public static void init() {}
}
