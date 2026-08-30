package com.mo_guang.ctpp.integration.emi;

import com.mo_guang.ctpp.registry.CTPPMachines;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;

/**
 * Hides the placeable-emitter machine items from EMI: they are unobtainable intermediates
 * (in-world placement and drops both go through the vanilla GT emitter items).
 */
@EmiEntrypoint
public class CTPPEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        for (var definition : CTPPMachines.PLACEABLE_EMITTER) {
            if (definition != null) registry.removeEmiStacks(EmiStack.of(definition.asStack()));
        }
    }
}
