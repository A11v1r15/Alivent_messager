package net.a11v1r15.aliventmessenger.mixin;

import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin
extends HostileEntity {
	protected CreeperEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(at = @At(value = "HEAD"), method = "explode()V")
	private void aliventMessenger$giveExplosionDamageToExplodingCreeper(CallbackInfo info) {
        if (this.getWorld() instanceof ServerWorld serverWorld) {
			this.damage(serverWorld, this.getDamageSources().explosion(null, null),Float.MAX_VALUE);
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
