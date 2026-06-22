package xu_mod.SSCXuAddon.init.Client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import xu_mod.SSCXuAddon.SSCXuAddon;
import xu_mod.SSCXuAddon.data.entity.minion.SpiderMinionRenderer;
import xu_mod.SSCXuAddon.data.entity.projectiles.BloodThornEntItyRenderer;
import xu_mod.SSCXuAddon.data.entity.projectiles.SummonTridentModel;
import xu_mod.SSCXuAddon.data.entity.projectiles.SummonTridentRenderer;
import xu_mod.SSCXuAddon.init.Init_Entity;

@Environment(EnvType.CLIENT)
public class Init_EntityRenderer {
    public static final EntityModelLayer SUMMON_TRIDENT_LAYER = new EntityModelLayer(SSCXuAddon.identifier("summon_trident"), "main");

    static {
        EntityRendererRegistry.register(Init_Entity.BLOOD_THORN, BloodThornEntItyRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(SUMMON_TRIDENT_LAYER, SummonTridentModel::getTexturedModelData);
        EntityRendererRegistry.register(Init_Entity.SUMMON_TRIDENT, SummonTridentRenderer::new);
        EntityRendererRegistry.register(Init_Entity.SPIDER_MINION, SpiderMinionRenderer::new);
    }

    public static void init() {}
}
