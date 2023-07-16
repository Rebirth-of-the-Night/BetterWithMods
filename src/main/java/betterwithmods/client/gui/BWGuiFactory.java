package betterwithmods.client.gui;

import betterwithmods.BWMod;
import betterwithmods.module.ModuleLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by primetoxinz on 9/14/16.
 */
public class BWGuiFactory implements IModGuiFactory {
    @Override
    public void initialize(Minecraft minecraftInstance) {
        // NO-OP
    }

    @Override
    public boolean hasConfigGui() {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        return new BWGuiConfig(parentScreen);
    }


    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }


    public static class BWGuiConfig extends GuiConfig {

        public BWGuiConfig(GuiScreen parentScreen) {
            super(parentScreen, getAllElements(), BWMod.MODID, false, false, GuiConfig.getAbridgedConfigPath(ModuleLoader.config.toString()));
        }

        public static List<IConfigElement> getAllElements() {
            List<IConfigElement> list = new ArrayList<>();

            Set<String> categories = ModuleLoader.config.getCategoryNames();
            for(String s : categories)
                if(!s.contains("."))
                    list.add(new DummyConfigElement.DummyCategoryElement(s, s, new ConfigElement(ModuleLoader.config.getCategory(s)).getChildElements()));

            return list;
        }

    }

}
