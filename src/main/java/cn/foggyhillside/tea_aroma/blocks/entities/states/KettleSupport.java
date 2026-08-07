package cn.foggyhillside.tea_aroma.blocks.entities.states;

import net.minecraft.util.StringRepresentable;

public enum KettleSupport implements StringRepresentable {
    NONE("none"),
    TRAY("tray"),
    HANDLE("handle");

    private final String supportName;

    KettleSupport(String name) {
        this.supportName = name;
    }

    public String toString() {
        return this.getSerializedName();
    }

    public String getSerializedName() {
        return this.supportName;
    }
}