package com.mo_guang.ctpp.client;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.BlockItem;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.ctnhlang.CN;
import com.ctnhlang.EN;
import com.mo_guang.ctpp.common.block.MagnetBlock;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;

public class MagnetTooltipHandler {

    @CN("磁感应强度：%d")
    @EN("Magnetic induction strength: %d")
    static Lang magnetTooltip;

    @SubscribeEvent
    public void onItemTooltip(ItemTooltipEvent event) {
        if (!(event.getItemStack().getItem() instanceof BlockItem blockItem)) return;

        int strength = MagnetBlock.getStrength(blockItem.getBlock().defaultBlockState());
        if (strength > 0) {
            event.getToolTip().add(magnetTooltip.translate(strength)
                    .withStyle(ChatFormatting.AQUA));
        }
    }
}
