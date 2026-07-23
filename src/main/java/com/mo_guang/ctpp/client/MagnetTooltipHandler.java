package com.mo_guang.ctpp.client;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.mo_guang.ctpp.common.block.MagnetBlock;

public class MagnetTooltipHandler {

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        if (!(event.getItemStack().getItem() instanceof BlockItem blockItem)) return;

        int strength = MagnetBlock.getStrength(blockItem.getBlock().defaultBlockState());
        if (strength > 0) {
            event.getToolTip().add(Component.translatable("ctpp.magnet.tooltip", strength)
                    .withStyle(ChatFormatting.AQUA));
        }
    }
}
