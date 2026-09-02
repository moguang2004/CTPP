package com.mo_guang.ctpp.util;

import com.ctnhlang.CN;
import com.ctnhlang.EN;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;

public class CommonTooltips {

    @CN("-输入配方所需的(N的平方)倍应力时，获得N并行")
    @EN("- Input (N squared) times stress required by the recipe to get N parallels")
    public static Lang KINETIC_OVERCLOCK;

    @CN("*所有输入应力的应力输入箱必须输入相同转速")
    @EN("*All the input speed should be the same.")
    public static Lang INPUT_SPEED;

    @CN("§n机械等级§r由多方块内安装的§l机械升级仓§r等级决定：无(0)，LV机械升级仓(1)，MV机械升级仓(2)，HV机械升级仓(3)§r")
    @EN("§nMechanical Tier§r is determined by the tier of the §lMechanical Upgrade Bus§r installed in the multiblock: None (0), LV Mechanical Upgrade Bus (1), MV Mechanical Upgrade Bus (2), HV Mechanical Upgrade Bus (3)§r")
    public static Lang MECHANICAL_TIER_MACHINE;
}
