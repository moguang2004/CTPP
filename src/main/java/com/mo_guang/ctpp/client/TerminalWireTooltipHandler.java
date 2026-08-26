package com.mo_guang.ctpp.client;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.WireProperties;
import com.gregtechceu.gtceu.utils.FormattingUtil;
import com.gregtechceu.gtceu.utils.GTUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.api.terminal.TerminalProperties;

@Mod.EventBusSubscriber(modid = CTPP.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class TerminalWireTooltipHandler {

    private TerminalWireTooltipHandler() {}

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        WireProperties wire = TerminalProperties.wireProperties(event.getItemStack());
        if (wire == null) return;

        int tier = GTUtil.getTierByVoltage(wire.getVoltage());
        event.getToolTip().add(Component.translatable("gtceu.cable.voltage",
                FormattingUtil.formatNumbers(wire.getVoltage()), GTValues.VNF[tier]));
        event.getToolTip().add(Component.translatable("gtceu.cable.amperage",
                FormattingUtil.formatNumbers(wire.getAmperage())));
        event.getToolTip().add(Component.translatable("gtceu.cable.loss_per_block",
                FormattingUtil.formatNumbers(wire.getLossPerBlock())));
    }
}
