package xu_mod.SSCXuAddon.data.entity.projectiles;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import xu_mod.SSCXuAddon.init.Init_Entity;
import xu_mod.SSCXuAddon.utils.Misc.MiscAction;

public class SummonTrident extends PersistentProjectileEntity {

    // Tier1 -> 虚拟海王权杖
    // Tier2 -> 海王权杖

    public static final float Tier1_DAMAGE = 8.0f;
    public static final float Tier2_DAMAGE = 10.0f;
    public static final int Tier1_WE_Rate = 5;
    public static final int Tier2_WE_Rate = 4;
    public static final float WE_Range = 2.0f;
    public static final float WE_BaseDamage = 4.0f;
    public static final float WE_ExtraDamage = 4.0f;
    public static final float WE_KnockPower = 1.0f;
    public static final int WE_ParticleCount = 16;
    public static final boolean WE_ForceDamage = false;

    public int Tier = 1;

    public SummonTrident(EntityType<SummonTrident> summonTridentEntityType, World world) {
        super(summonTridentEntityType, world);
    }

    public SummonTrident(LivingEntity owner, float speed, Vec3d PositionOffset) {
        super(Init_Entity.SUMMON_TRIDENT, owner.getWorld());
        this.setOwner(owner);
        this.setNoGravity(true);
        this.setPos(owner.getX() + PositionOffset.getX(), owner.getY() + owner.getEyeHeight(owner.getPose()) + PositionOffset.getY(), owner.getZ() + PositionOffset.getZ());
        this.setVelocity(owner, owner.getPitch(), owner.getYaw(), 0.0F, speed, 0.0F);
        this.velocityModified = true;
    }

    public SummonTrident(LivingEntity owner, float speed, Vec3d PositionOffset, int Tier) {
        super(Init_Entity.SUMMON_TRIDENT, owner.getWorld());
        this.setOwner(owner);
        this.setNoGravity(true);
        this.setPos(owner.getX() + PositionOffset.getX(), owner.getY() + owner.getEyeHeight(owner.getPose()) + PositionOffset.getY(), owner.getZ() + PositionOffset.getZ());
        this.setVelocity(owner, owner.getPitch(), owner.getYaw(), 0.0F, speed, 0.0F);
        this.velocityModified = true;
        this.Tier = Tier;
    }

    public void selfExplosion() {
        if (Tier == 2) {
            MiscAction.WaterExplosion(this, this, this.getOwner(), 3f, 5f, 5f, 2.0f, 24, true, true);
        } else {
            MiscAction.WaterExplosion(this, this, this.getOwner(), 3f, 4f, 4f, 1.2f, 24, true, true);
        }
        this.kill();
    }

    @Override
    protected ItemStack asItemStack() {
        return ItemStack.EMPTY;
    }

    public void tick() {
        if (!isOwnerAlive()) {
            this.selfExplosion();
        }
        if (this.Tier == 2) {
            if (this.age % Tier2_WE_Rate == 0) {
                MiscAction.WaterExplosion(this, this, this.getOwner(), WE_Range, WE_BaseDamage, WE_ExtraDamage, WE_KnockPower, WE_ParticleCount, WE_ForceDamage, false);
            }
        } else {
            if (this.age % Tier1_WE_Rate == 0) {
                MiscAction.WaterExplosion(this, this, this.getOwner(), WE_Range, WE_BaseDamage, WE_ExtraDamage, WE_KnockPower, WE_ParticleCount, WE_ForceDamage, false);
            }
        }
        super.tick();
    }

    private boolean isOwnerAlive() {
        Entity entity = this.getOwner();
        if (entity != null && entity.isAlive()) {
            return !(entity instanceof ServerPlayerEntity) || !entity.isSpectator();
        } else {
            return false;
        }
    }

    @Override
    public void onEntityHit(EntityHitResult entityHitResult) {
        Entity target = entityHitResult.getEntity();
        Entity owner = this.getOwner();
        DamageSource damageSource = this.getDamageSources().magic();
        if (target.damage(damageSource, this.Tier == 2 ? Tier2_DAMAGE : Tier1_DAMAGE)) {
            if (target.getType() == EntityType.ENDERMAN) {
                return;
            }
            if (target instanceof LivingEntity livingEntityTarget) {
                if (owner instanceof LivingEntity livingEntityOwner) {
                    EnchantmentHelper.onUserDamaged(livingEntityTarget, livingEntityOwner);
                    EnchantmentHelper.onTargetDamaged(livingEntityOwner, livingEntityTarget);
                }

                this.onHit(livingEntityTarget);
            }
        }
        this.selfExplosion();
    }

    @Override
    public void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        this.selfExplosion();
    }

    protected boolean tryPickup(PlayerEntity player) {
        this.selfExplosion();
        return true;
    }

    protected SoundEvent getHitSound() {
        return SoundEvents.ENTITY_AXOLOTL_SPLASH;
    }

    public void onPlayerCollision(PlayerEntity player) {
        return;
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.Tier = nbt.getInt("Tier");
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putInt("Tier", Tier);
    }

    protected float getDragInWater() {
        return 0.99F;
    }

    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return true;
    }
}
