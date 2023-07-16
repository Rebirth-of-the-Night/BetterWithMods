package betterwithmods.common.blocks;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

/**
 * Created by primetoxinz on 7/21/17.
 */
public enum EnumTier implements IStringSerializable {
    WOOD("wood"),
    STEEL("steel");

    public static EnumTier[] VALUES = values();
    public static final PropertyEnum<EnumTier> TIER = PropertyEnum.create("tier", EnumTier.class);
    private String name;


    EnumTier(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}