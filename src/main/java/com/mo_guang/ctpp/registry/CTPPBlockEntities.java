package com.mo_guang.ctpp.registry;

import com.mo_guang.ctpp.common.block.VoltageTerminalBlock;
import com.mo_guang.ctpp.common.blockentity.CTPPToolboxBlockEntity;
import com.mo_guang.ctpp.common.blockentity.GeneratorCoilBlockEntity;
import com.mo_guang.ctpp.common.blockentity.VoltageTerminalBlockEntity;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;

import static com.mo_guang.ctpp.CTPPRegistration.REGISTRATE;

public class CTPPBlockEntities {

    public static final BlockEntityEntry<GeneratorCoilBlockEntity> GENERATOR_COIL = REGISTRATE
            .blockEntity("generator_coil", GeneratorCoilBlockEntity::new)
            .validBlocks(() -> CTPPBlocks.GENERATOR_COIL.get())
            .register();

    public static final BlockEntityEntry<CTPPToolboxBlockEntity> TOOLBOX = REGISTRATE
            .blockEntity("toolbox", CTPPToolboxBlockEntity::new)
            .validBlocks(toolboxBlocks())
            .register();

    public static final BlockEntityEntry<VoltageTerminalBlockEntity> VOLTAGE_TERMINAL = REGISTRATE
            .blockEntity("voltage_terminal", VoltageTerminalBlockEntity::new)
            .validBlocks(terminalBlocks())
            .register();

    @SuppressWarnings("unchecked")
    private static BlockEntry<VoltageTerminalBlock>[] terminalBlocks() {
        return java.util.Arrays.stream(CTPPBlocks.VOLTAGE_COILS)
                .filter(java.util.Objects::nonNull)
                .toArray(BlockEntry[]::new);
    }

    @SuppressWarnings("unchecked")
    private static NonNullSupplier<? extends net.minecraft.world.level.block.Block>[] toolboxBlocks() {
        NonNullSupplier<? extends net.minecraft.world.level.block.Block>[] blocks = new NonNullSupplier[CTPPBlocks.TOOLBOXES.length];
        for (int i = 0; i < blocks.length; i++) {
            int index = i;
            blocks[i] = () -> CTPPBlocks.TOOLBOXES[index].get();
        }
        return blocks;
    }

    public static void init() {}
}
