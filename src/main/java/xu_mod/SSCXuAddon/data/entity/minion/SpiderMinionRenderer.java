package xu_mod.SSCXuAddon.data.entity.minion;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.SpiderEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class SpiderMinionRenderer extends MobEntityRenderer<SpiderMinion, SpiderEntityModel<SpiderMinion>> {
    private static final Identifier TEXTURE = new Identifier("shape-shifter-curse", "textures/entity/mob/t_spider.png");

    public SpiderMinionRenderer(EntityRendererFactory.Context context) {
        super(context, new SpiderEntityModel<>(context.getPart(EntityModelLayers.SPIDER)), 0.6F);
    }

    public Identifier getTexture(SpiderMinion ocelotEntity) {
        return TEXTURE;
    }

    protected void scale(SpiderMinion entity, MatrixStack matrices, float amount) {
        matrices.scale(0.6F, 0.6F, 0.6F);
    }
}
