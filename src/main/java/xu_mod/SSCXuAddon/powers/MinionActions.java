package xu_mod.SSCXuAddon.powers;

import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import net.onixary.shapeShifterCurseFabric.minion.IPlayerEntityMinion;
import net.onixary.shapeShifterCurseFabric.minion.MinionRegister;
import xu_mod.SSCXuAddon.SSCXuAddon;
import xu_mod.SSCXuAddon.data.entity.minion.SpiderMinion;
import xu_mod.SSCXuAddon.init.Init_Entity;

import java.util.function.Consumer;

public class MinionActions {
    public static void summonMinionSpider(SerializableData.Instance data, Pair<Entity, Entity> entities) {
        Entity Owner = entities.getLeft();
        Entity SpawnNearbyTarget = entities.getRight();
        boolean UseHunger = data.getBoolean("use_hunger");
        int MinionCount = data.getInt("count");
        int MaxMinionCount = data.getInt("max_minion_count");
        int Cooldown = data.getInt("cooldown");
        ActionFactory<Entity>.Instance OwnerAction = data.get("owner_action");
        if (Owner instanceof ServerPlayerEntity player) {
            boolean IsSummonSuccess = false;
            for (int i = 0; i < MinionCount; i++) {
                if (player instanceof IPlayerEntityMinion playerEntityMinion) {
                    if (playerEntityMinion.shape_shifter_curse$getMinionsCount(SpiderMinion.minionID) >= MaxMinionCount) {
                        return;
                    }
                    if (MinionRegister.IsInCoolDown(SpiderMinion.minionID, player, Cooldown)) {
                        return;
                    }
                    if (UseHunger && player.getHungerManager().getFoodLevel() <= 10) {
                        return;
                    }
                }
                else {
                    SSCXuAddon.LOGGER.warn("Can't spawn minion, player is not IPlayerEntityMinion");
                    return;
                }
                BlockPos targetPos = MinionRegister.getNearbyEmptySpace(SpawnNearbyTarget.getWorld(), player.getRandom(), SpawnNearbyTarget.getBlockPos(), 3, 1, 1, 4);
                if (targetPos == null) {
                    targetPos = SpawnNearbyTarget.getBlockPos();
                }
                if (SpawnNearbyTarget.getWorld() instanceof ServerWorld world) {
                    SpiderMinion minion = MinionRegister.SpawnMinion(Init_Entity.SPIDER_MINION, world, targetPos, player);
                    if (minion != null) {
                        IsSummonSuccess = true;
                    } else {
                        SSCXuAddon.LOGGER.warn("Can't spawn minion, minion is null");
                    }
                } else {
                    SSCXuAddon.LOGGER.warn("Can't spawn minion, world is not ServerWorld");
                }
            }
            if (IsSummonSuccess) {
                MinionRegister.SetCoolDown(SpiderMinion.minionID, player);
                if (OwnerAction != null) {
                    OwnerAction.accept(Owner);
                }
                // 添加音效与粒子效果
                if (!(player.getWorld() instanceof ServerWorld serverWorld)) {
                    return;
                }
                if (UseHunger) {
                    player.getHungerManager().setFoodLevel(player.getHungerManager().getFoodLevel() - 4);
                }
                player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_SPIDER_AMBIENT, player.getSoundCategory(), 1.0f, 1.5f);
                serverWorld.spawnParticles(player, ParticleTypes.SOUL_FIRE_FLAME, true, player.getBlockPos().getX() + 0.5f, player.getBlockPos().getY() + 0.5f, player.getBlockPos().getZ() + 0.5f, 8, 0, 0, 0, 0);
            }
        }
    }

    public static void registerAction(Consumer<ActionFactory<Entity>> ActionRegister, Consumer<ActionFactory<Pair<Entity, Entity>>> BIActionRegister) {
        ActionRegister.accept(new ActionFactory<>(
                SSCXuAddon.identifier("summon_spider_minion"),
                new SerializableData()
                        .add("use_hunger", SerializableDataTypes.BOOLEAN, true)
                        .add("count", SerializableDataTypes.INT, 1)
                        .add("max_minion_count", SerializableDataTypes.INT, Integer.MAX_VALUE)
                        .add("cooldown", SerializableDataTypes.INT, 0)
                        .add("owner_action", ApoliDataTypes.ENTITY_ACTION, null),
                (data, entity) -> {summonMinionSpider(data, new Pair<>(entity, entity));}
        ));
    }
}
