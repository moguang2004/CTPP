package com.mo_guang.ctpp.mixin;

import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.fluids.store.FluidStorageKeys;
import com.gregtechceu.gtceu.api.item.GTBucketItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

@Mixin(GTBucketItem.class)
public class GTBucketItemMixin extends BucketItem {
    @Final
    @Shadow(remap = false)
    Material material;
    public GTBucketItemMixin(Supplier<? extends Fluid> supplier, Properties builder) {
        super(supplier, builder);
    }
//
//    @Inject(method = "emptyContents", at = @At(value = "RETURN", ordinal = 5), remap = false)
//    public void emptyContents(Player pPlayer, Level pLevel, BlockPos pPos, BlockHitResult pResult, ItemStack container, CallbackInfoReturnable<Boolean> cir) {
//        BlockState blockstate = pLevel.getBlockState(pPos);
//        boolean flag = blockstate.canBeReplaced(this.getFluid());
//        if (!pLevel.isClientSide && flag && !blockstate.liquid()) {
//            pLevel.destroyBlock(pPos, true);
//        }
//
//        if (!pLevel.setBlock(pPos, material.getFluid().defaultFluidState().createLegacyBlock(), 11) && !blockstate.getFluidState().isSource()) {
//            cir.setReturnValue(false);
//        } else {
//            this.playEmptySound(pPlayer, pLevel, pPos);
//            cir.setReturnValue(true);
//        }
//    }
/**
 * @author moguang
 * @reason rewrite
 */
@Overwrite(remap = false)
    public boolean emptyContents(@Nullable Player pPlayer, Level pLevel, BlockPos pPos, @Nullable BlockHitResult pResult, @Nullable ItemStack container) {
    if (!(this.getFluid() instanceof FlowingFluid)) {
        return false;
    } else {
        BlockState blockstate = pLevel.getBlockState(pPos);
        Block block = blockstate.getBlock();
        boolean flag = blockstate.canBeReplaced(this.getFluid());
        boolean flag1 = blockstate.isAir() || flag || block instanceof LiquidBlockContainer && ((LiquidBlockContainer)block).canPlaceLiquid(pLevel, pPos, blockstate, this.getFluid());
        Optional<FluidStack> containedFluidStack = Optional.ofNullable(container).flatMap(FluidUtil::getFluidContained);
        if (!flag1) {
            return pResult != null && this.emptyContents(pPlayer, pLevel, pResult.getBlockPos().relative(pResult.getDirection()), (BlockHitResult)null, container);
        } else if (containedFluidStack.isPresent() && this.getFluid().getFluidType().isVaporizedOnPlacement(pLevel, pPos, (FluidStack)containedFluidStack.get())) {
            this.getFluid().getFluidType().onVaporize(pPlayer, pLevel, pPos, (FluidStack)containedFluidStack.get());
            return true;
        } else if (pLevel.dimensionType().ultraWarm() && this.getFluid().is(FluidTags.WATER)) {
            int i = pPos.getX();
            int j = pPos.getY();
            int k = pPos.getZ();
            pLevel.playSound(pPlayer, pPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (pLevel.random.nextFloat() - pLevel.random.nextFloat()) * 0.8F);

            for(int l = 0; l < 8; ++l) {
                pLevel.addParticle(ParticleTypes.LARGE_SMOKE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0, 0.0, 0.0);
            }

            return true;
        } else if (block instanceof LiquidBlockContainer && ((LiquidBlockContainer)block).canPlaceLiquid(pLevel, pPos, blockstate, this.getFluid())) {
            ((LiquidBlockContainer)block).placeLiquid(pLevel, pPos, blockstate, ((FlowingFluid)this.getFluid()).getSource(false));
            this.playEmptySound(pPlayer, pLevel, pPos);
            return true;
        } else {
            if (!pLevel.isClientSide && flag && !blockstate.liquid()) {
                pLevel.destroyBlock(pPos, true);
            }

            if (!pLevel.setBlock(pPos, material.getFluid().defaultFluidState().createLegacyBlock(), 11) && !blockstate.getFluidState().isSource()) {
                return false;
            } else {
                this.playEmptySound(pPlayer, pLevel, pPos);
                return true;
            }
        }
    }
    }

}
