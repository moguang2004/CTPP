package com.mo_guang.ctpp.common.toolbox;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;

import com.mo_guang.ctpp.config.MainConfig;

public final class CTPPToolboxSounds {

    private CTPPToolboxSounds() {}

    private static boolean enabled() {
        return MainConfig.INSTANCE == null || MainConfig.INSTANCE.clientConfig.toolboxSounds;
    }

    public static void playOpen(Level level, BlockPos pos) {
        if (!enabled()) return;
        level.playSound(null, pos, net.minecraft.sounds.SoundEvents.IRON_DOOR_OPEN, SoundSource.BLOCKS,
                0.25f, 1.2f + level.random.nextFloat() * 0.1f);
        level.playSound(null, pos, net.minecraft.sounds.SoundEvents.CHEST_OPEN, SoundSource.BLOCKS,
                0.1f, 1.1f + level.random.nextFloat() * 0.1f);
    }

    public static void playClose(Level level, BlockPos pos) {
        if (!enabled()) return;
        level.playSound(null, pos, net.minecraft.sounds.SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS,
                0.1f, 1.1f + level.random.nextFloat() * 0.1f);
        level.playSound(null, pos, net.minecraft.sounds.SoundEvents.IRON_DOOR_CLOSE, SoundSource.BLOCKS,
                0.25f, 1.2f + level.random.nextFloat() * 0.1f);
    }

    public static void playOpenLocally(Level level, BlockPos pos) {
        if (!enabled()) return;
        var center = pos.getCenter();
        level.playLocalSound(center.x, center.y, center.z, net.minecraft.sounds.SoundEvents.IRON_DOOR_OPEN,
                SoundSource.BLOCKS, 0.25f, 1.2f + level.random.nextFloat() * 0.1f, false);
        level.playLocalSound(center.x, center.y, center.z, net.minecraft.sounds.SoundEvents.CHEST_OPEN,
                SoundSource.BLOCKS, 0.1f, 1.1f + level.random.nextFloat() * 0.1f, false);
    }

    public static void playCloseLocally(Level level, BlockPos pos) {
        if (!enabled()) return;
        var center = pos.getCenter();
        level.playLocalSound(center.x, center.y, center.z, net.minecraft.sounds.SoundEvents.CHEST_CLOSE,
                SoundSource.BLOCKS, 0.1f, 1.1f + level.random.nextFloat() * 0.1f, false);
        level.playLocalSound(center.x, center.y, center.z, net.minecraft.sounds.SoundEvents.IRON_DOOR_CLOSE,
                SoundSource.BLOCKS, 0.25f, 1.2f + level.random.nextFloat() * 0.1f, false);
    }
}
