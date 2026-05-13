package xu_mod.SSCXuAddon.data.entity.projectiles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SummonTridentModel extends Model {
    private final ModelPart group;
    public SummonTridentModel(ModelPart root) {
        super(RenderLayer::getEntityTranslucentCull);
        this.group = root.getChild("group");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData group = modelPartData.addChild("group", ModelPartBuilder.create().uv(0, 0).cuboid(-0.5F, -17.5F, -0.5F, 1.0F, 25.0F, 1.0F, new Dilation(0.0F))
                .uv(4, 0).cuboid(-1.5F, -19.5F, -0.5F, 3.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(4, 3).cuboid(1.5F, -22.5F, -0.5F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(4, 3).cuboid(-2.5F, -22.5F, -0.5F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(8, 3).cuboid(-0.5F, -24.0F, -0.5F, 1.0F, 4.5F, 1.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 16.0F, 0.0F));
        return TexturedModelData.of(modelData, 16, 32);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        group.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }
}
