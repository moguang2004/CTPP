package com.mo_guang.ctpp.event;

import com.gregtechceu.gtceu.common.data.GTItems;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.registry.CTPPMachines;

/** Lets vanilla GT emitter items be placed in-world as placeable emitter machines. */
@Mod.EventBusSubscriber(modid = CTPP.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlaceableEmitterEventHandler {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack stack = event.getItemStack();
        int tier = tierFor(stack.getItem());
        if (tier < 0 || event.getFace() == null) return;
        var level = event.getLevel();
        var definition = CTPPMachines.PLACEABLE_EMITTER[tier];
        if (level.isClientSide()) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            return;
        }
        // delegate to the machine item's own placement logic so the machine is initialized correctly
        var machineStack = definition.asStack();
        var result = machineStack.getItem().useOn(
                new UseOnContext(level, event.getEntity(), event.getHand(), machineStack, event.getHitVec()));
        if (!result.consumesAction()) return;
        if (event.getEntity() == null || !event.getEntity().getAbilities().instabuild) stack.shrink(1);
        event.setCanceled(true);
        event.setCancellationResult(result);
    }

    private static int tierFor(Item item) {
        if (item == GTItems.EMITTER_LV.asItem()) return 1;
        if (item == GTItems.EMITTER_MV.asItem()) return 2;
        if (item == GTItems.EMITTER_HV.asItem()) return 3;
        if (item == GTItems.EMITTER_EV.asItem()) return 4;
        if (item == GTItems.EMITTER_IV.asItem()) return 5;
        if (item == GTItems.EMITTER_LuV.asItem()) return 6;
        if (item == GTItems.EMITTER_ZPM.asItem()) return 7;
        if (item == GTItems.EMITTER_UV.asItem()) return 8;
        return -1;
    }
}
