package betterwithmods.common.advancements;


import betterwithmods.BWMod;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InfernalEnchantedTrigger implements ICriterionTrigger<InfernalEnchantedTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation(BWMod.MODID, "infernal_enchant");
    private final Map<PlayerAdvancements, InfernalEnchantedTrigger.Listeners> listeners = Maps.newHashMap();

    @Nonnull
    public ResourceLocation getId() {
        return ID;
    }

    public void addListener(@Nonnull PlayerAdvancements playerAdvancementsIn, @Nonnull ICriterionTrigger.Listener<InfernalEnchantedTrigger.Instance> listener) {
        InfernalEnchantedTrigger.Listeners enchanteditemtrigger$listeners = this.listeners.get(playerAdvancementsIn);

        if (enchanteditemtrigger$listeners == null) {
            enchanteditemtrigger$listeners = new InfernalEnchantedTrigger.Listeners(playerAdvancementsIn);
            this.listeners.put(playerAdvancementsIn, enchanteditemtrigger$listeners);
        }

        enchanteditemtrigger$listeners.add(listener);
    }

    public void removeListener(@Nonnull PlayerAdvancements playerAdvancementsIn, @Nonnull ICriterionTrigger.Listener<InfernalEnchantedTrigger.Instance> listener) {
        InfernalEnchantedTrigger.Listeners enchanteditemtrigger$listeners = this.listeners.get(playerAdvancementsIn);

        if (enchanteditemtrigger$listeners != null) {
            enchanteditemtrigger$listeners.remove(listener);

            if (enchanteditemtrigger$listeners.isEmpty()) {
                this.listeners.remove(playerAdvancementsIn);
            }
        }
    }

    public void removeAllListeners(@Nonnull PlayerAdvancements playerAdvancementsIn) {
        this.listeners.remove(playerAdvancementsIn);
    }

    /**
     * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
     */
    @Nonnull
    public InfernalEnchantedTrigger.Instance deserializeInstance(@Nonnull JsonObject json, @Nonnull JsonDeserializationContext context) {
        ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("item"));
        MinMaxBounds minmaxbounds = MinMaxBounds.deserialize(json.get("levels"));
        return new InfernalEnchantedTrigger.Instance(itempredicate, minmaxbounds);
    }

    public void trigger(EntityPlayerMP player, ItemStack item, int levelsSpent) {
        InfernalEnchantedTrigger.Listeners enchanteditemtrigger$listeners = this.listeners.get(player.getAdvancements());

        if (enchanteditemtrigger$listeners != null) {
            enchanteditemtrigger$listeners.trigger(item, levelsSpent);
        }
    }

    public static class Instance extends AbstractCriterionInstance {
        private final ItemPredicate item;
        private final MinMaxBounds levels;

        public Instance(ItemPredicate item, MinMaxBounds levels) {
            super(InfernalEnchantedTrigger.ID);
            this.item = item;
            this.levels = levels;
        }

        public boolean test(ItemStack item, int levelsIn) {
            if (!this.item.test(item)) {
                return false;
            } else {
                return this.levels.test((float) levelsIn);
            }
        }
    }

    static class Listeners {
        private final PlayerAdvancements playerAdvancements;
        private final Set<ICriterionTrigger.Listener<InfernalEnchantedTrigger.Instance>> listeners = Sets.newHashSet();

        public Listeners(PlayerAdvancements playerAdvancementsIn) {
            this.playerAdvancements = playerAdvancementsIn;
        }

        public boolean isEmpty() {
            return this.listeners.isEmpty();
        }

        public void add(ICriterionTrigger.Listener<InfernalEnchantedTrigger.Instance> listener) {
            this.listeners.add(listener);
        }

        public void remove(ICriterionTrigger.Listener<InfernalEnchantedTrigger.Instance> listener) {
            this.listeners.remove(listener);
        }

        public void trigger(ItemStack item, int levelsIn) {
            List<ICriterionTrigger.Listener<InfernalEnchantedTrigger.Instance>> list = null;

            for (ICriterionTrigger.Listener<InfernalEnchantedTrigger.Instance> listener : this.listeners) {
                if (listener.getCriterionInstance().test(item, levelsIn)) {
                    if (list == null) {
                        list = Lists.newArrayList();
                    }

                    list.add(listener);
                }
            }

            if (list != null) {
                for (ICriterionTrigger.Listener<InfernalEnchantedTrigger.Instance> listener1 : list) {
                    listener1.grantCriterion(this.playerAdvancements);
                }
            }
        }
    }
}