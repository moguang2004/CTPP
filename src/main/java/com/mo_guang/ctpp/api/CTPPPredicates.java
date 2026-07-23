package com.mo_guang.ctpp.api;

import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;

import com.lowdragmc.lowdraglib.utils.BlockInfo;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import com.mo_guang.ctpp.api.pattern.CTPPBlockMaps;
import com.mo_guang.ctpp.common.block.MagnetBlock;

import java.util.ArrayList;
import java.util.List;

public class CTPPPredicates {

    public static TraceabilityPredicate magnetBlock() {
        var map = CTPPBlockMaps.MagnetBlock;
        List<BlockInfo> blockInfos = new ArrayList<>();

        for (var entry : map.entrySet()) {
            var blockSupplier = entry.getValue();
            Block block = blockSupplier.get();
            blockInfos.add(BlockInfo.fromBlockState(block.defaultBlockState()));
        }

        return (new TraceabilityPredicate((state) -> {
            BlockState blockState = state.getBlockState();
            int strength = MagnetBlock.getStrength(blockState);
            if (strength > 0) {
                int currentStrength = state.getMatchContext().getOrPut("MagnetStrength", 0);
                state.getMatchContext().set("MagnetStrength", currentStrength + strength);
                return true;
            }
            return false;
        }, () -> blockInfos.toArray(BlockInfo[]::new)))
                .addTooltips(Component.translatable("ctpp.machine.pattern.error.tier"));
    }
}
