package xu_mod.SSCXuAddon.init.Client;

import net.minecraft.client.item.ModelPredicateProviderRegistry;
import xu_mod.SSCXuAddon.SSCXuAddon;
import xu_mod.SSCXuAddon.data.item.tools.FormStoreStone;
import xu_mod.SSCXuAddon.init.Init_Item;

public class Init_ModelPredicateProvider {
    static {
        ModelPredicateProviderRegistry.register(Init_Item.FORM_STORE_STONE, SSCXuAddon.identifier("form_stone_full"), (stack, world, entity, time) -> {
            return FormStoreStone.getStoredForm(stack) != null ? 1.0F : 0.0F;
        });
    }

    public static void init() {}
}
