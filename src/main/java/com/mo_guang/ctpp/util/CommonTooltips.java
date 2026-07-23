package com.mo_guang.ctpp.util;

import com.ctnhlang.CN;
import com.ctnhlang.EN;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;

public class CommonTooltips {

    @CN("-输入配方所需的(N的平方)倍应力时，获得N并行")
    @EN("- Input (N squared) times stress required by the recipe to get N parallels")
    public static Lang KINETIC_OVERCLOCK;

    @CN("*输入转速以等级最高的应力仓的转速为准")
    @EN("*The input speed is based on the highest-speed stress chamber.")
    public static Lang INPUT_SPEED;

    @CN("§n机械等级§r由§l机械升级仓§r中的物品决定：无(0)，§7基础构件(1)§r，§e精密构件(2)§r，\n§8钢铁构建/基础电子电路(3)§r，§b优质电子电路(4)§r，§6进阶集成电路(5)§r")
    @EN("§nMechanical Tier§r is determined by items in the Mechanical Upgrade Bus: None (0), §7Basic Components (1)§r, §ePrecision Components (2)§r,\n§8Steel Structures or Basic Electronic Circuits (3)§r, §bAdvanced Electronic Circuits (4)§r, §6Integrated Circuits (5)§r")
    public static Lang MECHANICAL_TIER;

    @CN("§n机械等级§r请详见§l机械升级仓§r的物品信息")
    @EN("§nMechanical Tier§r please be seen in the Tooltip of §lMechanical upgrade bus§r")
    public static Lang MECHANICAL_TIER_MACHINE;
}
