package com.mo_guang.ctpp.client.terminal;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.network.GTNetwork;
import com.gregtechceu.gtceu.data.recipe.CustomTags;

import net.createmod.catnip.outliner.Outliner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.client.renderer.CTPPWireRenderTypes;
import com.mo_guang.ctpp.client.renderer.VoltageTerminalRenderer;
import com.mo_guang.ctpp.common.blockentity.VoltageTerminalBlockEntity;
import com.mo_guang.ctpp.network.packet.CTPPTerminalCancelWireSelectionPacket;

@Mod.EventBusSubscriber(modid = CTPP.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class TerminalClientSelectionEvents {

    private static final Object WIRE_OUTLINE = "ctpp_terminal_wire_target";
    private static final Object CUTTER_OUTLINE = "ctpp_terminal_cutter_target";

    private TerminalClientSelectionEvents() {}

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getLevel().isClientSide ||
                !(event.getLevel().getBlockEntity(event.getPos()) instanceof VoltageTerminalBlockEntity))
            return;
        ItemStack stack = event.getItemStack();
        if (stack.is(CustomTags.WIRE_CUTTERS)) {
            if (event.getEntity().isShiftKeyDown()) {
                TerminalClientSelection.clear();
            } else if (TerminalClientSelection.cutterTarget() != null) {
                // The second click is sent to the server as the other endpoint;
                // the local, non-persistent selection ends immediately.
                TerminalClientSelection.clear();
            } else {
                TerminalClientSelection.selectCutter(event.getPos());
            }
        } else if (ChemicalHelper.getPrefix(stack.getItem()) == TagPrefix.wireFine) {
            if (event.getEntity().isShiftKeyDown()) {
                // Explicitly synchronize cancellation. The client may already
                // have cleared its visual target, while the server still owns
                // the authoritative selection.
                GTNetwork.sendToServer(new CTPPTerminalCancelWireSelectionPacket(event.getPos()));
                if (TerminalClientSelection.wireTarget() != null &&
                        TerminalClientSelection.wireTarget().equals(event.getPos())) {
                    TerminalClientSelection.clearWire();
                }
                event.setCanceled(true);
            } else if (TerminalClientSelection.wireTarget() == null) {
                TerminalClientSelection.selectWire(event.getPos(), stack);
            } else if (TerminalClientSelection.wireTarget().equals(event.getPos())) {
                // Keep the current multiplier until the authoritative server
                // response arrives, avoiding a one-frame reset to 1x.
            } else {
                // Keep the first endpoint until the server confirms completion
                // or rejection through CTPPTerminalWireSelectionPacket.
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        Outliner outliner = Outliner.getInstance();
        if (mc.level == null || mc.player == null) {
            TerminalClientSelection.clear();
            outliner.remove(WIRE_OUTLINE);
            outliner.remove(CUTTER_OUTLINE);
            return;
        }
        ItemStack held = isFineWire(mc.player.getMainHandItem()) ? mc.player.getMainHandItem() :
                mc.player.getOffhandItem();
        boolean wireHeld = isFineWire(held);
        boolean cutterHeld = isCutter(mc.player.getMainHandItem()) || isCutter(mc.player.getOffhandItem());
        BlockPos wireTarget = TerminalClientSelection.wireTarget();
        if (wireTarget != null && (!wireHeld || !terminalExists(mc, wireTarget) ||
                !sameWire(TerminalClientSelection.wireItem(), held))) {
            TerminalClientSelection.clearWire();
            wireTarget = null;
        }
        BlockPos cutterTarget = TerminalClientSelection.cutterTarget();
        if (cutterTarget != null && (!cutterHeld || !terminalExists(mc, cutterTarget))) {
            TerminalClientSelection.clearCutter();
            cutterTarget = null;
        }
        if (wireTarget != null && wireHeld) {
            outliner.showAABB(WIRE_OUTLINE, outlineBox(mc, wireTarget))
                    .colored(0x7FCDE0).lineWidth(0.0625f);
            outliner.remove(CUTTER_OUTLINE);
        } else {
            outliner.remove(WIRE_OUTLINE);
            outliner.remove(CUTTER_OUTLINE);
        }
        if (cutterTarget != null && cutterHeld) {
            outliner.remove(WIRE_OUTLINE);
            outliner.showAABB(CUTTER_OUTLINE, outlineBox(mc, cutterTarget))
                    .colored(0xF5C542).lineWidth(0.0625f);
        } else {
            outliner.remove(CUTTER_OUTLINE);
        }
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || !mc.options.getCameraType().isFirstPerson()) {
            return;
        }
        BlockPos target = TerminalClientSelection.wireTarget();
        ItemStack held = isFineWire(mc.player.getMainHandItem()) ? mc.player.getMainHandItem() :
                mc.player.getOffhandItem();
        if (target == null || !isFineWire(held) || !terminalExists(mc, target) ||
                !sameWire(TerminalClientSelection.wireItem(), held)) {
            return;
        }
        float partialTick = event.getPartialTick();
        Vec3 start = firstPersonHand(mc, partialTick);
        Vec3 end = Vec3.atLowerCornerOf(target).add(0.5, 0.5, 0.5);
        var poseStack = event.getPoseStack();
        var cameraPos = event.getCamera().getPosition();
        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        var buffers = mc.renderBuffers().bufferSource();
        VoltageTerminalRenderer.renderPreview(poseStack, buffers, start, end,
                held, TerminalClientSelection.wireMultiplier(), LevelRenderer.getLightColor(mc.level, target));
        buffers.endBatch(CTPPWireRenderTypes.wire());
        poseStack.popPose();
    }

    private static boolean terminalExists(Minecraft mc, BlockPos pos) {
        BlockEntity be = mc.level.getBlockEntity(pos);
        return be instanceof VoltageTerminalBlockEntity;
    }

    private static net.minecraft.world.phys.AABB outlineBox(Minecraft mc, BlockPos pos) {
        // Matches Create's ArmInteractionPointHandler: render the block shape,
        // not a replacement full-cube outline.
        return mc.level.getBlockState(pos).getShape(mc.level, pos).bounds().move(pos);
    }

    private static boolean isFineWire(ItemStack stack) {
        return !stack.isEmpty() && ChemicalHelper.getPrefix(stack.getItem()) == TagPrefix.wireFine;
    }

    private static boolean isCutter(ItemStack stack) {
        return stack.is(CustomTags.WIRE_CUTTERS);
    }

    private static boolean sameWire(ItemStack first, ItemStack second) {
        return !first.isEmpty() && !second.isEmpty() && ItemStack.isSameItemSameTags(first, second);
    }

    private static Vec3 firstPersonHand(Minecraft mc, float partialTick) {
        Vec3 eye = mc.player.getEyePosition(partialTick);
        Vec3 look = mc.player.getViewVector(partialTick).normalize();
        Vec3 up = new Vec3(0, 1, 0);
        Vec3 right = look.cross(up);
        if (right.lengthSqr() < 1.0e-6) right = new Vec3(1, 0, 0);
        else right = right.normalize();
        double handSide = mc.player.getMainArm() == net.minecraft.world.entity.HumanoidArm.LEFT ? -1 : 1;
        return eye.add(look.scale(0.35)).add(right.scale(0.2 * handSide)).add(up.scale(-0.18));
    }
}
