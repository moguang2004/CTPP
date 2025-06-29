package com.mo_guang.ctpp.common.kinetic.fan.acidwashing;

import com.mo_guang.ctpp.common.data.CTPPRecipeTypeInfo;
import com.mo_guang.ctpp.data.tags.CustomTags;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.theme.Color;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class AcidWashingProcessingType implements FanProcessingType {
    public static AcidwashingRecipe.AcidwashingWrapper RECIPE_WRAPPER = new AcidwashingRecipe.AcidwashingWrapper();
    @Override
    public boolean isValidAt(Level level, BlockPos blockPos) {
        FluidState fluidState = level.getFluidState(blockPos);
        return fluidState.is(CustomTags.CATALYST_ACID_WASHING);
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public boolean canProcess(ItemStack itemStack, Level level) {
        RECIPE_WRAPPER.setItem(0, itemStack);
        Optional<AcidwashingRecipe> acidwashingRecipe = CTPPRecipeTypeInfo.ACIDWASHING.find(RECIPE_WRAPPER, level);
        return acidwashingRecipe.isPresent();
    }

    @Override
    public @Nullable List<ItemStack> process(ItemStack itemStack, Level level) {
        RECIPE_WRAPPER.setItem(0, itemStack);
        Optional<AcidwashingRecipe> acidwashingRecipe = CTPPRecipeTypeInfo.ACIDWASHING.find(RECIPE_WRAPPER, level);
        if (acidwashingRecipe.isPresent()) {
            return RecipeApplier.applyRecipeOn(level, itemStack, acidwashingRecipe.get());
        }
        return null;
    }

    @Override
    public void spawnProcessingParticles(Level level, Vec3 pos) {
        if (level.random.nextInt(8) != 0)
            return;
        pos = pos.add(VecHelper.offsetRandomly(Vec3.ZERO, level.random, 1)
                .multiply(1, 0.05f, 1)
                .normalize()
                .scale(0.15f));
        level.addParticle(ParticleTypes.BUBBLE, pos.x, pos.y + .45f, pos.z, 0, 0, 0);
    }

    @Override
    public void morphAirFlow(AirFlowParticleAccess particleAccess, RandomSource randomSource) {
        particleAccess.setColor(Color.mixColors(0x0, 0x4ffc55, randomSource.nextFloat()));
        particleAccess.setAlpha(1f);
        if (randomSource.nextFloat() < 1 / 128f)
            particleAccess.spawnExtraParticle(ParticleTypes.BUBBLE, .125f);
        if (randomSource.nextFloat() < 1 / 32f)
            particleAccess.spawnExtraParticle(ParticleTypes.SMOKE, .125f);
    }

    @Override
    public void affectEntity(Entity entity, Level level) {
        if (level.isClientSide) {
            if (entity instanceof Slime slime && slime.getSize() != 4) {
                Vec3 p = entity.getPosition(0);
                Vec3 v = p.add(0, 0.5f, 0)
                        .add(VecHelper.offsetRandomly(Vec3.ZERO, level.random, 1)
                                .multiply(1, 0.2f, 1)
                                .normalize()
                                .scale(1f));
                level.addParticle(ParticleTypes.BUBBLE, v.x, v.y, v.z, 0, 0.1f, 0);
                if (level.random.nextInt(3) == 0)
                    level.addParticle(ParticleTypes.LARGE_SMOKE, p.x, p.y + .5f, p.z,
                            (level.random.nextFloat() - .5f) * .5f, 0.1f, (level.random.nextFloat() - .5f) * .5f);
            }
            return;
        }
        if (entity instanceof LivingEntity livingEntity && !(entity instanceof Slime)) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 30, 1, false, false));
            livingEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 1, false, false));
            livingEntity.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 2, false, false));
        }
        if (entity instanceof Slime slime && slime.getSize() != 4) {
            int progress = slime.getPersistentData()
                    .getInt("CTPPAcidwashing");
            if (progress < 100) {
                if (progress % 10 == 0) {
                    level.playSound(null, entity.blockPosition(), SoundEvents.SOUL_ESCAPE, SoundSource.NEUTRAL,
                            1f, 1.5f * progress / 100f);
                }
                slime.getPersistentData()
                        .putInt("CTPPAcidwashing", progress + 1);
                return;
            }

            level.playSound(null, entity.blockPosition(), SoundEvents.GENERIC_EXTINGUISH_FIRE,
                    SoundSource.NEUTRAL, 1.25f, 0.65f);

            Slime newslime = EntityType.SLIME.create(level);
            CompoundTag serializeNBT = slime.saveWithoutId(new CompoundTag());
            serializeNBT.remove("UUID");
            serializeNBT.putInt("CTPPAcidwashing", 0);

            newslime.deserializeNBT(serializeNBT);
            if (slime.getSize() != 4) {
                newslime.setSize(slime.getSize() * 2, true);
            }
            newslime.copyPosition(slime);
            level.addFreshEntity(newslime);
            slime.discard();
        }
    }
}
