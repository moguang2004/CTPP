package com.mo_guang.ctpp.registry;

import com.mo_guang.ctpp.common.blockentity.GeneratorCoilBlockEntity;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

import static com.mo_guang.ctpp.CTPPRegistration.REGISTRATE;

public class CTPPBlockEntities {

    public static final BlockEntityEntry<GeneratorCoilBlockEntity> GENERATOR_COIL = REGISTRATE
            .blockEntity("generator_coil", GeneratorCoilBlockEntity::new)
            .validBlocks(() -> CTPPBlocks.GENERATOR_COIL.get())
            .register();

    public static void init() {}
}
