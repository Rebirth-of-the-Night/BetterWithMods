package betterwithmods.module.compat.jei;

import betterwithmods.client.container.anvil.ContainerSteelAnvil;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.inventory.Slot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AnvilCraftingTransfer implements IRecipeTransferInfo<ContainerSteelAnvil> {

    @Override
    public Class<ContainerSteelAnvil> getContainerClass() {
        return ContainerSteelAnvil.class;
    }

    @Override
    public String getRecipeCategoryUid() {
        return VanillaRecipeCategoryUid.CRAFTING;
    }

    @Override
    public boolean canHandle(ContainerSteelAnvil container) {
        return true;
    }

    @Override
    public List<Slot> getRecipeSlots(ContainerSteelAnvil container) {
        int[] indicies = new int[]{1,2,3,5,6,7,9,10,11};
        return Arrays.stream(indicies).mapToObj(container::getSlot).collect(Collectors.toList());
    }


    @Override
    public List<Slot> getInventorySlots(ContainerSteelAnvil container) {
        List<Slot> slots = new ArrayList<>();
        for (int i = 17; i < 53; i++) {
            Slot slot = container.getSlot(i);
            slots.add(slot);
        }
        return slots;
    }
}
