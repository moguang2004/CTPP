package com.mo_guang.ctpp.client.ponder.kinetic;

import com.gregtechceu.gtceu.api.GTValues;

import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.Direction;

import com.mo_guang.ctpp.client.ponder.CTPPPonderSceneBuilder;
import com.mo_guang.ctpp.registry.CTPPMachines;

public class KineticHatch {

    private KineticHatch() {}

    public static void Common(SceneBuilder builder, SceneBuildingUtil util) {
        CTPPPonderSceneBuilder scene = new CTPPPonderSceneBuilder(builder);
        scene.title("kinetic_hatch_common", "Kinetic Input and Output Hatch", "应力输入仓与应力输出仓");
        scene.configureBasePlate(0, 0, 6);
        scene.scaleSceneView(0.75f);
        scene.idle(10);

        scene.world().showSection(util.select().layer(0), Direction.DOWN);
        scene.world().setBlock(util.grid().at(1, 1, 3),
                CTPPMachines.KINETIC_INPUT_BOX[GTValues.IV].defaultBlockState(), false);
        scene.world().setBlock(util.grid().at(2, 1, 3),
                CTPPMachines.KINETIC_INPUT_BOX[GTValues.EV].defaultBlockState(), false);
        scene.world().setBlock(util.grid().at(3, 1, 3),
                CTPPMachines.KINETIC_INPUT_BOX[GTValues.HV].defaultBlockState(), false);
        scene.world().setBlock(util.grid().at(4, 1, 3),
                CTPPMachines.KINETIC_INPUT_BOX[GTValues.MV].defaultBlockState(), false);
        scene.world().setBlock(util.grid().at(5, 1, 3),
                CTPPMachines.KINETIC_INPUT_BOX[GTValues.LV].defaultBlockState(), false);
        scene.world().showSection(util.select().fromTo(1, 1, 3, 5, 1, 3), Direction.DOWN);
        scene.idle(80);

        scene.overlay().showOutline(PonderPalette.RED, "kinetic_hatches", util.select().fromTo(1, 1, 3, 5, 1, 3),
                60);
        scene.showText(60, "Kinetic hatches have different voltage-style tiers.", "应力仓拥有类似电压等级的不同等级。")
                .pointAt(util.vector().blockSurface(util.grid().at(1, 1, 3), Direction.UP))
                .attachKeyFrame();
        scene.idle(80);

        scene.overlay().showOutline(PonderPalette.RED, "lv_hatch", util.select().position(5, 1, 3), 60);
        scene.showText(60,
                "LV is the lowest practical tier. At 256 RPM, it can input or output 32768 su.",
                "LV 是最低可用等级，在 256 RPM 下可输入或输出 32768 su。")
                .pointAt(util.vector().blockSurface(util.grid().at(5, 1, 3), Direction.UP))
                .attachKeyFrame();
        scene.idle(80);

        scene.showText(120,
                "Similar to GregTech tier scaling, each higher tier requires four times the stress.",
                "类似 GregTech 的等级增幅，每提升一级需要 4 倍应力。")
                .pointAt(util.vector().blockSurface(util.grid().at(1, 1, 3), Direction.UP))
                .attachKeyFrame();
        showStressTier(scene, util, 4, "131072 su");
        showStressTier(scene, util, 3, "524288 su");
        showStressTier(scene, util, 2, "2097152 su");
        showStressTier(scene, util, 1, "8388608 su");
        scene.markAsFinished();
    }

    private static void showStressTier(CTPPPonderSceneBuilder scene, SceneBuildingUtil util, int x, String stress) {
        scene.overlay().showOutline(PonderPalette.RED, stress, util.select().position(x, 1, 3), 20);
        scene.showText(20, stress, stress)
                .pointAt(util.vector().blockSurface(util.grid().at(x, 1, 3), Direction.UP))
                .attachKeyFrame();
        scene.idle(30);
    }
}
