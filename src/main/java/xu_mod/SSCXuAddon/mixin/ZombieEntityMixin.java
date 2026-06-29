package xu_mod.SSCXuAddon.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xu_mod.SSCXuAddon.init.Init_Apoli;

import java.util.Set;
import java.util.function.Predicate;

@Mixin(ZombieEntity.class)
public abstract class ZombieEntityMixin extends HostileEntity {
    protected ZombieEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("TAIL"), method = "initCustomGoals")
    private void addGoals(CallbackInfo info) {
        Set<PrioritizedGoal> goals = this.targetSelector.getGoals();
        for (PrioritizedGoal prioritizedGoal : goals) {
            if (prioritizedGoal.getGoal() instanceof ActiveTargetGoal<?> atg && prioritizedGoal.getPriority() == 2 && atg.targetClass == PlayerEntity.class) {
                Predicate<LivingEntity> targetPredicate = atg.targetPredicate.predicate;
                if (targetPredicate == null) {
                    targetPredicate = e -> !Init_Apoli.ZombieFriendly.isActive(e);
                } else {
                    targetPredicate = targetPredicate.and(e -> !Init_Apoli.ZombieFriendly.isActive(e));
                }
                atg.targetPredicate.setPredicate(targetPredicate);
            }
        }
    }
}
