package com.mo_guang.ctpp.api.terminal;

import com.gregtechceu.gtceu.api.data.chemical.material.properties.WireProperties;
import com.gregtechceu.gtceu.api.pipenet.IPipeType;

import net.minecraft.resources.ResourceLocation;

import com.mo_guang.ctpp.CTPP;

public enum VoltageTerminal implements IPipeType<WireProperties> {

    TERMINAL;

    @Override
    public float getThickness() {
        return 0;
    }

    @Override
    public WireProperties modifyProperties(WireProperties baseProperties) {
        return baseProperties;
    }

    @Override
    public boolean isPaintable() {
        return false;
    }

    @Override
    public ResourceLocation type() {
        return CTPP.id("terminal");
    }
}
