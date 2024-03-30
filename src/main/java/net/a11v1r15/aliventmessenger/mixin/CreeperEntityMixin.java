package net.a11v1r15.aliventmessenger.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.render.entity.feature.SkinOverlayOwner;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin
extends HostileEntity
implements SkinOverlayOwner {
	protected CreeperEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
		super(entityType, world);
	}
	
	@SuppressWarnings("resource")
	@Inject(at = @At(value = "HEAD"), method = "explode()V")
	private void aliventMessenger$giveExplosionDamageToExplodingCreeper(CallbackInfo info) {
        if (!this.getWorld().isClient) {
			this.damage(this.getDamageSources().explosion(this.getWorld().createExplosion(null, this.getX(), this.getY(), this.getZ(), 0, World.ExplosionSourceType.NONE)), Float.MAX_VALUE);
		}
	}
	
    @ModifyVariable(
        method = "interactMob(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;",
        at = @At(value = "STORE"), ordinal = 0
        )
    private SoundEvent aliventMessenger$creeperAttackedByIgniter(SoundEvent sound, PlayerEntity player) {
        this.setAttacker(player);
        return sound;
    }
}
