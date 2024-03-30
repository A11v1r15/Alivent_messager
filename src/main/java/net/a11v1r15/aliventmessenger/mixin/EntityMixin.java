package net.a11v1r15.aliventmessenger.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.a11v1r15.aliventmessenger.AliventMessengerConfig;
import net.minecraft.SharedConstants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityLike;

@Mixin(Entity.class)
public abstract class EntityMixin
implements Nameable,
EntityLike,
CommandOutput {
    @Shadow public World world;
    @SuppressWarnings("deprecation")
    @ModifyVariable(
        method = "dropStack(Lnet/minecraft/item/ItemStack;F)Lnet/minecraft/entity/ItemEntity;",
        at = @At(value = "STORE"), ordinal = 0
        )
    private ItemEntity aliventMessenger$giveLoreNameToDroppings(ItemEntity x) {
        if(AliventMessengerConfig.loreDrops && this.hasCustomName()){
            if(SharedConstants.DATA_PACK_VERSION < 33){ //Before Snapshot 24w09a, that introduced Item Components
                NbtList lore = new NbtList();
                lore.add(0, NbtString.of("{\"text\":\"" + this.getCustomName().getString() + "\"}"));
                NbtCompound display = x.getStack().getOrCreateSubNbt(ItemStack.DISPLAY_KEY);
                display.put(ItemStack.LORE_KEY, lore);
            }
        }
        return x;
    }

    @Shadow abstract Text getDefaultName();
    @Shadow abstract EntityType<?> getType();

    @ModifyVariable(
        method = "getName()Lnet/minecraft/text/Text;",
        at = @At(value = "STORE"), ordinal = 0
        )
    private Text aliventMessenger$giveColourAndSpeciesNameToEntityName(Text name) {
        if(name != null) {
            if(AliventMessengerConfig.speciesName)
                name = Text.translatable("commands.list.nameAndId", name, this.getDefaultName());
            if(SpawnEggItem.forEntity(this.getType()) != null && AliventMessengerConfig.nameColour)
                ((MutableText)name).styled(style -> style.withColor(SpawnEggItem.forEntity(this.getType()).getColor(0)));
        }
        return name;
    }

    @ModifyExpressionValue(
        method = "getName()Lnet/minecraft/text/Text;", 
        at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getDefaultName()Lnet/minecraft/text/Text;")
        )
    private Text aliventMessenger$giveColourToEntityDefaultName(Text name) {
        if(SpawnEggItem.forEntity(this.getType()) != null && AliventMessengerConfig.nameColour)
            ((MutableText)name).setStyle(name.getStyle().withColor(SpawnEggItem.forEntity(this.getType()).getColor(0)));
        return name;
    }
}