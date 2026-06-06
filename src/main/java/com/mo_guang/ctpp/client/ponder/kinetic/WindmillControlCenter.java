package com.mo_guang.ctpp.client.ponder.kinetic;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMachines;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.Direction;

import com.mo_guang.ctpp.client.ponder.CTPPPonderSceneBuilder;
import com.mo_guang.ctpp.registry.CTPPMachines;

// TODO: 待重写
public class WindmillControlCenter {

    private WindmillControlCenter() {}

    public static void Common(SceneBuilder builder, SceneBuildingUtil util) {
        CTPPPonderSceneBuilder scene = new CTPPPonderSceneBuilder(builder);
        scene.title("windmill_control_center_common", "How to build Windmill Control Center", "如何搭建风车控制中心");
        scene.configureBasePlate(0, 0, 6);
        scene.scaleSceneView(0.4f);

        Selection windmill1 = util.select().fromTo(2, 6, 2, 2, 14, 2);
        Selection windmill2 = util.select().fromTo(2, 6, 4, 2, 14, 4);
        Selection windmill3 = util.select().fromTo(4, 6, 2, 4, 14, 2);
        Selection windmill4 = util.select().fromTo(4, 6, 4, 4, 14, 4);

        scene.idle(10);
        scene.world().showSection(util.select().position(3, 2, 2), Direction.DOWN);
        scene.showText(60, "First, you need a windmill control center main block", "首先放置风车控制中心主方块")
                .pointAt(util.vector().blockSurface(util.grid().at(3, 2, 2), Direction.WEST))
                .attachKeyFrame();
        scene.idle(60);

        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(3, 2, 2), Direction.WEST),
                Pointing.LEFT, 40)
                .rightClick()
                .withItem(GTItems.TERMINAL.asStack())
                .whileSneaking();
        scene.idle(40);
        scene.world().showSection(util.select().fromTo(1, 1, 1, 5, 4, 5), Direction.DOWN);
        scene.showText(40, "Use a terminal for one-click placement.", "使用终端一键放置结构。")
                .attachKeyFrame();

        scene.idle(60);
        scene.world().setBlock(util.grid().at(3, 3, 2), GTMachines.FLUID_IMPORT_HATCH[GTValues.LV].defaultBlockState(),
                true);
        scene.showText(60, "Place an input hatch so lubricant can be supplied.", "放置输入仓以输入润滑油。")
                .pointAt(util.vector().blockSurface(util.grid().at(3, 3, 2), Direction.WEST))
                .attachKeyFrame();
        scene.idle(20);
        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(3, 3, 2), Direction.WEST),
                Pointing.LEFT, 40)
                .rightClick()
                .withItem(GTMaterials.Lubricant.getBucket().getDefaultInstance());

        scene.idle(80);
        scene.world().setBlock(util.grid().at(2, 3, 2),
                CTPPMachines.KINETIC_OUTPUT_BOX[GTValues.LV].defaultBlockState(), true);
        scene.world().setBlock(util.grid().at(4, 3, 2),
                CTPPMachines.KINETIC_OUTPUT_BOX[GTValues.LV].defaultBlockState(), true);
        scene.world().setBlock(util.grid().at(2, 2, 2),
                CTPPMachines.KINETIC_OUTPUT_BOX[GTValues.LV].defaultBlockState(), true);
        scene.world().setBlock(util.grid().at(4, 2, 2),
                CTPPMachines.KINETIC_OUTPUT_BOX[GTValues.LV].defaultBlockState(), true);
        scene.showText(60, "Place enough kinetic output hatches to export the generated stress.",
                "放置足够的应力输出仓以导出产生的应力。")
                .attachKeyFrame();

        scene.idle(80);
        scene.world().showSection(util.select().fromTo(2, 5, 2, 4, 5, 4), Direction.DOWN);
        scene.showText(60,
                "Place windmill bearings within 16 blocks of the controller. More bearings mean more output, up to 16 bearings.",
                "在主方块 16 格半径内放置风车轴承。轴承越多输出越高，最多可计入 16 个。")
                .attachKeyFrame();

        scene.idle(80);
        scene.showText(60, "Attach 128 sails or wool to each windmill bearing for maximum stress.",
                "给每个风车轴承连接 128 个风帆或羊毛以获得最大应力。")
                .attachKeyFrame();
        ElementLink<WorldSectionElement> windmillLink1 = scene.world().showIndependentSection(windmill1,
                Direction.DOWN);
        ElementLink<WorldSectionElement> windmillLink2 = scene.world().showIndependentSection(windmill2,
                Direction.DOWN);
        ElementLink<WorldSectionElement> windmillLink3 = scene.world().showIndependentSection(windmill3,
                Direction.DOWN);
        ElementLink<WorldSectionElement> windmillLink4 = scene.world().showIndependentSection(windmill4,
                Direction.DOWN);

        scene.idle(80);
        scene.showText(60, "Activate the windmill bearings.", "激活风车轴承。")
                .attachKeyFrame();
        scene.overlay().showControls(util.vector().blockSurface(util.grid().at(2, 5, 2), Direction.UP), Pointing.LEFT,
                40)
                .rightClick();
        scene.world().rotateSection(windmillLink1, 0, 360, 0, 400);
        scene.world().rotateSection(windmillLink2, 0, 360, 0, 400);
        scene.world().rotateSection(windmillLink3, 0, 360, 0, 400);
        scene.world().rotateSection(windmillLink4, 0, 360, 0, 400);

        scene.idle(80);
        scene.showText(60, "With sufficient output hatches, the controller can now export windmill stress.",
                "拥有足够输出仓后，控制中心即可导出风车应力。")
                .attachKeyFrame();
        scene.markAsFinished();
    }
}
