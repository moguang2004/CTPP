package com.mo_guang.ctpp.common.item.debug;

import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.component.IInteractionItem;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;

import com.ctnhlang.CN;
import com.ctnhlang.EN;
import com.ctnhlang.Prefix;
import com.mo_guang.ctpp.dynamicPart.rotation.IContraptionMultiblock;
import com.mo_guang.ctpp.dynamicPart.rotation.SimpleRotatingContraptionEntity;
import org.jetbrains.annotations.Nullable;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Prefix("debug_tool")
public class ContraptionDebugToolItem extends ComponentItem implements IInteractionItem {

    @CN("仅供调试：建议在创造模式或 OP 环境下使用")
    @EN("Debug only: creative mode or OP recommended.")
    static Lang creative_only;

    @CN("点击的方块不是装置多方块控制器")
    @EN("The clicked block is not a contraption multiblock controller.")
    static Lang not_controller;

    @CN("多方块必须先成型，才能组装装置实体")
    @EN("The multiblock must be formed before assembling contraptions.")
    static Lang not_formed;

    @CN("这个控制器没有可用于调试组装的支点")
    @EN("This controller does not expose a contraption assembly pivot.")
    static Lang no_pivot;

    @CN("右键装置控制器或部件，将追踪到的实体位置输出到聊天栏")
    @EN("Right-click a contraption controller or part to print tracked entity positions.")
    static Lang locator_tooltip;

    @CN("右键已成型的装置控制器，补组装缺失的装置实体")
    @EN("Right-click a formed contraption controller to assemble missing contraption entities.")
    static Lang assembler_tooltip;

    @CN("右键装置控制器，解组装当前追踪到的装置实体")
    @EN("Right-click a contraption controller to disassemble tracked contraption entities.")
    static Lang disassembler_tooltip;

    @CN("控制器：%s，已成型：%s，支点：%s，追踪实体数：%s")
    @EN("Controller: %s, formed: %s, pivot: %s, tracked entities: %s")
    static Lang locator_summary;

    @CN("没有找到被追踪的装置实体")
    @EN("No tracked contraption entities were found.")
    static Lang locator_no_entities;

    @CN("#%s 位置：%s，支点：%s，运行中：%s")
    @EN("#%s Position: %s, Pivot: %s, Running: %s")
    static Lang locator_entity;

    @CN("这个控制器已经在追踪 %s 个装置实体")
    @EN("This controller already tracks %s contraption entities.")
    static Lang assembler_already_present;

    @CN("根据当前结构组装装置实体失败")
    @EN("Failed to assemble contraption entities from the current structure.")
    static Lang assembler_failed;

    @CN("已在支点 %s 组装 %s 个装置实体")
    @EN("Assembled %2$s contraption entities at pivot %1$s.")
    static Lang assembler_success;

    @CN("这个控制器当前没有追踪任何装置实体")
    @EN("This controller is not tracking any contraption entities.")
    static Lang disassembler_no_entities;

    @CN("已解组装 %s 个装置实体")
    @EN("Disassembled %s contraption entities.")
    static Lang disassembler_success;

    private final Mode mode;

    public ContraptionDebugToolItem(Properties properties, Mode mode) {
        super(properties.stacksTo(1));
        this.mode = mode;
    }

    @CN("使用调试工具需要创造模式或者OP权限")
    @EN("These debug tools require creative mode or OP permissions.")
    static Lang permission;

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        Level level = context.getLevel();
        if (level.isClientSide) return InteractionResult.PASS;

        if (!canUseDebugTool(player)) {
            player.sendSystemMessage(permission.translate()
                    .withStyle(ChatFormatting.RED));
            return InteractionResult.SUCCESS;
        }

        IContraptionMultiblock<?> controller = resolveController(level, context.getClickedPos());
        if (controller == null) {
            player.sendSystemMessage(not_controller.translate()
                    .withStyle(ChatFormatting.RED));
            return InteractionResult.SUCCESS;
        }

