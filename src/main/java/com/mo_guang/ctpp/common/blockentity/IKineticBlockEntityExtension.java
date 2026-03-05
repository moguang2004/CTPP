package com.mo_guang.ctpp.common.blockentity;

public interface IKineticBlockEntityExtension {

    boolean isCTNHInMultiblock();

    // 设置是否在多方块中的状态
    void setCTNHInMultiblock(boolean inMultiblock);
}
