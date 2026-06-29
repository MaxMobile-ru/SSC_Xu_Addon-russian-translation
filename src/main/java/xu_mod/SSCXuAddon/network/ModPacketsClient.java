package xu_mod.SSCXuAddon.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.onixary.shapeShifterCurseFabric.ShapeShifterCurseFabric;
import xu_mod.SSCXuAddon.SSCXuAddon;
import xu_mod.SSCXuAddon.data.item.UndeadEssence;
import xu_mod.SSCXuAddon.data.item.trinket.NineLiveCharm;
import xu_mod.SSCXuAddon.init.Init_Config;

public class ModPacketsClient {
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.playerLogin, ModPacketsClient::onPlayerLogin);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.triggerNineLiveCharm, ModPacketsClient::onTriggerNineLiveCharm);
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.triggerUndeadEssenceLikeItem, ModPacketsClient::onTriggerUndeadEssenceLikeItem);
    }

    public static void sendUpdateCustomPlayerConfigPacket() {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(Init_Config.playerCustomConfig.enableFakeBlindPower);
        ClientPlayNetworking.send(ModPackets.syncPlayerCustomConfig, buf);
    }

    private static void onPlayerLogin(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        new Thread(() -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
            }
            ModPacketsClient.sendUpdateCustomPlayerConfigPacket();
        }).start();
    }

    private static void onTriggerNineLiveCharm(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if (client.world == null) {
            SSCXuAddon.LOGGER.error("World is null when receiving active nine live charm packet");
            return;
        }
        PlayerEntity playerEntity = client.world.getPlayerByUuid(buf.readUuid());
        if (playerEntity == null) {
            SSCXuAddon.LOGGER.warn("Can't find player entity when receiving active nine live charm packet");
            return;
        }
        NineLiveCharm.ClientTrigger(playerEntity);
    }

    private static void onTriggerUndeadEssenceLikeItem(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        if (client.world == null) {
            SSCXuAddon.LOGGER.error("World is null when receiving active undead essence like item packet");
            return;
        }
        PlayerEntity playerEntity = client.world.getPlayerByUuid(buf.readUuid());
        if (playerEntity == null) {
            SSCXuAddon.LOGGER.warn("Can't find player entity when receiving active undead essence like item packet");
            return;
        }
        int type = buf.readInt();
        switch (type) {
            case 0 -> {
                UndeadEssence.useForTotemClient(playerEntity);
            }
            default -> {
                SSCXuAddon.LOGGER.warn("Unknown undead essence like item type when receiving active undead essence like item packet");
            }
        }
    }
}
