package xu_mod.SSCXuAddon.data.entity.minion;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.minion.IMinion;
import net.onixary.shapeShifterCurseFabric.minion.IPlayerEntityMinion;
import net.onixary.shapeShifterCurseFabric.status_effects.EntangledEffectUtils;
import org.jetbrains.annotations.Nullable;
import xu_mod.SSCXuAddon.SSCXuAddon;
import xu_mod.SSCXuAddon.utils.ParticleUtils;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class SpiderMinion extends SpiderEntity implements IMinion<SpiderMinion>, Tameable {
    public static final Identifier minionID = SSCXuAddon.identifier("spider_minion");
    public static TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(SpiderMinion.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);;

    @Override
    public void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(OWNER_UUID, Optional.empty());
    }

    public SpiderMinion(EntityType<? extends SpiderEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public @Nullable EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        Random random = world.getRandom();
        this.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).addPersistentModifier(new EntityAttributeModifier("Random spawn bonus", random.nextTriangular((double)0.0F, 0.11485000000000001), EntityAttributeModifier.Operation.MULTIPLY_BASE));
        if (random.nextFloat() < 0.05F) {
            this.setLeftHanded(true);
        } else {
            this.setLeftHanded(false);
        }
        return entityData;
    }

    public static DefaultAttributeContainer.Builder createMobAttributes() {
        // 由于有承伤机制 得大砍血量 设定上是刺客型生物
        // 速度0.45 +50%
        // 生命12 -25%
        // 攻击3 +50%
        // 4秒中毒2 2点吸血 2秒隐身(隐身时远离所有实体) 50%让被攻击者丢失攻击目标
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.45)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 12.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new FleeEntityGoalEX<MobEntity>(this, MobEntity.class, 3.0F, 0.5d, 0.7d, EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR::test, entity -> entity.hasStatusEffect(StatusEffects.INVISIBILITY)));
        this.goalSelector.add(3, new PounceAtTargetGoal(this, 0.4F));
        this.goalSelector.add(4, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(5, new MinionGoals.Minion_FollowOwnerGoalNoTP(this, 1.0, 10.0F, 2.0F, false));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(7, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(7, new LookAroundGoal(this));
        this.targetSelector.add(1, new MinionGoals.Minion_TrackOwnerAttackerGoal(this, (entity -> !entity.hasStatusEffect(StatusEffects.INVISIBILITY))));
        this.targetSelector.add(2, new MinionGoals.Minion_AttackWithOwnerGoal(this, (entity -> !entity.hasStatusEffect(StatusEffects.INVISIBILITY))));
    }

    @Override
    public void InitMinion(PlayerEntity playerEntity) {
        if (playerEntity instanceof IPlayerEntityMinion iPlayerEntityMinion) {
            iPlayerEntityMinion.shape_shifter_curse$addMinion(this);
        }
        else {
            ShapeShifterCurseFabric.LOGGER.error("PlayerEntity is not IPlayerEntityMinion, It Shouldn't Happen!");
            this.setHealth(0.0f);
        }
    }

    @Override
    public void setOwner(PlayerEntity playerEntity) {
        if (playerEntity == null) {
            this.setOwnerUuid(null);
            return;
        }
        this.setOwnerUuid(playerEntity.getUuid());
        return;
    }

    @Override
    public UUID getMinionOwnerUUID() {
        return this.getOwnerUuid();
    }

    @Override
    public void setMinionOwnerUUID(UUID uuid) {
        this.setOwnerUuid(uuid);
    }

    @Override
    public Identifier getMinionTypeID() {
        return minionID;
    }

    @Override
    public SpiderMinion getSelf() {
        return this;
    }

    @Override
    public @Nullable UUID getOwnerUuid() {
        return (UUID)((Optional)this.dataTracker.get(OWNER_UUID)).orElse((Object)null);
    }

    public void setOwnerUuid(@Nullable UUID uuid) {
        this.dataTracker.set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    @Override
    public EntityView method_48926() {
        return super.getWorld();
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.getOwnerUuid() != null) {
            nbt.putUuid("Owner", this.getOwnerUuid());
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        UUID uUID;
        if (nbt.containsUuid("Owner")) {
            uUID = nbt.getUuid("Owner");
        } else {
            String string = nbt.getString("Owner");
            uUID = ServerConfigHandler.getPlayerUuidByName(this.getServer(), string);
        }
        if (uUID != null) {
            try {
                this.setOwnerUuid(uUID);
            } catch (Throwable var4) {
                this.setOwnerUuid(null);
            }
        }
    }

    public double getMinionDisappearRange() {
        return 1024.0d;
    }

    public int getMinionMaxAge() {
        return 2400;  // 2 * 60 * 20 = 2 min
    }

    public boolean shouldExist() {
        if (this.getWorld().isClient) {
            return true;
        }
        if (this.getMinionOwnerUUID() == null) {
            return false;
        }
        PlayerEntity owner = this.getWorld().getPlayerByUuid(this.getMinionOwnerUUID());
        if (owner == null) {
            return false;
        }
        if (this.squaredDistanceTo(owner) > this.getMinionDisappearRange()) {
            return false;
        }
        if (this.age >= this.getMinionMaxAge()) {
            return false;
        }
        if (owner instanceof IPlayerEntityMinion iPlayerEntityMinion) {
            return iPlayerEntityMinion.shape_shifter_curse$minionExist(this.getMinionTypeID(), this.getUuid());
        }
        return false;
    }

    @Override
    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.shouldExist()) {
            this.setHealth(0.0f);
        }
    }

    @Override
    public void onDeath(DamageSource source) {
        this.playSound(SoundEvents.BLOCK_STONE_BREAK, 1.0f, 1.0f); // 蛛网破坏
        if (!this.getWorld().isClient) {
            ParticleUtils.spawnParticle(this, ParticleTypes.CLOUD, 64, 0.0f, new Vec3d(2.0, 1.0, 2.0), 0.5f);
            for (LivingEntity entity : this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().expand(3f), (entity) -> {
                if (entity == this) {
                    return false;
                }
                if (entity.getUuid().equals(this.getMinionOwnerUUID())) {
                    return false;
                }
                if (entity instanceof SpiderMinion) {
                    return false;
                }
                return true;
            })) {
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 2));
                entity.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 50, 2));
                EntangledEffectUtils.applyEntangledEffect(this.getOwner(), entity, 400);
            }
        }
        if (this.getMinionOwnerUUID() != null && this.getWorld().getPlayerByUuid(this.getMinionOwnerUUID()) instanceof IPlayerEntityMinion iPlayerEntityMinion) {
            // 从玩家召唤物列表中移除
            iPlayerEntityMinion.shape_shifter_curse$removeMinion(this.getMinionTypeID(), this.getUuid());
        }
        this.setOwner(null);
        super.onDeath(source);
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        return EntityDimensions.fixed(0.7f, 0.45f);
    }

    @Override
    public float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 0.45F;
    }

    public boolean tryAttack(Entity target) {
        if (super.tryAttack(target)) {
            this.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 40, 0), this);
            if (target instanceof LivingEntity livingEntity) {
                livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 80, 1), this);
                if (this.getRandom().nextFloat() > 0.50 && target instanceof MobEntity mobEntity) {
                    mobEntity.setTarget(null);
                }
            }
            this.heal(2);
            return true;
        } else {
            return false;
        }
    }

    public static class FleeEntityGoalEX<T extends LivingEntity> extends FleeEntityGoal<T> {
        Predicate<LivingEntity> canStartSelector;

        public FleeEntityGoalEX(PathAwareEntity mob, Class<T> fleeFromType, float distance, double slowSpeed, double fastSpeed, Predicate<LivingEntity> inclusionSelector, Predicate<LivingEntity> canStartSelector) {
            super(mob, fleeFromType, (entity) -> true, distance, slowSpeed, fastSpeed, inclusionSelector);
            this.canStartSelector = canStartSelector;
        }

        @Override
        public boolean canStart() {
            return this.canStartSelector.test(this.mob) && super.canStart();
        }
    }
}
