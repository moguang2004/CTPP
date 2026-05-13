package com.mo_guang.ctpp;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

import com.mo_guang.ctpp.client.ClientProxy;
import com.mo_guang.ctpp.common.CommonProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(CTPP.MODID)
@SuppressWarnings("removal")
public class CTPP {

    public static final String MODID = "ctpp";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public CTPP() {
        DistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
        CTPPEntityTypes.init();
    }

    public static ResourceLocation id(String name) {
        return new ResourceLocation(MODID, name);
    }
}
