package xu_mod.SSCXuAddon.data.entity.minion;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SpiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.EntityView;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.minion.IMinion;
import net.onixary.shapeShifterCurseFabric.minion.IPlayerEntityMinion;
import org.jetbrains.annotations.Nullable;
import xu_mod.SSCXuAddon.SSCXuAddon;

import java.util.Optional;
import java.util.UUID;

public class SpiderMinion extends SpiderEntity implements IMinion<SpiderMinion>, Tameable {
    public static final Identifier minionID = SSCXuAddon.identifier("spider_minion");
    public static TrackedData<Optional<UUID>> OWNER_UUID;

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

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new PounceAtTargetGoal(this, 0.4F));
        this.goalSelector.add(3, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(4, new MinionGoals.Minion_FollowOwnerGoalNoTP(this, 1.0, 10.0F, 2.0F, false));
        this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8));
        this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(6, new LookAroundGoal(this));
        this.targetSelector.add(1, new MinionGoals.Minion_TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new MinionGoals.Minion_AttackWithOwnerGoal(this));
    }

    @Override
    public void InitMinion(PlayerEntity playerEntity) {
        if (playerEntity instanceof IPlayerEntityMinion iPlayerEntityMinion) {
            iPlayerEntityMinion.shape_shifter_curse$addMinion(this);
        }
        else {
            ShapeShifterCurseFabric.LOGGER.error("PlayerEntity is not IPlayerEntityMinion, It Shouldn't Happen!");
            this.setHealth(0.0f);   // 自动死亡
        }
    }

    @Override
    public void setOwner(PlayerEntity playerEntity) {
        this.setOwnerUuid(playerEntity.getUuid());
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
        return 1024.0d;  // 32格外自动消失 如果不需要这个功能可以填Double.MAX_VALUE 如果没有让召唤物强制传送功能必须要设置一个合理的值 否则召唤物可能会卸载
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
        if (owner instanceof IPlayerEntityMinion iPlayerEntityMinion) {
            return iPlayerEntityMinion.shape_shifter_curse$minionExist(this.getMinionTypeID(), this.getUuid());
        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.shouldExist()) {
            this.setHealth(0.0f);  // 自动死亡
        }
    }

    // 从玩家召唤物列表中移除
    @Override
    public void onDeath(DamageSource source) {
        if (this.getMinionOwnerUUID() != null && this.getWorld().getPlayerByUuid(this.getMinionOwnerUUID()) instanceof IPlayerEntityMinion iPlayerEntityMinion) {
            iPlayerEntityMinion.shape_shifter_curse$removeMinion(this.getMinionTypeID(), this.getUuid());
        }
        this.setOwner(null);
        super.onDeath(source);
    }
}
