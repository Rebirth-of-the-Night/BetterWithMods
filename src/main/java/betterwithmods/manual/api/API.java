package betterwithmods.manual.api;


import betterwithmods.BWMod;
import betterwithmods.manual.api.detail.ManualDefinition;

/**
 * Glue / actual references for the RTFM API.
 */
public final class API {
    /**
     * The ID of the mod, i.e. the internal string it is identified by.
     */
    public static final String MOD_ID = BWMod.MODID;

    public static final String MOD_VERSION = BWMod.VERSION;

    // --------------------------------------------------------------------- //

    // The default manual book. Set in RTFM pre-init. You should generally not modify this.
    public static ManualDefinition manualAPI;


    private API() {
    }
}
