package com.mo_guang.ctpp.integration.jade;

import com.gregtechceu.gtceu.api.block.MetaMachineBlock;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import com.ctnhlang.CN;
import com.ctnhlang.EN;
import com.ctnhlang.Key;
import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.common.machine.simple.PlaceableEmitterMachine;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;

/** Jade integration: placed emitters display the vanilla GT emitter item's name and icon. */
@WailaPlugin
public class CTPPJadePlugin implements IWailaPlugin {

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(new PlaceableEmitterProvider(), MetaMachineBlock.class);
    }

    public static class PlaceableEmitterProvider implements IBlockComponentProvider {

        @Key("config.jade.plugin_ctpp.placeable_emitter")
        @CN("[CTPP] 可放置发射器信息")
        @EN("[CTPP] Placeable Emitter Info")
        public static Lang configJadePluginCtppPlaceableEmitter;

        @Override
        public ResourceLocation getUid() {
            return CTPP.id("placeable_emitter");
        }

        @Override
        public IElement getIcon(BlockAccessor accessor, IPluginConfig config, IElement currentIcon) {
            var stack = emitterStack(accessor);
            return stack.isEmpty() ? currentIcon : IElementHelper.get().item(stack);
        }

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            var stack = emitterStack(accessor);
            if (stack.isEmpty()) return;
            tooltip.remove(Identifiers.CORE_OBJECT_NAME);
            tooltip.add(0, stack.getHoverName());
        }

        private static ItemStack emitterStack(BlockAccessor accessor) {
            var machine = accessor.getBlockEntity() instanceof IMachineBlockEntity machineBlockEntity ?
                    machineBlockEntity.getMetaMachine() : null;
            if (!(machine instanceof PlaceableEmitterMachine emitter)) return ItemStack.EMPTY;
            return PlaceableEmitterMachine.emitterItem(emitter.getTier());
        }
    }
}
