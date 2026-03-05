package com.mo_guang.ctpp.integration.emi;

import net.minecraft.world.item.Item;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiInitRegistry;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.stack.EmiStack;
import org.antarcticgardens.newage.NewAgeBlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@EmiEntrypoint
public class CTPPEmiPlugin implements EmiPlugin {

    public static List<Supplier<? extends Item>> disabled = new ArrayList<>();

    @Override
    public void initialize(EmiInitRegistry registry) {
        disabled.addAll(List.of(
                NewAgeBlocks.CARBON_BRUSHES::asItem));

        for (var item : disabled) {
            registry.disableStack(EmiStack.of(item.get()));
        }
    }

    @Override
    public void register(EmiRegistry registry) {}
}
