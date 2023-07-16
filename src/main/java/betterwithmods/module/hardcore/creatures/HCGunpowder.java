package betterwithmods.module.hardcore.creatures;

import betterwithmods.common.items.ItemMaterial;
import betterwithmods.module.Feature;
import betterwithmods.util.EntityUtils;
import com.google.common.collect.Lists;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

/**
 * Created by primetoxinz on 4/20/17.
 */
public class HCGunpowder extends Feature {
    public static List<Class<? extends Entity>> disableGunpowder = Lists.newArrayList();

    @Override
    public void setupConfig() {

        String[] array = loadPropStringList("Disable Gunpowder Drop", "List of entity classes which gunpowder will be replaced with niter", new String[]{
                "net.minecraft.entity.monster.EntityCreeper",
                "net.minecraft.entity.monster.EntityGhast",
                "net.minecraft.entity.monster.EntityWitch",
                "betterwithmods.common.entity.EntityShearedCreeper"
        });
        disableGunpowder = EntityUtils.loadEntitiesFromStrings(array);
    }

    @Override
    public String getFeatureDescription() {
        return "Makes a raw resource drop that must be crafted to make useful gunpowder";
    }

    @SubscribeEvent
    public void mobDrops(LivingDropsEvent evt) {
        boolean contained = false;
        for(Class<? extends Entity> clazz: disableGunpowder) {
            if (evt.getEntity().getClass().isAssignableFrom(clazz)) {
                contained = true;
                break;
            }
        }
        if (contained) {
            for (EntityItem item : evt.getDrops()) {
                ItemStack stack = item.getItem();
                if (stack.getItem() == Items.GUNPOWDER) {
                    item.setItem(ItemMaterial.getMaterial(ItemMaterial.EnumMaterial.NITER, stack.getCount()));
                }
            }
        }
    }

    @Override
    public boolean hasSubscriptions() {
        return true;
    }
}
