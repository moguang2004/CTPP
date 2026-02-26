package com.mo_guang.ctpp.mixin.create.fix;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.content.logistics.BigItemStack;
import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import com.simibubi.create.content.logistics.stockTicker.StockTickerBlockEntity;
import com.simibubi.create.content.logistics.tunnel.BrassTunnelBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.schematics.cannon.MaterialChecklist;
import com.simibubi.create.foundation.utility.CreateLang;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = DeployerApplicationRecipe.class, remap = false)
public class DeployerApplicationRecipeMixin {
    @Redirect(method = "getDescriptionForAssembly",
            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/utility/CreateLang;translateDirect(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;"))
    MutableComponent fixGTName(String key, Object[] args, @Local(name = "matchingStacks") ItemStack[] matchingStacks){
        return CreateLang.translateDirect(key, matchingStacks[0].getDisplayName());
    }

    @Mixin(value = DeployerBlockEntity.class, remap = false)
    static class DeployerBlockEntityMixin{
        @Shadow
        protected ItemStack heldItem;

        @Redirect(method = "addToGoggleTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/MutableComponent;getString()Ljava/lang/String;"))
        String fixGTName(MutableComponent instance){
            return heldItem.getDisplayName().getString();
        }
    }

    @Mixin(value = ChuteBlockEntity.class, remap = false)
    static class ChuteBlockEntityMixin{
        @Shadow
        ItemStack item;

        @Redirect(method = "addToGoggleTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/MutableComponent;getString()Ljava/lang/String;"))
        String fixGTName(MutableComponent instance){
            return item.getDisplayName().getString();
        }
    }

    @Mixin(value = StockTickerBlockEntity.class, remap = false)
    static class StockTickerBlockEntityMixin{
        @Redirect(method = "addToTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/MutableComponent;getString()Ljava/lang/String;"))
        String fixGTName(MutableComponent instance, @Local(name = "entry") BigItemStack entry){
            return entry.stack.getDisplayName().getString();
        }
    }

    @Mixin(value = BrassTunnelBlockEntity.class, remap = false)
    static class BrassTunnelBlockEntityMixin{
        @Redirect(method = "addToGoggleTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/MutableComponent;getString()Ljava/lang/String;"))
        String fixGTName(MutableComponent instance, @Local(name = "item") ItemStack item){
            return item.getDisplayName().getString();
        }
    }

    @Mixin(value = BasinBlockEntity.class, remap = false)
    static class BasinBlockEntityMixin{
        @Redirect(method = "addToGoggleTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"))
        MutableComponent fixGTName(String key, @Local(name = "stackInSlot") ItemStack stackInSlot){
            return stackInSlot.getDisplayName().copy();
        }
    }

    @Mixin(value = MaterialChecklist.class, remap = false)
    static class MaterialChecklistMixin{
        @Redirect(method = "entry", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;"))
        MutableComponent fixGTName(String key, ItemStack item){
            return item.getDisplayName().copy();
        }
    }
}
