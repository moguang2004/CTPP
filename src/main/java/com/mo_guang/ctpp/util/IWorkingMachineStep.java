package com.mo_guang.ctpp.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import com.negodya1.vintageimprovements.VintageBlocks;
import com.negodya1.vintageimprovements.compat.jei.category.assemblies.AssemblyCentrifugation;
import com.negodya1.vintageimprovements.compat.jei.category.assemblies.AssemblyCoiling;
import com.negodya1.vintageimprovements.compat.jei.category.assemblies.AssemblyCurving;
import com.negodya1.vintageimprovements.compat.jei.category.assemblies.AssemblyHammering;
import com.negodya1.vintageimprovements.compat.jei.category.assemblies.AssemblyLaserCutting;
import com.negodya1.vintageimprovements.compat.jei.category.assemblies.AssemblyPolishing;
import com.negodya1.vintageimprovements.compat.jei.category.assemblies.AssemblyPressurizing;
import com.negodya1.vintageimprovements.compat.jei.category.assemblies.AssemblyTurning;
import com.negodya1.vintageimprovements.compat.jei.category.assemblies.AssemblyVacuumizing;
import com.negodya1.vintageimprovements.compat.jei.category.assemblies.AssemblyVibrating;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.sequencedAssembly.SequencedAssemblySubCategory;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.recipe.RecipeIngredientRole;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IWorkingMachineStep {

    Map<Class<?>, ItemStack> workMachineMap = new HashMap<>();

    @SuppressWarnings("removal")
    default void addWorkMachine(IRecipeLayoutBuilder builder, int x) {
        var workMachine = getWorkMachineMap().get(this.getClass());
        if (workMachine != null) {
            var slot = builder.addSlot(RecipeIngredientRole.CATALYST, x + 2, 33)
                    .addItemStack(workMachine)
                    .addTooltipCallback(new IRecipeSlotTooltipCallback() {

                        @Override
                        public void onTooltip(IRecipeSlotView iRecipeSlotView, List<Component> list) {
                            list.clear();
                        }
                    });
            if (slot instanceof ICustomSlot hideSlotBuilder) {
                hideSlotBuilder.ctpp$setHide();
                hideSlotBuilder.ctpp$setRect(20, 50);
            }
        }
    };

    static Map<Class<?>, ItemStack> getWorkMachineMap() {
        if (workMachineMap.isEmpty()) {
            workMachineMap.put(SequencedAssemblySubCategory.AssemblyPressing.class,
                    AllBlocks.MECHANICAL_PRESS.asStack());
            workMachineMap.put(SequencedAssemblySubCategory.AssemblySpouting.class, AllBlocks.SPOUT.asStack());
            workMachineMap.put(SequencedAssemblySubCategory.AssemblyCutting.class, AllBlocks.MECHANICAL_SAW.asStack());
            workMachineMap.put(SequencedAssemblySubCategory.AssemblyDeploying.class, AllBlocks.DEPLOYER.asStack());
            workMachineMap.put(AssemblyCentrifugation.class, VintageBlocks.CENTRIFUGE.asStack());
            workMachineMap.put(AssemblyCoiling.class, VintageBlocks.SPRING_COILING_MACHINE.asStack());
            workMachineMap.put(AssemblyCurving.class, VintageBlocks.CURVING_PRESS.asStack());
            workMachineMap.put(AssemblyHammering.class, VintageBlocks.HELVE.asStack());
            workMachineMap.put(AssemblyLaserCutting.class, VintageBlocks.LASER.asStack());
            workMachineMap.put(AssemblyPolishing.class, VintageBlocks.BELT_GRINDER.asStack());
            workMachineMap.put(AssemblyPressurizing.class, VintageBlocks.VACUUM_CHAMBER.asStack());
            workMachineMap.put(AssemblyTurning.class, VintageBlocks.LATHE_ROTATING.asStack());
            workMachineMap.put(AssemblyVacuumizing.class, VintageBlocks.VACUUM_CHAMBER.asStack());
            workMachineMap.put(AssemblyVibrating.class, VintageBlocks.VIBRATING_TABLE.asStack());
        }
        return workMachineMap;
    }
}
