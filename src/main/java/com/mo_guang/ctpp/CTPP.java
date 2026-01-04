package com.mo_guang.ctpp;

import com.mo_guang.ctpp.client.ClientProxy;
import com.mo_guang.ctpp.common.CommonProxy;
import com.mo_guang.ctpp.common.data.recipe.CTPPFanProcessingTypes;
import com.mo_guang.ctpp.common.data.recipe.CTPPRecipeTypeInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

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

    }

    public static ResourceLocation id(String name) {
        return new ResourceLocation(MODID, name);
    }
}
