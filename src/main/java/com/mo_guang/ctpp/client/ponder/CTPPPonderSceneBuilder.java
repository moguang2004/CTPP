package com.mo_guang.ctpp.client.ponder;

import com.gregtechceu.gtceu.GTCEu;

import net.createmod.ponder.api.scene.SceneBuilder;

import com.mo_guang.ctpp.CTPP;
import tech.vixhentx.mcmod.ctnhlib.client.ponder.CTNHPonderSceneBuilder;

import static com.mo_guang.ctpp.CTPPRegistration.REGISTRATE;

public class CTPPPonderSceneBuilder extends CTNHPonderSceneBuilder {

    public CTPPPonderSceneBuilder(SceneBuilder builder) {
        super(builder, CTPP.MODID, CTPPPonderSceneBuilder::registerLang);
    }

    private static void registerLang(String key, String en, String cn) {
        if (GTCEu.isDataGen()) {
            REGISTRATE.genLang(key, en, cn);
        }
    }
}
