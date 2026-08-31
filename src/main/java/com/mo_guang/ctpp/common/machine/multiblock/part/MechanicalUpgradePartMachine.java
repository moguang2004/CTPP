package com.mo_guang.ctpp.common.machine.multiblock.part;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredPartMachine;

import net.minecraft.network.chat.Component;

import com.ctnhlang.CN;
import com.ctnhlang.EN;
import com.mo_guang.ctpp.common.machine.multiblock.KineticMultiblockMachine;
import com.mo_guang.ctpp.util.CTPPValues;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;

import java.util.List;

/**
 * 机械升级仓：不再有物品栏，机械等级由仓自身的 GT 等级决定（LV → 2，MV → 4，HV → 5）。
 */
public class MechanicalUpgradePartMachine extends TieredPartMachine {

    public static final int MAX_MECHANICAL_TIER = 5;

    @CN("当前机械等级：%d(%s)")
    @EN("Current Mechanical Tier：%d(%s)")
    static Lang mechanicalTier;

    public MechanicalUpgradePartMachine(IMachineBlockEntity holder, int tier) {
        super(holder, tier);
    }

    public static int getMechanicalTier(int tier) {
        return Math.min(tier * 2, MAX_MECHANICAL_TIER);
    }

    public int getMechanicalTier() {
        return getMechanicalTier(getTier());
    }

    @Override
    public void addedToController(IMultiController controller) {
        super.addedToController(controller);
        noticeController();
    }

    @Override
    public void removedFromController(IMultiController controller) {
        super.removedFromController(controller);
        if (controller instanceof KineticMultiblockMachine kineticMultiblockMachine) {
            kineticMultiblockMachine.checkTier();
            kineticMultiblockMachine.onTierChanged();
        }
    }

    public void noticeController() {
        if (!getControllers().isEmpty() &&
                getControllers().first() instanceof KineticMultiblockMachine kineticMultiblockMachine) {
            kineticMultiblockMachine.checkTier();
            kineticMultiblockMachine.onTierChanged();
        }
    }

    @Override
    public void addMultiText(List<Component> textList) {
        super.addMultiText(textList);
        int tier = getMechanicalTier();
        textList.add(mechanicalTier.translate(tier, CTPPValues.MT[tier].translate()));
    }

    @Override
    public boolean canShared() {
        return false;
    }
}
