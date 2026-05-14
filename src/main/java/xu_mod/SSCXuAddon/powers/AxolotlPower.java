package xu_mod.SSCXuAddon.powers;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Active;
import io.github.apace100.apoli.power.ActiveCooldownPower;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.power.factory.condition.ConditionFactory;
import io.github.apace100.apoli.util.HudRender;
import io.github.apace100.apoli.util.Space;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;
import net.onixary.shapeShifterCurseFabric.mana.ManaUtils;
import org.joml.Vector3f;
import xu_mod.SSCXuAddon.SSCXuAddon;
import xu_mod.SSCXuAddon.data.item.tools.SeaScepter;
import xu_mod.SSCXuAddon.utils.Misc.ExplosionBehaviorExceptBreakBlock;
import xu_mod.SSCXuAddon.utils.Misc.MiscAction;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class AxolotlPower {
    // 改一下结构 使用内嵌Power 让power文件夹简洁一点

    public static class AxolotlSkill1Power extends ActiveCooldownPower {
        public double nRadius = 1.5d;
        public float nDamage = 2.0f;
        public float nKnockPower = 0.5f;

        public double wRadius = 3d;
        public float wDamage = 4.0f;
        public float wExtraDamage = 4.0f;
        public float wKnockPower = 1.2f;
        public int wParticleCount = 24;
        public boolean wForceDamage = true;
        public boolean wHighSound = true;

        public double w2Radius = 3.5d;
        public float w2Damage = 6.0f;
        public float w2ExtraDamage = 12.0f;
        public float w2KnockPower = 1.5f;
        public int w2ParticleCount = 24;
        public boolean w2ForceDamage = true;
        public boolean w2HighSound = true;

        public Consumer<Entity> onInvokeAction = null;
        public Consumer<Entity> onFinalAction = null;

        public long skillTick = 40;

        public float movementSpeedX = 2.5f;  // 初始移动速度X(视角方向) 未调整
        public float movementSpeedY = 0.1f;  // 初始移动速度Y 未调整
        public float minSpd = 0.1f;

        private long skillRemainTick = 0;

        // TODO 仅测试用 发布前一定得记得改
        public float manaCost = 0f;

        public boolean shouldTick = false;

        public AxolotlSkill1Power(PowerType<?> type, LivingEntity entity, int cooldownDuration, HudRender hudRender, Consumer<Entity> activeFunction) {
            super(type, entity, cooldownDuration, hudRender, activeFunction);
        }


        @Override
        public boolean shouldTick() {
            return shouldTick;
        }

        @Override
        public void onUse() {
            if (this.entity instanceof PlayerEntity player && canUse() && (ManaUtils.isPlayerManaAbove(player, manaCost))) {
                Item handItem = player.getMainHandStack().getItem();
                if (handItem instanceof SeaScepter || handItem instanceof TridentItem) {
                    this.skillRemainTick = skillTick;
                    this.shouldTick = true;
                    Vector3f vec = new Vector3f(0, 0, movementSpeedX);
                    Space.LOCAL.toGlobal(vec, this.entity);
                    this.entity.addVelocity(vec.x, vec.y, vec.z);
                    this.entity.addVelocity(0, movementSpeedY, 0);
                    this.entity.velocityModified = true;
                    if (this.onInvokeAction != null) {
                        this.onInvokeAction.accept(this.entity);
                    }
                    this.entity.getWorld().playSound(null, this.entity.getX(), this.entity.getY(), this.entity.getZ(), SoundEvents.ENTITY_GOAT_LONG_JUMP, SoundCategory.PLAYERS, 1.0F, 1.0F);
                } else {
                    MiscAction.WaterExplosion(this.entity, this.entity, this.entity, w2Radius, w2Damage, w2ExtraDamage, w2KnockPower, w2ParticleCount, w2ForceDamage, w2HighSound);
                    this.entity.getWorld().playSound(null, this.entity.getX(), this.entity.getY(), this.entity.getZ(), SoundEvents.ENTITY_WITHER_BREAK_BLOCK, SoundCategory.PLAYERS, 1.0F, 1.0F);
                }
                this.use();
            }
        }

        @Override
        public void tick() {
            if (this.entity.getWorld() instanceof ServerWorld serverWorld) {
                if (this.skillRemainTick <= 0) {
                    this.shouldTick = false;
                    MiscAction.WaterExplosion(this.entity, this.entity, this.entity, wRadius, wDamage, wExtraDamage, wKnockPower, wParticleCount, wForceDamage, wHighSound);
                    this.entity.getWorld().playSound(null, this.entity.getX(), this.entity.getY(), this.entity.getZ(), SoundEvents.ENTITY_WITHER_BREAK_BLOCK, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    if (this.onFinalAction != null) {
                        this.onFinalAction.accept(this.entity);
                    }
                    return;
                }
                for (LivingEntity entity : this.entity.getWorld().getEntitiesByClass(LivingEntity.class, this.entity.getBoundingBox().expand(nRadius), e -> e != this.entity)) {
                    Vec3d direction = entity.getPos().subtract(this.entity.getPos());
                    double distance = direction.length();
                    if (distance > nRadius) {
                        continue;
                    }
                    entity.damage(this.entity.getDamageSources().explosion(this.entity, this.entity), nDamage);
                    entity.takeKnockback(nKnockPower, -direction.x, -direction.z);
                }
                double spd = this.entity.getVelocity().horizontalLength();
                if (spd < minSpd) {
                    this.entity.setVelocity(new Vec3d(0, 0, 0));
                    this.entity.velocityModified = true;
                    skillRemainTick = 0;
                }
                skillRemainTick--;
            }
        }
    }


    public static void registerPower(Consumer<PowerFactory<?>> registerMethod) {
        registerMethod.accept(new PowerFactory<>(
                SSCXuAddon.identifier("axolotl_skill_1"),
                new SerializableData()
                        .add("key", ApoliDataTypes.KEY, null)
                ,
                data -> (type, player) -> {
                    AxolotlSkill1Power power = new AxolotlSkill1Power(type, player, 160, HudRender.DONT_RENDER, null);
                    power.setKey(data.get("key"));
                    return power;
                }
                ));
    }

    public static void registerActions(Consumer<ActionFactory<Entity>> ActionRegister, Consumer<ActionFactory<Pair<Entity, Entity>>> BIActionRegister) {
        ActionRegister.accept(new ActionFactory<>(
                SSCXuAddon.identifier("water_explosion"),
                new SerializableData()
                        .add("radius", SerializableDataTypes.DOUBLE, 3.0d)
                        .add("base_damage", SerializableDataTypes.FLOAT, 4.0f)
                        .add("extra_damage", SerializableDataTypes.FLOAT, 4.0f)
                        .add("knock_power", SerializableDataTypes.FLOAT, 1.0f)
                        .add("particle_count", SerializableDataTypes.INT, 8)
                        .add("force_damage", SerializableDataTypes.BOOLEAN, false)
                        .add("high_sound", SerializableDataTypes.BOOLEAN, false),
                (data, e) -> {
                    if (e instanceof LivingEntity entity) {
                        if (e instanceof Ownable ownable) {
                            MiscAction.WaterExplosion(entity, entity, ownable.getOwner(), data.get("radius"), data.get("base_damage"), data.get("extra_damage"), data.get("knock_power"), data.get("particle_count"), data.get("force_damage"), data.get("high_sound"));
                        } else {
                            MiscAction.WaterExplosion(entity, entity, entity, data.get("radius"), data.get("base_damage"), data.get("extra_damage"), data.get("knock_power"), data.get("particle_count"), data.get("force_damage"), data.get("high_sound"));
                        }
                    }
                }
        ));
        ActionRegister.accept(new ActionFactory<>(
                SSCXuAddon.identifier("charge_scepter"),
                new SerializableData()
                        .add("value", SerializableDataTypes.INT, 5)
                        .add("over_charge", SerializableDataTypes.BOOLEAN, true),
                (data, e) -> {
                    if (e instanceof PlayerEntity player) {
                        ItemStack stack = player.getMainHandStack();
                        if (!(stack.getItem() instanceof SeaScepter)) {
                            stack = player.getOffHandStack();
                        }
                        if (stack.getItem() instanceof SeaScepter) {
                            SeaScepter.charge(stack, player.getWorld(), data.get("value"), data.get("over_charge"));
                        }
                    }
                }
        ));
    }

    public static void registerConditions(Consumer<ConditionFactory<Entity>> ConditionRegister) {

    }


}
