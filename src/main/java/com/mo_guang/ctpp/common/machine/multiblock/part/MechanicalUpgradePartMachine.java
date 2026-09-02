package com.mo_guang.ctpp.common.machine.multiblock.part;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredPartMachine;

import net.minecraft.network.chat.Component;

import com.ctnhlang.CN;
import com.ctnhlang.EN;
import com.mo_guang.ctpp.common.machine.multiblock.KineticMultiblockMachine;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;

import java.util.List;

public class MechanicalUpgradePartMachine extends TieredPartMachine {

    @CN("当前机械等级：%d(%s)")
    @EN("Current Mechanical Tier：%d(%s)")
    static Lang mechanicalTier;

    public MechanicalUpgradePartMachine(IMachineBlockEntity holder, int tier) {
        super(holder, tier);
    }

    public int getMechanicalTier() {
        return getTier();
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
        textList.add(mechanicalTier.translate(tier, GTValues.VNF[tier]));
    }

    @Override
    public boolean canShared() {
        return false;
    }
}
