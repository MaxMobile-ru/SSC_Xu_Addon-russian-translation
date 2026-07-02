package xu_mod.SSCXuAddon.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xu_mod.SSCXuAddon.init.Init_Apoli;

import java.util.Set;
import java.util.function.Predicate;

@Mixin(AbstractSkeletonEntity.class)
public abstract class AbstractSkeletonEntityMixin extends HostileEntity implements RangedAttackMob {
    protected AbstractSkeletonEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("TAIL"), method = "initGoals")
    private void addGoals(CallbackInfo info) {
        Set<PrioritizedGoal> goals = this.targetSelector.getGoals();
        for (PrioritizedGoal prioritizedGoal : goals) {
            if (prioritizedGoal.getGoal() instanceof ActiveTargetGoal<?> atg && prioritizedGoal.getPriority() == 2 && atg.targetClass == PlayerEntity.class) {
                Predicate<LivingEntity> targetPredicate = atg.targetPredicate.predicate;
                if (targetPredicate == null) {
                    targetPredicate = e -> !Init_Apoli.SkeletonFriendly.isActive(e);
                } else {
                    targetPredicate = targetPredicate.and(e -> !Init_Apoli.SkeletonFriendly.isActive(e));
                }
                atg.targetPredicate.setPredicate(targetPredicate);
            }
        }
    }
}