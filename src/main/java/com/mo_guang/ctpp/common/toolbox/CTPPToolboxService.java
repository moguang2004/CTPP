package com.mo_guang.ctpp.common.toolbox;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import com.mo_guang.ctpp.common.blockentity.CTPPToolboxBlockEntity;
import com.mo_guang.ctpp.common.item.CTPPToolboxItem;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class CTPPToolboxService {

    private CTPPToolboxService() {}

    public static CTPPToolboxSavedData.Record ensure(ItemStack stack, ServerLevel level) {
        CTPPToolboxSavedData data = CTPPToolboxSavedData.get(level);
        CTPPToolboxSavedData.Record record = data.getOrCreate(CTPPToolboxStackData.getId(stack),
                CTPPToolboxItem.getColor(stack));
        CTPPToolboxStackData.update(stack, record);
        return record;
    }

    public static @Nullable Resolved resolve(ServerPlayer player, CTPPToolboxSourceId source) {
        CTPPToolboxSavedData.Record record = CTPPToolboxSavedData.get(player.serverLevel()).find(source.toolboxId());
        if (record == null) return null;

        if (source.type() == CTPPToolboxSourceId.Type.BLOCK) {
            if (source.blockPos() == null || source.blockPos().distSqr(player.blockPosition()) > 64 * 64) return null;
            if (!(player.level().getBlockEntity(source.blockPos()) instanceof CTPPToolboxBlockEntity block) ||
                    !source.toolboxId().equals(block.getToolboxId()))
                return null;
            return new Resolved(source, record, block.getDisplayStack(), block.getDisplayName(), ItemStack.EMPTY);
        }

        ItemStack hinted = source.type() == CTPPToolboxSourceId.Type.PLAYER_INVENTORY ?
                inventoryStack(player, source.slot()) : curiosStack(player, source.slot());
        if (matches(hinted, source.toolboxId())) {
            CTPPToolboxStackData.update(hinted, record);
            return new Resolved(source, record, hinted.copy(), hinted.getHoverName(), hinted);
        }

        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (!matches(stack, source.toolboxId())) continue;
            CTPPToolboxStackData.update(stack, record);
            CTPPToolboxSourceId relocated = new CTPPToolboxSourceId(
                    CTPPToolboxSourceId.Type.PLAYER_INVENTORY, slot, source.toolboxId(), null);
            return new Resolved(relocated, record, stack.copy(), stack.getHoverName(), stack);
        }

        var curios = CuriosApi.getCuriosInventory(player).resolve().orElse(null);
        if (curios == null) return null;
        var handler = curios.getEquippedCurios();
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack stack = handler.getStackInSlot(slot);
            if (!matches(stack, source.toolboxId())) continue;
            CTPPToolboxStackData.update(stack, record);
            CTPPToolboxSourceId relocated = new CTPPToolboxSourceId(
                    CTPPToolboxSourceId.Type.CURIOS, slot, source.toolboxId(), null);
            return new Resolved(relocated, record, stack.copy(), stack.getHoverName(), stack);
        }
        return null;
    }

    public static List<Resolved> collect(ServerPlayer player) {
        List<Resolved> result = new ArrayList<>();
        java.util.Set<UUID> seen = new java.util.HashSet<>();
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (!(stack.getItem() instanceof CTPPToolboxItem)) continue;
            CTPPToolboxSavedData.Record record = ensure(stack, player.serverLevel());
            if (!seen.add(record.id())) continue;
            result.add(new Resolved(new CTPPToolboxSourceId(CTPPToolboxSourceId.Type.PLAYER_INVENTORY, slot,
                    record.id(), null), record, stack.copy(), stack.getHoverName(), stack));
        }
        CuriosApi.getCuriosInventory(player).resolve().ifPresent(curios -> {
            var handler = curios.getEquippedCurios();
            for (int slot = 0; slot < handler.getSlots(); slot++) {
                ItemStack stack = handler.getStackInSlot(slot);
                if (!(stack.getItem() instanceof CTPPToolboxItem)) continue;
                CTPPToolboxSavedData.Record record = ensure(stack, player.serverLevel());
                if (!seen.add(record.id())) continue;
                result.add(new Resolved(new CTPPToolboxSourceId(CTPPToolboxSourceId.Type.CURIOS, slot,
                        record.id(), null), record, stack.copy(), stack.getHoverName(), stack));
            }
        });
        for (CTPPToolboxBlockEntity block : CTPPToolboxBlockRegistry.nearby(player)) {
            UUID id = block.getToolboxId();
            CTPPToolboxSavedData.Record record = id == null ? null :
                    CTPPToolboxSavedData.get(player.serverLevel()).find(id);
            if (record == null || !seen.add(id)) continue;
            result.add(new Resolved(new CTPPToolboxSourceId(CTPPToolboxSourceId.Type.BLOCK, -1, id,
                    block.getBlockPos()), record, block.getDisplayStack(), block.getDisplayName(), ItemStack.EMPTY));
        }
        return result;
    }

    private static ItemStack inventoryStack(Player player, int slot) {
        return slot >= 0 && slot < player.getInventory().getContainerSize() ?
                player.getInventory().getItem(slot) : ItemStack.EMPTY;
    }

    private static ItemStack curiosStack(Player player, int slot) {
        var curios = CuriosApi.getCuriosInventory(player).resolve().orElse(null);
        if (curios == null || slot < 0 || slot >= curios.getEquippedCurios().getSlots()) return ItemStack.EMPTY;
        return curios.getEquippedCurios().getStackInSlot(slot);
    }

    private static boolean matches(ItemStack stack, UUID id) {
        return stack.getItem() instanceof CTPPToolboxItem && id.equals(CTPPToolboxStackData.getId(stack));
    }

    public record Resolved(CTPPToolboxSourceId source, CTPPToolboxSavedData.Record record,
                           ItemStack displayStack, Component displayName, ItemStack hostStack) {

        public CTPPToolboxInventory inventory() {
            return record.inventory();
        }

        public void syncProjection() {
            if (!hostStack.isEmpty()) CTPPToolboxStackData.update(hostStack, record);
        }
    }
}
