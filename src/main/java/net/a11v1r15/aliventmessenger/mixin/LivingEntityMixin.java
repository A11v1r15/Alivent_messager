package net.a11v1r15.aliventmessenger.mixin;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import net.a11v1r15.aliventmessenger.AliventMessengerConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin
extends Entity {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow abstract DamageTracker getDamageTracker();
    @Shadow LivingEntity attacker;
    
    @SuppressWarnings("resource")
    @Inject(at = @At(value = "HEAD"), method = "onDeath(Lnet/minecraft/entity/damage/DamageSource;)V")
    private void aliventMessenger$sendAliventMessageToChat(CallbackInfo info) {
        if (!this.getWorld().isClient &&
            this.getWorld().getGameRules().getBoolean(GameRules.SHOW_DEATH_MESSAGES)) {
            if (this.hasCustomName() || AliventMessengerConfig.allMobMessages) {
                final boolean test = ((Object)this instanceof TameableEntity && ((TameableEntity)(Object)this).getOwner() instanceof ServerPlayerEntity);
                this.getServer().getPlayerManager().getPlayerList().forEach(player -> {if (!(test && ((TameableEntity)(Object)this).getOwnerUuid() == player.getUuid())) player.sendMessage(this.getDamageTracker().getDeathMessage(), false);});
            } else if (((Object)this instanceof AllayEntity) && ((AllayEntity)(Object)this).isHoldingItem()) {
                Optional<UUID> likedPlayer = ((AllayEntity)(Object)this).getBrain().getOptionalMemory(MemoryModuleType.LIKED_PLAYER);
                this.getServer().getPlayerManager().getPlayerList().forEach(player -> {if (player.getUuid().equals(likedPlayer.get())) player.sendMessage(this.getDamageTracker().getDeathMessage(), false);});
            } else if(AliventMessengerConfig.villagerMessages &&
                      ((Object)this instanceof VillagerEntity || (Object)this instanceof ZombieVillagerEntity)){
                if ((Object)this instanceof VillagerEntity) {
                    this.getServer().getPlayerManager().getPlayerList().forEach(player -> player.sendMessage(this.getDamageTracker().getDeathMessage(), false));
                } else if ((Object)this instanceof ZombieVillagerEntity && !(((ZombieVillagerEntity)(Object)this).canImmediatelyDespawn(Double.MAX_VALUE))) {
                    this.getServer().getPlayerManager().getPlayerList().forEach(player -> player.sendMessage(this.getDamageTracker().getDeathMessage(), false));
                }
            } else if (AliventMessengerConfig.playerKillMessages &&
                       this.attacker instanceof ServerPlayerEntity) {
                final boolean test = ((Object)this instanceof TameableEntity && ((TameableEntity)(Object)this).getOwner() instanceof ServerPlayerEntity);
                this.getServer().getPlayerManager().getPlayerList().forEach(player -> {if (!(test && ((TameableEntity)(Object)this).getOwnerUuid() == player.getUuid())) player.sendMessage(this.getDamageTracker().getDeathMessage(), false);});
            }
        }
    }

    @WrapWithCondition(
	    method = "onDeath(Lnet/minecraft/entity/damage/DamageSource;)V", 
	    at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;info(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V")
    )
    private boolean aliventMessenger$conditionallyRemoveAliventMessageFromLog(Logger instance, String message, Object p0, Object p1) {
        return !AliventMessengerConfig.aliventMessageServerSpamRemover;
    }
}
