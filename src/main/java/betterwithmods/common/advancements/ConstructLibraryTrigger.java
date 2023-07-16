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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConstructLibraryTrigger implements ICriterionTrigger<ConstructLibraryTrigger.Instance> {

    private static final ResourceLocation ID = new ResourceLocation(BWMod.MODID, "construct_library");

    private final Map<PlayerAdvancements, ConstructLibraryTrigger.Listeners> listeners = Maps.newHashMap();

    @Nonnull
    public ResourceLocation getId() {
        return ID;
    }

    public void addListener(@Nonnull PlayerAdvancements playerAdvancementsIn, @Nonnull Listener<ConstructLibraryTrigger.Instance> listener) {
        ConstructLibraryTrigger.Listeners listeners = this.listeners.get(playerAdvancementsIn);

        if (listeners == null) {
            listeners = new ConstructLibraryTrigger.Listeners(playerAdvancementsIn);
            this.listeners.put(playerAdvancementsIn, listeners);
        }
        listeners.add(listener);
    }

    public void removeListener(@Nonnull PlayerAdvancements playerAdvancementsIn, @Nonnull Listener<ConstructLibraryTrigger.Instance> listener) {
        ConstructLibraryTrigger.Listeners listeners = this.listeners.get(playerAdvancementsIn);

        if (listeners != null) {
            listeners.remove(listener);

            if (listeners.isEmpty()) {
                this.listeners.remove(playerAdvancementsIn);
            }
        }
    }

    public void removeAllListeners(@Nonnull PlayerAdvancements playerAdvancementsIn) {
        this.listeners.remove(playerAdvancementsIn);
    }


    @Nonnull
    public ConstructLibraryTrigger.Instance deserializeInstance(@Nonnull JsonObject json, @Nonnull JsonDeserializationContext context) {
        int bookcases = json.get("count").getAsInt();
        return new ConstructLibraryTrigger.Instance(bookcases);
    }


    public void trigger(EntityPlayerMP player, int bookcases) {
        ConstructLibraryTrigger.Listeners listeners = this.listeners.get(player.getAdvancements());
        if (listeners != null) {
            listeners.trigger(bookcases);
        }
    }


    public static class Instance extends AbstractCriterionInstance {
        private final int bookcases;

        public Instance(int bookcases) {
            super(ConstructLibraryTrigger.ID);
            this.bookcases = bookcases;
        }

        public boolean test(int bookcases) {
            return bookcases >= this.bookcases;
        }
    }


    static class Listeners {
        private final PlayerAdvancements playerAdvancements;
        private final Set<Listener<ConstructLibraryTrigger.Instance>> listeners = Sets.newHashSet();

        public Listeners(PlayerAdvancements playerAdvancementsIn) {
            this.playerAdvancements = playerAdvancementsIn;
        }

        public boolean isEmpty() {
            return this.listeners.isEmpty();
        }

        public void add(Listener<ConstructLibraryTrigger.Instance> listener) {
            this.listeners.add(listener);
        }

        public void remove(Listener<ConstructLibraryTrigger.Instance> listener) {
            this.listeners.remove(listener);
        }

        public void trigger(int bookcase) {
            List<Listener<ConstructLibraryTrigger.Instance>> list = null;
            for (Listener<ConstructLibraryTrigger.Instance> listener : this.listeners) {
                if (listener.getCriterionInstance().test(bookcase)) {
                    if (list == null) {
                        list = Lists.newArrayList();
                    }
                    list.add(listener);
                }
            }

            if (list != null) {
                for (Listener<ConstructLibraryTrigger.Instance> listener1 : list) {
                    listener1.grantCriterion(this.playerAdvancements);
                }
            }
        }
    }
}
