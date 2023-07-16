package betterwithmods.module.tweaks;

import betterwithmods.BWMod;
import betterwithmods.module.Feature;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LlamaDrops extends Feature {

    public static final ResourceLocation LLAMA_LOOT = new ResourceLocation(BWMod.MODID, "entity/llama");
    //Override loottables
    @SubscribeEvent
    public void onLootTableLoad(LootTableLoadEvent event) {
        if (event.getName().equals(LootTableList.ENTITIES_LLAMA)) {
            LootTable table = event.getLootTableManager().getLootTableFromLocation(LLAMA_LOOT);
            event.setTable(table);
        }
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }

    @Override
    public String getFeatureDescription() {
        return "Add mutton to Llama drops";
    }
}
