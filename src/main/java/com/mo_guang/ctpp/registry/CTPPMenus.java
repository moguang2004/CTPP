package com.mo_guang.ctpp.registry;

import com.mo_guang.ctpp.client.toolbox.CTPPToolboxScreen;
import com.mo_guang.ctpp.common.menu.CTPPToolboxMenu;
import com.tterrag.registrate.util.entry.MenuEntry;

import static com.mo_guang.ctpp.CTPPRegistration.REGISTRATE;

public final class CTPPMenus {

    public static final MenuEntry<CTPPToolboxMenu> TOOLBOX = REGISTRATE
            .menu("toolbox", CTPPToolboxMenu::new, () -> CTPPToolboxScreen::new)
            .register();

    private CTPPMenus() {}

    public static void init() {}
}
