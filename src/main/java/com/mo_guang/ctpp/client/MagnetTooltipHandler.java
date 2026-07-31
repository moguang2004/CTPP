package com.mo_guang.ctpp.client;

import net.minecraft.ChatFormatting;
import net.minecraft.world.item.BlockItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.ctnhlang.CN;
import com.ctnhlang.EN;
import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.common.block.MagnetBlock;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;

@Mod.EventBusSubscriber(modid = CTPP.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class MagnetTooltipHandler {

    @CN("磁感应强度：%d")
    @EN("Magnetic induction strength: %d")
    static Lang magnetTooltip;

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (!(event.getItemStack().getItem() instanceof BlockItem blockItem)) return;

        int strength = MagnetBlock.getStrength(blockItem.getBlock().defaultBlockState());
        if (strength > 0) {
            event.getToolTip().add(magnetTooltip.translate(strength)
                    .withStyle(ChatFormatting.AQUA));
        }
    }
}
