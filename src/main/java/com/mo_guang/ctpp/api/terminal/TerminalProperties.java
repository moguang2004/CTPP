package com.mo_guang.ctpp.api.terminal;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.WireProperties;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/** CTPP-owned terminal and fine-wire electrical properties. */
public final class TerminalProperties {

    private TerminalProperties() {}

    public record FineWireSpec(long voltage, long amperage, int lossPerBlock) {
        public int loss(BlockPos first, BlockPos second) {
            return Math.max(1, (int) Math.ceil(Math.sqrt(first.distSqr(second)) * lossPerBlock));
        }

        public static @Nullable FineWireSpec from(ItemStack stack) {
            if (stack.isEmpty() || ChemicalHelper.getPrefix(stack.getItem()) != TagPrefix.wireFine) {
                return null;
            }
            Material material = ChemicalHelper.getMaterialStack(stack).material();
            WireProperties wire = material.getProperty(PropertyKey.WIRE);
            if (wire == null) {
                return new FineWireSpec(GTValues.V[GTValues.LV], 1, 1);
            }
            return new FineWireSpec(wire.getVoltage(), Math.max(1, wire.getAmperage()),
                    Math.max(1, wire.getLossPerBlock()));
        }
    }

    public static final class Link {
        private static final int DEFAULT_TEMPERATURE = 293;
        private static final int MELT_TEMPERATURE = 3000;

        private final BlockPos other;
        private final FineWireSpec wire;
        private int temperature = DEFAULT_TEMPERATURE;
        private int heatQueue;

        public Link(BlockPos other, FineWireSpec wire) {
            this.other = other.immutable();
            this.wire = wire;
        }

        public BlockPos other() {
            return other;
        }

        public FineWireSpec wire() {
            return wire;
        }

        public int getTemperature() {
            return temperature;
        }

        public int getHeatQueue() {
            return heatQueue;
        }

        public void applyHeat(int amount) {
            heatQueue = Math.min(Integer.MAX_VALUE - heatQueue, Math.max(0, amount)) + heatQueue;
        }

        public boolean tick() {
            if (heatQueue > 0) {
                temperature = Math.min(Integer.MAX_VALUE, temperature + heatQueue);
                heatQueue = 0;
            } else if (temperature > DEFAULT_TEMPERATURE) {
                temperature = Math.max(DEFAULT_TEMPERATURE,
                        (int) (temperature - Math.pow(temperature - DEFAULT_TEMPERATURE, 0.35)));
            }
            return temperature >= MELT_TEMPERATURE;
        }

        public void loadHeat(int temperature, int heatQueue) {
            this.temperature = Math.max(DEFAULT_TEMPERATURE, temperature);
            this.heatQueue = Math.max(0, heatQueue);
        }
    }
}
