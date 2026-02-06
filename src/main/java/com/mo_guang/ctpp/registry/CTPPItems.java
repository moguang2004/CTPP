package com.mo_guang.ctpp.registry;

import com.mo_guang.ctpp.dynamicPart.rotation.SimpleRotatingContraptionEntity;
import com.mo_guang.ctpp.dynamicPart.RotationWandItem;
import com.simibubi.create.content.contraptions.render.ContraptionEntityRenderer;
import com.tterrag.registrate.util.entry.EntityEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;

import static com.mo_guang.ctpp.CTPPRegistration.REGISTRATE;

public class CTPPItems {
    public static void init() {}
    public static ItemEntry<Item> BASIC_MECHANISM = REGISTRATE.item("basic_mechanism", Item::new).cnlang("基础构件").register();
    public static ItemEntry<Item> INCOMPLETE_BASIC_MECHANISM = REGISTRATE.item("incomplete_basic_mechanism", Item::new).cnlang("未完成的基础构件").register();
    public static ItemEntry<Item> STEEL_MECHANISM = REGISTRATE.item("steel_mechanism", Item::new).cnlang("钢铁构件").register();
//    public static ItemEntry<RotationWandItem> ROTATION_WAND = REGISTRATE.item("rotation_wand", p -> new RotationWandItem(p)).register();
    public static final EntityEntry<SimpleRotatingContraptionEntity> ROTATING_CONTRAPTION =
            REGISTRATE.movingEntity("rotating_contraption",
                            (EntityType.EntityFactory<SimpleRotatingContraptionEntity>) SimpleRotatingContraptionEntity::new, MobCategory.MISC)
                    .properties(builder -> builder
                            .sized(1.0f, 1.0f) // 实体大小
                            .setTrackingRange(256)
                            .setUpdateInterval(1)
                            .fireImmune() // 可选
                    )
                    .renderer(() -> ContraptionEntityRenderer<SimpleRotatingContraptionEntity>::new)
                    .register();

}
