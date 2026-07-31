package com.mo_guang.ctpp.common.item;

import com.gregtechceu.gtceu.api.item.IToolboxItem;
import com.gregtechceu.gtceu.api.item.component.IRecipeRemainder;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.api.item.tool.ToolHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.network.NetworkHooks;

import com.ctnhlang.CN;
import com.ctnhlang.EN;
import com.mo_guang.ctpp.common.block.CTPPToolboxBlock;
import com.mo_guang.ctpp.common.menu.CTPPToolboxMenu;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxSavedData;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxService;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxSounds;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxSourceId;
import com.mo_guang.ctpp.common.toolbox.CTPPToolboxStackData;
import org.jetbrains.annotations.Nullable;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;

import java.util.List;
import java.util.UUID;

public class CTPPToolboxItem extends BlockItem implements IToolboxItem, IRecipeRemainder {

    @CN("工具箱为空")
    @EN("Toolbox is empty")
    private static Lang emptyTooltip;

    public CTPPToolboxItem(Block block, Properties properties) {
        super(block, properties.stacksTo(1));
    }

    public static DyeColor getColor(ItemStack stack) {
        return stack.getItem() instanceof BlockItem blockItem &&
                blockItem.getBlock() instanceof CTPPToolboxBlock block ?
                        block.getColor() : DyeColor.BROWN;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity, int slot,
                              boolean selected) {
        if (level instanceof ServerLevel serverLevel) {
            UUID id = CTPPToolboxStackData.getId(stack);
            CTPPToolboxSavedData.Record record = id == null ? null : CTPPToolboxSavedData.get(serverLevel).find(id);
            if (record == null) record = CTPPToolboxService.ensure(stack, serverLevel);
            else CTPPToolboxStackData.update(stack, record);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            CTPPToolboxSavedData.Record record = CTPPToolboxService.ensure(stack, serverPlayer.serverLevel());
            int slot = hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : 40;
            CTPPToolboxSourceId source = new CTPPToolboxSourceId(
                    CTPPToolboxSourceId.Type.PLAYER_INVENTORY, slot, record.id(), null);
            open(serverPlayer, source);
            CTPPToolboxSounds.playOpen(level, player.blockPosition());
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    public static void open(ServerPlayer player, CTPPToolboxSourceId source) {
        CTPPToolboxService.Resolved resolved = CTPPToolboxService.resolve(player, source);
        if (resolved == null) return;
        NetworkHooks.openScreen(player, new net.minecraft.world.MenuProvider() {

            @Override
            public Component getDisplayName() {
                return resolved.displayName();
            }

            @Override
            public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int id, Inventory inventory,
                                                                                  Player menuPlayer) {
                return CTPPToolboxMenu.create(id, inventory, resolved);
            }
        }, buffer -> {
            resolved.source().write(buffer);
            buffer.writeItem(resolved.displayStack());
        });
    }

    @Override
    public boolean containsTool(ItemStack toolbox, GTToolType toolType) {
        return CTPPToolboxStackData.containsTool(toolbox, toolType.name);
    }

    @Override
    public boolean damageTool(ItemStack toolbox, GTToolType toolType, @Nullable LivingEntity user, int damage) {
        if (!(user != null && user.level() instanceof ServerLevel level)) return false;
        CTPPToolboxSavedData.Record record = find(toolbox, level);
        if (record == null) return false;
        for (int slot = 0; slot < record.inventory().getSlots(); slot++) {
            ItemStack tool = record.inventory().getStackInSlot(slot);
            if (!tool.isEmpty() && ToolHelper.is(tool, toolType)) {
                ToolHelper.damageItem(tool, user, damage);
                record.inventory().setStackInSlot(slot, tool);
                CTPPToolboxStackData.update(toolbox, record);
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack getRecipeRemained(ItemStack stack) {
        ItemStack result = stack.copy();
        String name = result.getOrCreateTag().getString(CTPPToolboxStackData.LAST_USED_TOOL);
        result.removeTagKey(CTPPToolboxStackData.LAST_USED_TOOL);
        GTToolType type = GTToolType.getTypes().get(name);
        Player player = ForgeHooks.getCraftingPlayer();
        if (type == null || player == null || !(player.level() instanceof ServerLevel level)) return result;
        CTPPToolboxSavedData.Record record = find(result, level);
        if (record == null) return result;
        for (int slot = 0; slot < record.inventory().getSlots(); slot++) {
            ItemStack tool = record.inventory().getStackInSlot(slot);
            if (!tool.isEmpty() && ToolHelper.is(tool, type)) {
                ToolHelper.damageItemWhenCrafting(tool, player);
                record.inventory().setStackInSlot(slot, tool);
                break;
            }
        }
        CTPPToolboxStackData.update(result, record);
        return result;
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        return getRecipeRemained(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        var tag = stack.getTag();
        String summary = tag == null ? "" : tag.getString(CTPPToolboxStackData.TOOL_TYPES).trim();
        if (summary.isEmpty()) {
            tooltip.add(emptyTooltip.translate().withStyle(ChatFormatting.GRAY));
            return;
        }
        for (String name : summary.split(" ")) {
            if (!name.isEmpty()) tooltip.add(Component.literal(" * " + name).withStyle(ChatFormatting.AQUA));
        }
    }

    private static @Nullable CTPPToolboxSavedData.Record find(ItemStack stack, ServerLevel level) {
        UUID id = CTPPToolboxStackData.getId(stack);
        return id == null ? null : CTPPToolboxSavedData.get(level).find(id);
    }
}
