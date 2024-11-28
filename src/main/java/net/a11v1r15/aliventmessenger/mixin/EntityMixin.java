package net.a11v1r15.aliventmessenger.mixin;

import net.a11v1r15.aliventmessenger.AliventColorUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.a11v1r15.aliventmessenger.AliventMessengerConfig;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Nameable;
import net.minecraft.world.entity.EntityLike;

@Mixin(Entity.class)
public abstract class EntityMixin
implements Nameable,
EntityLike,
CommandOutput {
    @ModifyVariable(
        method = "Lnet/minecraft/entity/Entity;dropStack(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/item/ItemStack;F)Lnet/minecraft/entity/ItemEntity;",
        at = @At(value = "STORE"), ordinal = 0
        )
    private ItemEntity aliventMessenger$giveLoreNameToDroppings(ItemEntity x) {
        if(AliventMessengerConfig.loreDrops && this.hasCustomName()){
            LoreComponent lore = LoreComponent.DEFAULT;
            lore = lore.with(Text.literal(this.getCustomName().getString()));
            x.getStack().set(DataComponentTypes.LORE, lore);
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
            int[] colours = AliventColorUtil.VANILLA.get(this.getType());
            if(colours != null && AliventMessengerConfig.nameColour)
                ((MutableText)name).styled(style -> style.withColor(colours[0]).withShadowColor(colours[1]+0x3F000000));
        }
        return name;
    }

    @ModifyExpressionValue(
        method = "getName()Lnet/minecraft/text/Text;", 
        at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getDefaultName()Lnet/minecraft/text/Text;")
        )
    private Text aliventMessenger$giveColourToEntityDefaultName(Text name) {
        int[] colours = AliventColorUtil.VANILLA.get(this.getType());
        if(colours != null && AliventMessengerConfig.nameColour)
            ((MutableText)name).setStyle(name.getStyle().withColor(colours[0]).withShadowColor(colours[1]+0x3F000000));
        return name;
    }
}