package xu_mod.SSCXuAddon.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.ITMob;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.axolotl.TransformativeAxolotlEntity;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.bat.TransformativeBatEntity;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.ocelot.TransformativeOcelotEntity;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.spider.TransformativeSpiderEntity;
import net.onixary.shapeShifterCurseFabric.form_giving_custom_entity.wolf.TransformativeWolfEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Utils {
    // 纯服务端数据 如果客户端调用 只会获得一个空HashMap
    public static final HashMap<UUID, Long> sprintingTime = new HashMap<>();
    private static final HashMap<UUID, Vec3d> playerLastPos = new HashMap<>();
    public static final HashMap<UUID, Double> playerSpeed = new HashMap<>();
    public static final HashMap<UUID, Integer> exhaustionTime = new HashMap<>();

    public static final HashMap<UUID, HashMap<Identifier, Integer>> localLastCoolDown = new HashMap<>();
    public static final HashMap<UUID, HashMap<Identifier, Long>> localCoolDown = new HashMap<>();

    public static long MaxSprintingTime = 300;

    public static void Tick(MinecraftServer server) {
        for (PlayerEntity player : server.getPlayerManager().getPlayerList()) {
            UUID uuid = player.getUuid();
            if (player.isSprinting()) {
                sprintingTime.put(uuid, Math.min(MaxSprintingTime, sprintingTime.getOrDefault(uuid, 0L) + 1));
            } else {
                sprintingTime.put(uuid, 0L);
            }
            int playerExhaustionTime = exhaustionTime.getOrDefault(uuid, 0);
            if (playerExhaustionTime > 0) {
                exhaustionTime.put(uuid, playerExhaustionTime - 1);
            } else if (playerExhaustionTime < 0) {
                exhaustionTime.put(uuid, 0);
            }
            if (playerLastPos.containsKey(uuid)) {
                playerSpeed.put(uuid, player.getPos().distanceTo(playerLastPos.get(uuid)));
            }
            playerLastPos.put(uuid, player.getPos());
        }
    }

    public static void triggerLocalCoolDown(PlayerEntity player, Identifier id, int coolDown) {
        localLastCoolDown.computeIfAbsent(player.getUuid(), k -> new HashMap<>()).put(id, coolDown);
        long nowTime = player.getWorld().getTime();
        localCoolDown.computeIfAbsent(player.getUuid(), k -> new HashMap<>()).put(id, nowTime);
    }

    public static double getCoolDownPassPercent(PlayerEntity player, Identifier id) {
        if (!localLastCoolDown.containsKey(player.getUuid())) {
            return 1.0;
        }
        if (!localLastCoolDown.get(player.getUuid()).containsKey(id)) {
            return 1.0;
        }
        if (!localCoolDown.containsKey(player.getUuid())) {
            return 1.0;
        }
        if (!localCoolDown.get(player.getUuid()).containsKey(id)) {
            return 1.0;
        }
        if (localLastCoolDown.get(player.getUuid()).get(id) == 0) {
            return 1.0;
        }
        long nowTime = player.getWorld().getTime();
        return (double) (nowTime - localCoolDown.get(player.getUuid()).get(id)) / localLastCoolDown.get(player.getUuid()).get(id);
    }

    public static boolean IsTransformativeMob(Entity entity) {
        return entity instanceof ITMob;
    }
}