        return switch (mode) {
            case LOCATE -> locate(player, controller);
            case ASSEMBLE -> assemble(player, controller);
            case DISASSEMBLE -> disassemble(player, controller);
        };
    }

    @Override
    public boolean sneakBypassUse(ItemStack stack, LevelReader level, BlockPos pos, Player player) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents,
                                TooltipFlag isAdvanced) {
        tooltipComponents.add(mode.tooltip().translate().withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(creative_only.translate()
                .withStyle(ChatFormatting.DARK_RED));
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }

    private static boolean canUseDebugTool(Player player) {
        return player.isCreative() || player.hasPermissions(2);
    }

    @Nullable
    private static IContraptionMultiblock<?> resolveController(Level level, BlockPos pos) {
        MetaMachine machine = MetaMachine.getMachine(level, pos);
        if (machine instanceof IContraptionMultiblock<?> controller) {
            return controller;
        }
        if (machine instanceof IMultiPart multiPart) {
            for (var candidate : multiPart.getControllers()) {
                if (candidate instanceof IContraptionMultiblock<?> contraptionController) {
                    return contraptionController;
                }
            }
        }
        return null;
    }

    private static <T extends SimpleRotatingContraptionEntity> InteractionResult locate(Player player,
                                                                                        IContraptionMultiblock<T> controller) {
        controller.findAndReattachEntities();
        sanitizeTrackedEntities(controller);
        BlockPos controllerPos = controller.getBlockPosition();
        BlockPos pivot = controller.getAssemblyPivot();
        List<T> entities = controller.getContraptionEntity();
        int entityCount = entities == null ? 0 : entities.size();

        player.sendSystemMessage(locator_summary.translate(
                formatBlockPos(controllerPos),
                controller.isFormed(),
                pivot == null ? "-" : formatBlockPos(pivot),
                entityCount).withStyle(ChatFormatting.AQUA));

        if (entityCount == 0) {
            player.sendSystemMessage(locator_no_entities.translate()
                    .withStyle(ChatFormatting.YELLOW));
            return InteractionResult.SUCCESS;
        }

        for (int i = 0; i < entities.size(); i++) {
            T entity = entities.get(i);
            player.sendSystemMessage(locator_entity.translate(
                    i + 1,
                    formatVec3(entity.position()),
                    formatVec3(entity.getPivot()),
                    entity.isRunning()).withStyle(ChatFormatting.GRAY));
        }
        return InteractionResult.SUCCESS;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static <T extends SimpleRotatingContraptionEntity> InteractionResult assemble(Player player,
                                                                                          IContraptionMultiblock<T> controller) {
        if (!controller.isFormed()) {
            player.sendSystemMessage(not_formed.translate()
                    .withStyle(ChatFormatting.RED));
            return InteractionResult.SUCCESS;
        }

        controller.findAndReattachEntities();
        sanitizeTrackedEntities(controller);
        if (controller.getContraptionEntity() != null && !controller.getContraptionEntity().isEmpty()) {
            player.sendSystemMessage(assembler_already_present.translate(
                    controller.getContraptionEntity().size()).withStyle(ChatFormatting.YELLOW));
            return InteractionResult.SUCCESS;
        }

        BlockPos pivot = controller.getAssemblyPivot();
        if (pivot == null) {
            player.sendSystemMessage(no_pivot.translate()
                    .withStyle(ChatFormatting.RED));
            return InteractionResult.SUCCESS;
        }

        var assembled = controller.assemble(pivot);
        if (assembled == null || assembled.isEmpty()) {
            player.sendSystemMessage(assembler_failed.translate()
                    .withStyle(ChatFormatting.RED));
            return InteractionResult.SUCCESS;
        }

        ((IContraptionMultiblock) controller).setContraptionEntity(new ArrayList<>(assembled.values()));
        controller.onDebugAssembled();
        controller.self().notifyBlockUpdate();
        player.sendSystemMessage(assembler_success.translate(
                formatBlockPos(pivot),
                assembled.size()).withStyle(ChatFormatting.GREEN));
        return InteractionResult.SUCCESS;
    }

    private static <T extends SimpleRotatingContraptionEntity> InteractionResult disassemble(Player player,
                                                                                             IContraptionMultiblock<T> controller) {
        controller.findAndReattachEntities();
        sanitizeTrackedEntities(controller);
        if (controller.getContraptionEntity() == null || controller.getContraptionEntity().isEmpty()) {
            player.sendSystemMessage(disassembler_no_entities.translate()
                    .withStyle(ChatFormatting.YELLOW));
            return InteractionResult.SUCCESS;
        }

        int entityCount = controller.getContraptionEntity().size();
        controller.clearAndDisassembleRotatingEntities();
        controller.self().notifyBlockUpdate();
        player.sendSystemMessage(disassembler_success.translate(entityCount).withStyle(ChatFormatting.GREEN));
        return InteractionResult.SUCCESS;
    }

    private static String formatBlockPos(BlockPos pos) {
        return pos.getX() + ", " + pos.getY() + ", " + pos.getZ();
    }

    private static String formatVec3(Vec3 vec3) {
        return String.format(Locale.ROOT, "%.2f, %.2f, %.2f", vec3.x, vec3.y, vec3.z);
    }

    private static <T extends SimpleRotatingContraptionEntity> void sanitizeTrackedEntities(
                                                                                            IContraptionMultiblock<T> controller) {
        List<T> trackedEntities = controller.getContraptionEntity();
        if (trackedEntities == null || trackedEntities.isEmpty()) return;

        List<T> liveEntities = trackedEntities.stream()
                .filter(entity -> entity != null && entity.isAlive() && !entity.isRemoved())
                .toList();
        if (liveEntities.size() != trackedEntities.size()) {
            controller.setContraptionEntity(new ArrayList<>(liveEntities));
        }
    }

    public enum Mode {

        LOCATE("locator"),
        ASSEMBLE("assembler"),
        DISASSEMBLE("disassembler");

        private final String id;

        Mode(String id) {
            this.id = id;
        }

        public Lang tooltip() {
            return switch (this) {
                case LOCATE -> locator_tooltip;
                case ASSEMBLE -> assembler_tooltip;
                case DISASSEMBLE -> disassembler_tooltip;
            };
        }
    }
}
