package betterwithmods.common;

import betterwithmods.BWMod;
import betterwithmods.common.registry.KilnStructureManager;
import betterwithmods.module.hardcore.world.HCBonemeal;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.function.Consumer;

@SuppressWarnings("deprecation")
public class BWIMCHandler {

    private static final HashMap<String, Consumer<Object>> IMC_HANDLER = new HashMap<>();

    public static void registerIMC(String key, Consumer<Object> function) {
        IMC_HANDLER.put(key, function);
    }

    static {
        registerIMC("registerKilnBlock", object -> {
            ItemStack stack = (ItemStack) object;
            if(stack.getItem() instanceof ItemBlock) {
                Block block = ((ItemBlock) stack.getItem()).getBlock();
                IBlockState state = block.getStateFromMeta(stack.getMetadata());
                KilnStructureManager.registerKilnBlock(state);
            }
        });

        registerIMC("registerFertilizer", object -> {
            ItemStack stack = (ItemStack) object;
            HCBonemeal.registerFertilzier(stack);
        });
    }

    public static void processIMC(ImmutableList<IMCMessage> message) {
        Logger log = BWMod.logger;
        for (IMCMessage m : message) {
            try {
                String k = m.key;
                log.debug("[BWIMCHandler] %s from %s", k, m.getSender());
                if (IMC_HANDLER.containsKey(k)) {
                    IMC_HANDLER.get(k).accept(ObfuscationReflectionHelper.getPrivateValue(IMCMessage.class, m, "value"));
                }
            } catch (Throwable t) {
                bigWarning(log, Level.ERROR, "Bad IMC message (%s)\nfrom %s", m.key, m.getSender());
                log.catching(t);
            }
        }
    }

    public static void bigWarning(Logger log, Level level, String format, Object... data) {
        String o = String.format(format, data);
        String err = "************************";
        err += err;
        log.log(level, err);
        log.log(level, err);
        for (String str : o.split("\n", 0))
            log.log(level, str);
        log.log(level, err);
        log.log(level, err);
    }
}
