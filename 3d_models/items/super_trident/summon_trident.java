// Made with Blockbench 5.1.4
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class summon_trident extends EntityModel<Entity> {
	private final ModelPart group;
	public summon_trident(ModelPart root) {
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
	public void setAngles(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
	}
	@Override
	public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		group.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}
}