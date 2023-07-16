package betterwithmods.common.penalties.attribute;

import com.google.common.collect.Lists;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

public class PotionTemplate {
    ResourceLocation resourceLocation;
    int duration;
    int amplifier;
    boolean curable;

    public PotionTemplate(ResourceLocation resourceLocation, int duration, int amplifier, boolean curable) {
        this.resourceLocation = resourceLocation;
        this.duration = duration;
        this.amplifier = amplifier;
        this.curable = curable;
    }

    Potion getPotion() {
        return Potion.REGISTRY.getObject(resourceLocation);
    }

    public PotionEffect createEffect() {
        Potion potion = getPotion();
        PotionEffect potionEffect = new PotionEffect(potion, duration, amplifier, false, false);
        if(!curable)
            potionEffect.setCurativeItems(Lists.newArrayList());
        return potionEffect;
    }

    public String serialize() {
        return String.format("%s/%s/%s/%s", resourceLocation.toString(), duration, amplifier, curable);
    }

    public static PotionTemplate deserialize(String string) {
        String[] splitString = string.split("/");
        return new PotionTemplate(new ResourceLocation(splitString[0]), Integer.parseInt(splitString[1]), Integer.parseInt(splitString[2]), Boolean.parseBoolean(splitString[3]));
    }
}
