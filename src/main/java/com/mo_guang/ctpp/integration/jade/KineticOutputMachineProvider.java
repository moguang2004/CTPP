package com.mo_guang.ctpp.integration.jade;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.integration.jade.provider.CapabilityBlockProvider;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import com.ctnhlang.CN;
import com.ctnhlang.EN;
import com.ctnhlang.Key;
import com.mo_guang.ctpp.CTPP;
import com.mo_guang.ctpp.common.machine.multiblock.KineticOutputMachine;
import com.mo_guang.ctpp.data.recipe.builder.CTPPRecipeHelper;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;

public class KineticOutputMachineProvider extends CapabilityBlockProvider<KineticOutputMachine> {

    @Key("config.jade.plugin_ctpp.kinetic_output_machine_provider")
    @CN("[CTPP]应力输出")
    @EN("[CTPP] Kinetic Output Stress")
    public static Lang configJadePluginCtppKineticOutputMachineProvider;

    public KineticOutputMachineProvider() {
        super(CTPP.id("kinetic_output_machine_provider"));
    }

    @Override
    protected @Nullable KineticOutputMachine getCapability(Level level, BlockPos pos, @Nullable Direction side) {
        return MetaMachine.getMachine(level, pos) instanceof KineticOutputMachine machine ? machine : null;
    }

    @Override
    protected void write(CompoundTag data, KineticOutputMachine machine) {
        if (!machine.isFormed()) return;
        data.putBoolean("Working", machine.getRecipeLogic().isWorking());
        data.putFloat("MaxOutputStress", machine.getMaxOutputStress());
        var recipe = machine.getRecipeLogic().getLastRecipe();
        if (recipe != null) {
            data.putFloat("OutputStress", CTPPRecipeHelper.getOutputStress(recipe));
        }
    }
    @CN("应力输出：%s")
    @EN("Stress Output: %s")
    static Lang kineticOutput;

    @Override
    protected void addTooltip(CompoundTag capData, ITooltip tooltip, Player player, BlockAccessor block,
                              BlockEntity blockEntity, IPluginConfig config) {
        if (capData.getBoolean("Working") && capData.contains("OutputStress")) {
            Component stress = Component.literal(String.format("%s SU",
                    FormattingUtil.formatNumber2Places(capData.getFloat("OutputStress"))))
                    .withStyle(ChatFormatting.GOLD);
            tooltip.add(kineticOutput.translate(stress)
                    .withStyle(ChatFormatting.GRAY));
        }
    }
}
