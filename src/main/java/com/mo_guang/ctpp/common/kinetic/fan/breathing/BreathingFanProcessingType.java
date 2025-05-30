package com.mo_guang.ctpp.common.kinetic.fan.breathing;

import com.mo_guang.ctpp.common.data.CTPPRecipeTypeInfo;
import com.mo_guang.ctpp.data.tags.CustomTags;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.content.processing.burner.LitBlazeBurnerBlock;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import com.simibubi.create.foundation.utility.Color;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class BreathingFanProcessingType implements FanProcessingType {
    public static BreathingRecipe.BreathingWrapper RECIPE_WRAPPER = new BreathingRecipe.BreathingWrapper();
    @Override
    public boolean isValidAt(Level level, BlockPos blockPos) {
        BlockState blockState = level.getBlockState(blockPos);
        return blockState.is(CustomTags.CATALYST_BREATHING);
    }

    @Override
    public int getPriority() {
        return 100;
    }

    @Override
    public boolean canProcess(ItemStack itemStack, Level level) {
        RECIPE_WRAPPER.setItem(0, itemStack);
        Optional<BreathingRecipe> breathingRecipe = CTPPRecipeTypeInfo.BREATHING.find(RECIPE_WRAPPER, level);
        return breathingRecipe.isPresent();
    }

    @Override
    public @Nullable List<ItemStack> process(ItemStack itemStack, Level level) {
        RECIPE_WRAPPER.setItem(0, itemStack);
        Optional<BreathingRecipe> breathingRecipe = CTPPRecipeTypeInfo.BREATHING.find(RECIPE_WRAPPER, level);
        if(breathingRecipe.isPresent()) {
            return RecipeApplier.applyRecipeOn(level, itemStack, breathingRecipe.get());
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
        level.addParticle(ParticleTypes.DRAGON_BREATH, pos.x, pos.y + .45f, pos.z, 0, 0, 0);
    }

    @Override
    public void morphAirFlow(AirFlowParticleAccess particleAccess, RandomSource randomSource) {
        particleAccess.setColor(Color.mixColors(0x0, 0xa73cbd, randomSource.nextFloat()));
        particleAccess.setAlpha(1f);
        if (randomSource.nextFloat() < 1 / 128f)
            particleAccess.spawnExtraParticle(ParticleTypes.DRAGON_BREATH, .125f);
        if (randomSource.nextFloat() < 1 / 32f)
            particleAccess.spawnExtraParticle(ParticleTypes.SMOKE, .125f);
    }

    @Override
    public void affectEntity(Entity entity, Level level) {
        if (level.isClientSide) {
            if (entity instanceof Silverfish) {
                Vec3 p = entity.getPosition(0);
                Vec3 v = p.add(0, 0.5f, 0)
                        .add(VecHelper.offsetRandomly(Vec3.ZERO, level.random, 1)
                                .multiply(1, 0.2f, 1)
                                .normalize()
                                .scale(1f));
                level.addParticle(ParticleTypes.DRAGON_BREATH, v.x, v.y, v.z, 0, 0.1f, 0);
                if (level.random.nextInt(3) == 0)
                    level.addParticle(ParticleTypes.LARGE_SMOKE, p.x, p.y + .5f, p.z,
                            (level.random.nextFloat() - .5f) * .5f, 0.1f, (level.random.nextFloat() - .5f) * .5f);
            }
            return;
        }

        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 30, 0, false, false));
            livingEntity.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 100, 1, false, false));
        }
        if (entity instanceof Silverfish silverfish) {
            int progress = silverfish.getPersistentData()
                    .getInt("CTPPBreathing");
            if (progress < 100) {
                if (progress % 10 == 0) {
                    level.playSound(null, entity.blockPosition(), SoundEvents.SOUL_ESCAPE, SoundSource.NEUTRAL,
                            1f, 1.5f * progress / 100f);
                }
                silverfish.getPersistentData()
                        .putInt("CTPPBreathing", progress + 1);
                return;
            }

            level.playSound(null, entity.blockPosition(), SoundEvents.GENERIC_EXTINGUISH_FIRE,
                    SoundSource.NEUTRAL, 1.25f, 0.65f);

            Endermite endermite = EntityType.ENDERMITE.create(level);
            CompoundTag serializeNBT = silverfish.saveWithoutId(new CompoundTag());
            serializeNBT.remove("UUID");

            endermite.deserializeNBT(serializeNBT);
            endermite.setPos(silverfish.getPosition(0));
            level.addFreshEntity(endermite);
            silverfish.discard();
        }
    }
}
