package net.a11v1r15.aliventmessenger.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import net.a11v1r15.aliventmessenger.AliventMessengerConfig;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

@Mixin(TameableEntity.class)
public abstract class TameableEntityMixin extends AnimalEntity {

    protected TameableEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @WrapWithCondition(
	    method = "onDeath(Lnet/minecraft/entity/damage/DamageSource;)V", 
	    at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;sendMessage(Lnet/minecraft/text/Text;)V")
    )
    private boolean aliventMessenger$conditionallyUnprivateTameableDeathMessage(ServerPlayerEntity instance, Text message) {
        return !(this.hasCustomName() || AliventMessengerConfig.allMobMessages ||
                (AliventMessengerConfig.playerKillMessages && this.getAttacker() instanceof ServerPlayerEntity));
    }
}
