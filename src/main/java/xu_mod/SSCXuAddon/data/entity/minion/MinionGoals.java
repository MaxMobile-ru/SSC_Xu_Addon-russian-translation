package xu_mod.SSCXuAddon.data.entity.minion;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Tameable;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.MobEntity;
import net.onixary.shapeShifterCurseFabric.minion.IMinion;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class MinionGoals {
    public static class Minion_TrackOwnerAttackerGoal extends TrackTargetGoal {
        private final LivingEntity tameableEntity;
        private final IMinion<?> tameable;
        private LivingEntity attacker;
        private int lastAttackedTime;
        private @Nullable Predicate<LivingEntity> condition = null;

        public Minion_TrackOwnerAttackerGoal(IMinion<? extends MobEntity> tameable, @Nullable Predicate<LivingEntity> condition) {
            super(tameable.getSelf(), false);
            this.tameable = tameable;
            this.tameableEntity = (LivingEntity) tameable;
            this.setControls(EnumSet.of(Control.TARGET));
            this.condition = condition;
        }

        public LivingEntity getOwner() {
            return this.tameableEntity.getWorld().getPlayerByUuid(this.tameable.getMinionOwnerUUID());
        }

        public boolean canStart() {
            if (this.condition != null && !this.condition.test(this.tameableEntity)) {
                return false;
            }
            LivingEntity livingEntity = this.getOwner();
            if (livingEntity == null) {
                return false;
            } else {
                this.attacker = livingEntity.getAttacker();
                if (this.attacker != null && this.attacker instanceof Tameable pet && !Objects.equals(pet.getOwner(), livingEntity)) {
                    return false;
                }
                int i = livingEntity.getLastAttackedTime();
                return i != this.lastAttackedTime && this.canTrack(this.attacker, TargetPredicate.DEFAULT);
            }
        }

        public void start() {
            this.mob.setTarget(this.attacker);
            LivingEntity livingEntity = this.getOwner();
            if (livingEntity != null) {
                this.lastAttackedTime = livingEntity.getLastAttackedTime();
            }
            super.start();
        }
    }

    public static class Minion_AttackWithOwnerGoal extends TrackTargetGoal {
        private final MobEntity tameableEntity;
        private final IMinion<?> tameable;
        private LivingEntity attacking;
        private int lastAttackTime;
        private @Nullable Predicate<LivingEntity> condition = null;

        public Minion_AttackWithOwnerGoal(IMinion<? extends MobEntity> tameable, @Nullable Predicate<LivingEntity> condition) {
            super(tameable.getSelf(), false);
            this.tameable = tameable;
            this.tameableEntity = tameable.getSelf();
            this.setControls(EnumSet.of(Control.TARGET));
            this.condition = condition;
        }

        public LivingEntity getOwner() {
            return this.tameableEntity.getWorld().getPlayerByUuid(this.tameable.getMinionOwnerUUID());
        }

        public boolean canStart() {
            if (this.condition != null && !this.condition.test(this.tameableEntity)) {
                return false;
            }
            LivingEntity livingEntity = this.getOwner();
            if (livingEntity == null) {
                return false;
            } else {
                this.attacking = livingEntity.getAttacking();
                if (this.attacking != null && this.attacking instanceof Tameable pet && !Objects.equals(pet.getOwner(), livingEntity)) {
                    return false;
                }
                int i = livingEntity.getLastAttackTime();
                return i != this.lastAttackTime && this.canTrack(this.attacking, TargetPredicate.DEFAULT);
            }
        }

        public void start() {
            this.mob.setTarget(this.attacking);
            LivingEntity livingEntity = this.getOwner();
            if (livingEntity != null) {
                this.lastAttackTime = livingEntity.getLastAttackTime();
            }
            super.start();
        }
    }

    public static class Minion_FollowOwnerGoalNoTP extends Goal {
        private final MobEntity tameableEntity;
        private final IMinion<?> tameable;
        private LivingEntity owner;
        private final double speed;
        private final EntityNavigation navigation;
        private int updateCountdownTicks;
        private final float maxDistance;
        private final float minDistance;
        private float oldWaterPathfindingPenalty;

        public Minion_FollowOwnerGoalNoTP(IMinion<? extends MobEntity> tameable, double speed, float minDistance, float maxDistance, boolean leavesAllowed) {
            this.tameable = tameable;
            this.tameableEntity = tameable.getSelf();
            this.speed = speed;
            this.navigation = this.tameableEntity.getNavigation();
            this.minDistance = minDistance;
            this.maxDistance = maxDistance;
            this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
            if (!(this.tameableEntity.getNavigation() instanceof MobNavigation) && !(this.tameableEntity.getNavigation() instanceof BirdNavigation)) {
                throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
            }
        }

        public LivingEntity getOwner() {
            return this.tameableEntity.getWorld().getPlayerByUuid(this.tameable.getMinionOwnerUUID());
        }

        public boolean canStart() {
            LivingEntity livingEntity = this.getOwner();
            if (livingEntity == null) {
                return false;
            } else if (livingEntity.isSpectator()) {
                return false;
            } else if (this.cannotFollow()) {
                return false;
            } else if (this.tameableEntity.squaredDistanceTo(livingEntity) < (double)(this.minDistance * this.minDistance)) {
                return false;
            } else {
                this.owner = livingEntity;
                return true;
            }
        }

        public boolean shouldContinue() {
            if (this.navigation.isIdle()) {
                return false;
            } else if (this.cannotFollow()) {
                return false;
            } else {
                return !(this.tameableEntity.squaredDistanceTo(this.owner) <= (double)(this.maxDistance * this.maxDistance));
            }
        }

        private boolean cannotFollow() {
            return this.tameableEntity.hasVehicle() || this.tameableEntity.isLeashed();
        }

        public void start() {
            this.updateCountdownTicks = 0;
            this.oldWaterPathfindingPenalty = this.tameableEntity.getPathfindingPenalty(PathNodeType.WATER);
            this.tameableEntity.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
        }

        public void stop() {
            this.owner = null;
            this.navigation.stop();
            this.tameableEntity.setPathfindingPenalty(PathNodeType.WATER, this.oldWaterPathfindingPenalty);
        }

        public void tick() {
            this.tameableEntity.getLookControl().lookAt(this.owner, 10.0F, (float)this.tameableEntity.getMaxLookPitchChange());
            if (--this.updateCountdownTicks <= 0) {
                this.updateCountdownTicks = this.getTickCount(10);
                this.navigation.startMovingTo(this.owner, this.speed);
            }
        }

        private int getRandomInt(int min, int max) {
            return this.tameableEntity.getRandom().nextInt(max - min + 1) + min;
        }
    }
}
