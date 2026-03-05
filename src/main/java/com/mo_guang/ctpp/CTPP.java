package com.mo_guang.ctpp;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

import com.mo_guang.ctpp.client.ClientProxy;
import com.mo_guang.ctpp.common.CommonProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.vixhentx.mcmod.ctnhlib.langprovider.LangProcessor;

import static com.mo_guang.ctpp.CTPPRegistration.REGISTRATE;

@Mod(CTPP.MODID)
@SuppressWarnings("removal")
public class CTPP {

    public static final String MODID = "ctpp";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public CTPP() {
        LangProcessor langProcessor = new LangProcessor(REGISTRATE);
        langProcessor.processAll();

        DistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);
        CTPPEntityTypes.init();
    }

    public static ResourceLocation id(String name) {
        return new ResourceLocation(MODID, name);
    }
}
