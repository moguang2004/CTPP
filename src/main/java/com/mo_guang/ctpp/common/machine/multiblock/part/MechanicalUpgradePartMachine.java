package com.mo_guang.ctpp.common.machine.multiblock.part;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.SlotWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.IMachineModifyDrops;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDistinctPart;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.jei.IngredientIO;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.mo_guang.ctpp.CTPPItems;
import com.mo_guang.ctpp.common.machine.multiblock.KineticMultiblockMachine;
import com.mo_guang.ctpp.util.CTPPValues;
import com.simibubi.create.AllItems;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MechanicalUpgradePartMachine extends TieredIOPartMachine implements IMachineModifyDrops, IDistinctPart {
    @Getter
    @Persisted
    private final NotifiableItemStackHandler inventory;
    @Persisted
    public int tier = 0;
    public static Map<Item, Integer> tierMap = new HashMap<>();
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(MechanicalUpgradePartMachine.class,
            TieredIOPartMachine.MANAGED_FIELD_HOLDER);
    public MechanicalUpgradePartMachine(IMachineBlockEntity holder) {
        super(holder, GTValues.ULV, IO.IN);
        tierMap.put(AllItems.PRECISION_MECHANISM.get(), 2);
        tierMap.put(CTPPItems.BASIC_MECHANISM.get(), 1);
        tierMap.put(CTPPItems.STEEL_MECHANISM.get(), 3);
        tierMap.put(GTItems.ELECTRONIC_CIRCUIT_LV.get(), 3);
        tierMap.put(GTItems.ELECTRONIC_CIRCUIT_MV.get(), 4);
        tierMap.put(GTItems.INTEGRATED_CIRCUIT_HV.get(), 5);
        this.inventory = new NotifiableItemStackHandler(this, 1, IO.IN) {
            @Override
            public void onContentsChanged() {
                super.onContentsChanged();
                checkTier();
            }
        }.setFilter(itemStack -> tierMap.containsKey(itemStack.getItem()));
    }
    public void checkTier() {
        if (inventory.isEmpty()) {
            tier = 0;
        }
        else {
            var itemStack = inventory.getStackInSlot(0);
            if (!itemStack.isEmpty()) {
                tier = tierMap.get(itemStack.getItem());
            }
            else tier = 0;
        }
        noticeController();
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
            kineticMultiblockMachine.tier = 0;
        }
    }

    public void noticeController() {
        if (!getControllers().isEmpty() && getControllers().first() instanceof KineticMultiblockMachine kineticMultiblockMachine) {
            kineticMultiblockMachine.tier = tier;
        }
    }

    @Override
    public void addMultiText(List<Component> textList) {
        super.addMultiText(textList);
        textList.add(textList.size(), Component.translatable("ctpp.multiblock.mechanical_tier", tier, CTPPValues.MT[tier]));
    }

    @Override
    public void onDrops(List<ItemStack> drops) {
        clearInventory(getInventory().storage);
    }

    @Override
    public boolean isDistinct() {
        return getInventory().isDistinct();
    }

    @Override
    public void setDistinct(boolean isDistinct) {
        getInventory().setDistinct(isDistinct);
    }

    @Override
    public Widget createUIWidget() {
        var group = new WidgetGroup(0, 0, 34, 34);
        var container = new WidgetGroup(4, 4, 26, 26);
        int index = 0;
        container.addWidget(
                new SlotWidget(getInventory().storage, index++, 4, 4, true, io.support(IO.IN))
                        .setBackgroundTexture(GuiTextures.SLOT)
                        .setIngredientIO(IngredientIO.INPUT));

        container.setBackground(GuiTextures.BACKGROUND_INVERSE);
        group.addWidget(container);

        return group;
    }

    @Override
    public boolean canShared() {
        return false;
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}
