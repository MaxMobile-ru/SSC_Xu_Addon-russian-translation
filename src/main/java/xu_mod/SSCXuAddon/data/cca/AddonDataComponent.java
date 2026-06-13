package xu_mod.SSCXuAddon.data.cca;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class AddonDataComponent implements AutoSyncedComponent {
    private final PlayerEntity componentOwner;
    private final HashMap<Identifier, Long> cooldownData = new HashMap<>();
    private int manaLevel = 1;

    // 每当受到 当前生命值/5 以上的伤害时 消耗1层护盾 减少 当前生命值/5 + shieldStrength数的伤害 之后再将伤害进行计算
    // 示例值: 护盾戒指
    // shieldMax += 3
    // shieldStrength += 5
    // 假设受到10点伤害 当前血量为20 则消耗1层护盾 减少(20 / 5) + 5 = 9点伤害 受到1点伤害 小于 (20 / 5) 停止计算 最终伤害为1 消耗1层护盾
    // 假设受到20点伤害 当前血量为20 则先消耗1层护盾 减少(20 / 5) + 5 = 9点伤害 受到11点伤害 再消耗1层护盾 减少(20 / 5) + 5 = 9点伤害 受到2点伤害 小于 (20 / 5) 停止计算 最终伤害为2 消耗2层护盾
    private int shieldCount = 0;  // 护盾数量
    private int shieldMax = 0;  // 护盾最大值
    private int shieldStrength = 0;  // 护盾强度

    private static final int SpaceBagSlotCount = 27;
    private SimpleInventory SpaceBag = new SimpleInventory(SpaceBagSlotCount);

    public int getManaLevel() {
        return manaLevel;
    }

    public void setManaLevel(int manaLevel) {
        this.manaLevel = manaLevel;
    }

    public HashMap<Identifier, Long> getCooldownData() {
        return cooldownData;
    }

    public long getCooldown(Identifier id, long DefaultCooldown) {
        return cooldownData.getOrDefault(id, DefaultCooldown);
    }

    public void setCooldown(Identifier id, long cooldown) {
        cooldownData.put(id, cooldown);
    }

    public void triggerCooldown(Identifier id) {
        cooldownData.put(id, componentOwner.getWorld().getTime());
    }

    public void resetCooldown(Identifier id) {
        cooldownData.remove(id);
    }

    public boolean isNotInCooldown(Identifier id, long cooldown) {
        return cooldownData.getOrDefault(id, -1728000L) + cooldown <= componentOwner.getWorld().getTime();
    }

    public Inventory getSpaceBag() {
        return SpaceBag;
    }

    public AddonDataComponent(PlayerEntity player) {
        this.componentOwner = player;
    }

    @Override
    public void readFromNbt(NbtCompound nbtCompound) {
        if (nbtCompound.contains("manaLevel")) {
            manaLevel = nbtCompound.getInt("manaLevel");
        }
        if (nbtCompound.contains("cooldownData")) {
            this.cooldownData.clear();
            NbtCompound cooldownCompound = nbtCompound.getCompound("cooldownData");
            for (String id : cooldownCompound.getKeys()) {
                cooldownData.put(new Identifier(id), cooldownCompound.getLong(id));
            }
        }
        if (nbtCompound.contains("spaceBag")) {
            this.SpaceBag = new SimpleInventory(SpaceBagSlotCount);
            Inventories.readNbt(nbtCompound.getCompound("spaceBag"), SpaceBag.stacks);
        }
    }

    @Override
    public void writeToNbt(NbtCompound nbtCompound) {
        nbtCompound.putInt("manaLevel", manaLevel);
        NbtCompound cooldownCompound = new NbtCompound();
        cooldownData.forEach((id, cooldown) -> cooldownCompound.putLong(id.toString(), cooldown));
        nbtCompound.put("cooldownData", cooldownCompound);
        NbtCompound spaceBagCompound = new NbtCompound();
        Inventories.writeNbt(spaceBagCompound, SpaceBag.stacks);
        nbtCompound.put("spaceBag", spaceBagCompound);
    }

    /*
        // 等什么时候用到仅初始化同步的值时再写

    @Override
    public boolean shouldSyncWith(ServerPlayerEntity player) {
        return true;
    }

    @Override
    public void writeSyncPacket(PacketByteBuf buf, ServerPlayerEntity recipient) {
        AutoSyncedComponent.super.writeSyncPacket(buf, recipient);
    }

    @Override
    public void applySyncPacket(PacketByteBuf buf) {
        AutoSyncedComponent.super.applySyncPacket(buf);
    }

     */
}
