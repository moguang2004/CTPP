package com.mo_guang.ctpp.api;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.Set;

/**
 * Extended energy container interface for transfer handlers that accept energy with a
 * visited-position exclusion set (used by long-distance energy bridges to avoid loops).
 */
public interface IEnergyTransferHandler extends IEnergyContainer {

    /**
     * Accept energy from the network, skipping the given positions when walking the network.
     * The default implementation ignores the exclusion set and behaves like a plain container.
     *
     * @param side     the side the energy comes from
     * @param voltage  energy packet size
     * @param amperage number of packets
     * @param excluded positions that must not be visited again
     * @return amount of used amperes. 0 if not accepted anything.
     */
    default long acceptEnergyFromNetwork(Direction side, long voltage, long amperage, Set<BlockPos> excluded) {
        return acceptEnergyFromNetwork(side, voltage, amperage);
    }
}
