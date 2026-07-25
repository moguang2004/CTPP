package com.mo_guang.ctpp.mixin.create.diesel;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

import com.ctnhlang.CN;
import com.ctnhlang.Category;
import com.ctnhlang.EN;
import com.jesz.createdieselgenerators.compat.jei.BasinFermentingCategory;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;

@Category("tooltip")
@Mixin(value = BasinFermentingCategory.class, remap = false)
public class BasinFermentingCategoryMixin {

    @EN("This recipe type requires an exact burner heat level")
    @CN("该配方类型需要严格对应的燃烧等级")
    private static Lang exactBurnerLevel;

    @Inject(method = "draw(Lcom/simibubi/create/content/processing/basin/BasinRecipe;Lmezz/jei/api/gui/ingredient/IRecipeSlotsView;Lnet/minecraft/client/gui/GuiGraphics;DD)V",
            at = @At("HEAD"))
    void insertTooltip(BasinRecipe recipe, IRecipeSlotsView iRecipeSlotsView, GuiGraphics graphics, double mouseX,
                       double mouseY, CallbackInfo ci) {
        graphics.drawString(Minecraft.getInstance().font,
                exactBurnerLevel.translate(),
                9, 3, ChatFormatting.YELLOW.getColor(), false);
    }
}
