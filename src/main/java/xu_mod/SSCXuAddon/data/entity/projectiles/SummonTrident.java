package xu_mod.SSCXuAddon.data.entity.projectiles;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import xu_mod.SSCXuAddon.init.Init_Entity;

public class SummonTrident extends PersistentProjectileEntity {
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

    @Override
    protected ItemStack asItemStack() {
        return ItemStack.EMPTY;
    }


    public void tick() {
        Entity entity = this.getOwner();
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

    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity target = entityHitResult.getEntity();
        float f = 8.0F;
        Entity owner = this.getOwner();
        DamageSource damageSource = this.getDamageSources().magic();
        SoundEvent soundEvent = SoundEvents.ITEM_TRIDENT_HIT;
        if (target.damage(damageSource, f)) {
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

        this.setVelocity(this.getVelocity().multiply(-0.01, -0.1, -0.01));
        float g = 1.0F;
        this.playSound(soundEvent, g, 1.0F);
    }

    protected boolean tryPickup(PlayerEntity player) {
        return false;
    }

    protected SoundEvent getHitSound() {
        return SoundEvents.ITEM_TRIDENT_HIT_GROUND;
    }

    public void onPlayerCollision(PlayerEntity player) {
        return;
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
    }

    protected float getDragInWater() {
        return 0.99F;
    }

    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return true;
    }
}
