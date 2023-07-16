package betterwithmods.util;

import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InvUtils {

    public static boolean containsIngredient(List<Ingredient> collection, List<Ingredient> ingredient) {
        return matchesPredicate(collection, ingredient, (a, b) -> {
            if (a.getMatchingStacks().length > 0)
                return Arrays.stream(a.getMatchingStacks()).allMatch(b::apply);
            return false;
        });
    }

    public static boolean isIngredientValid(Ingredient ingredient) {
        return !ArrayUtils.isEmpty(ingredient.getMatchingStacks());
    }

    public static boolean containsIngredient(ItemStackHandler handler, Ingredient ingredient) {
        return InventoryIterator.stream(handler).anyMatch(ingredient::apply);
    }

    public static boolean isFluidContainer(ItemStack stack) {
        return !stack.isEmpty() && (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) || stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null));
    }

    @SafeVarargs
    public static <T> NonNullList<T> asNonnullList(T... array) {
        NonNullList<T> nonNullList = NonNullList.create();
        if (array != null)
            nonNullList.addAll(Arrays.stream(array).filter(Objects::nonNull).collect(Collectors.toList()));
        return nonNullList;
    }

    public static <T> NonNullList<T> asNonnullList(List<T> list) {
        NonNullList<T> nonNullList = NonNullList.create();
        if (list != null)
            nonNullList.addAll(list.stream().filter(Objects::nonNull).collect(Collectors.toList()));
        return nonNullList;
    }

    public static IItemHandlerModifiable getPlayerInventory(EntityPlayer player, EnumFacing inv) {
        return (IItemHandlerModifiable) player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, inv);
    }

    public static void givePlayer(EntityPlayer player, EnumFacing inv, NonNullList<ItemStack> stacks) {
        IItemHandlerModifiable inventory = (IItemHandlerModifiable) player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, inv);
        if (inventory != null) {
            insert(player.world, player.getPosition(), inventory, stacks, false);
        }
    }

    public static boolean usePlayerItemStrict(EntityPlayer player, EnumFacing inv, Ingredient ingredient, int amount) {
        IItemHandlerModifiable inventory = (IItemHandlerModifiable) player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, inv);
        return inventory != null && consumeItemsInInventoryStrict(inventory, ingredient, amount, false);
    }

    public static boolean usePlayerItem(EntityPlayer player, EnumFacing inv, ItemStack stack, int amount) {
        IItemHandlerModifiable inventory = (IItemHandlerModifiable) player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, inv);
        return inventory != null && consumeItemsInInventory(inventory, stack, amount, false);
    }

    public static boolean usePlayerItem(EntityPlayer player, EnumFacing inv, Ingredient ingredient) {
        IItemHandlerModifiable inventory = (IItemHandlerModifiable) player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, inv);
        boolean result = false;

        if (inventory != null) {
            NonNullList<ItemStack> containers = NonNullList.create();
            result = consumeItemsInInventory(inventory, ingredient, false, containers);
            givePlayer(player, inv, containers);
        }
        return result;
    }

    public static Optional<IItemHandler> getItemHandler(World world, BlockPos pos, EnumFacing facing) {
        return getItemHandler(world, pos, facing, te -> true);
    }

    public static Optional<IItemHandler> getItemHandler(World world, BlockPos pos, EnumFacing facing, Predicate<TileEntity> tePredicate) {
        if (!world.isRemote) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile != null && tePredicate.test(tile)) {
                return Optional.ofNullable(tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing));
            } else {
                List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1), entity -> entity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing));
                Optional<Entity> entity = entities.stream().findFirst();
                if (entity.isPresent()) {
                    return Optional.ofNullable(entity.get().getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing));
                }
            }
        }
        return Optional.ofNullable(null);
    }

    public static void ejectInventoryContents(World world, BlockPos pos, IItemHandler inv) {
        for (int i = 0; i < inv.getSlots(); ++i) {
            ejectStackWithOffset(world, pos, inv.getStackInSlot(i));
        }
    }

    public static void clearInventory(IItemHandlerModifiable inv) {
        for (int i = 0; i < inv.getSlots(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                inv.setStackInSlot(i, ItemStack.EMPTY);
            }
        }
    }

    public static void copyTags(ItemStack destStack, ItemStack sourceStack) {
        if (sourceStack.hasTagCompound()) {
            destStack.setTagCompound(sourceStack.getTagCompound().copy());
        }

    }

    public static ItemStack decrStackSize(IItemHandlerModifiable inv, int slot, int amount) {
        if (!inv.getStackInSlot(slot).isEmpty()) {
            ItemStack splitStack;
            if (inv.getStackInSlot(slot).getCount() <= amount) {
                splitStack = inv.getStackInSlot(slot);
                inv.setStackInSlot(slot, ItemStack.EMPTY);
                return splitStack;
            } else {
                splitStack = inv.getStackInSlot(slot).splitStack(amount);
                if (inv.getStackInSlot(slot).getCount() < 1) {
                    inv.setStackInSlot(slot, ItemStack.EMPTY);
                }
                return splitStack;
            }
        } else {
            return null;
        }
    }

    public static ItemStack insertSingle(IItemHandler inv, ItemStack stack, boolean simulate) {
        return insert(inv, stack, 1, simulate);
    }

    public static ItemStack insert(IItemHandler inv, ItemStack stack, int count, boolean simulate) {
        ItemStack copy = stack.copy();
        if (copy.getCount() > count) {
            copy.setCount(count);
        }
        return insert(inv, copy, simulate);
    }

    public static boolean canInsert(IItemHandler inv, ItemStack stack, int count) {
        ItemStack copy = stack.copy();
        if (copy.getCount() > count) {
            copy.setCount(count);
        }
        ItemStack inserted = insert(inv, copy, true);
        if (inserted.equals(copy)) {
            return false;
        }
        return true;
    }


    public static void insert(World world, BlockPos pos, IItemHandler inv, NonNullList<ItemStack> stacks, boolean simulate) {
        stacks.forEach(stack -> {


            ItemStack returned = insert(inv, stack, 0, inv.getSlots(), simulate);
            if (!returned.isEmpty()) {
                EJECT_OFFSET.setStack(returned).ejectStack(world, new Vec3d(pos), Vec3d.ZERO);
            }
        });
    }

    public static ItemStack insert(IItemHandler inv, ItemStack stack, boolean simulate) {
        return insert(inv, stack, 0, inv.getSlots(), simulate);
    }

    public static ItemStack insert(IItemHandler inv, ItemStack stack, int minSlot, int maxSlot, boolean simulate) {

        return attemptInsert(inv, stack, minSlot, maxSlot, simulate);
    }

    public static ItemStack attemptInsert(IItemHandler inv, ItemStack stack, int minSlot, int maxSlot, boolean simulate) {
        if (isFull(inv)) {
            return stack;
        }
        for (int slot = minSlot; slot < maxSlot; slot++) {
            stack = inv.insertItem(slot, stack, simulate);
            if (stack.isEmpty())
                break;
        }
        return stack;
    }

    public static boolean insertFromWorld(IItemHandler inv, EntityItem entity, int minSlot, int maxSlot, boolean simulate) {
        ItemStack stack = entity.getItem().copy();
        ItemStack leftover = attemptInsert(inv, stack, minSlot, maxSlot, simulate);
        if (leftover.isEmpty()) {
            entity.setDead();
            return true;
        } else {
            entity.setItem(leftover);
            return false;
        }
    }

    public static boolean isFull(IItemHandler inv) {
        boolean full = true;
        for (int slot = 0; slot < inv.getSlots(); slot++) {
            ItemStack stack = inv.getStackInSlot(slot);
            if (stack.getCount() != inv.getSlotLimit(slot)) {
                full = false;
                break;
            }
        }
        return full;
    }

    public static ItemStack findItemInInventory(Ingredient ingredient, IItemHandler inventory) {
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (ingredient.apply(stack)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    public static int getRandomOccupiedStackInRange(IItemHandler inv, int minSlot, int maxSlot) {
        List<Integer> list = Lists.newArrayList();
        for (int slot = minSlot; slot <= maxSlot; ++slot) {
            if (!inv.getStackInSlot(slot).isEmpty()) {
                list.add(slot);
            }
        }

        if (list.isEmpty())
            return -1;
        return list.get(RandomUtils.nextInt(0, list.size()));
    }

    public static int getFirstOccupiedStackInRange(IItemHandler inv, int minSlot, int maxSlot) {
        for (int slot = minSlot; slot <= maxSlot; ++slot) {
            if (!inv.getStackInSlot(slot).isEmpty()) {
                return slot;
            }
        }
        return -1;
    }

    public static int getFirstEmptyStackInRange(IItemHandler inv, int minSlot, int maxSlot) {
        for (int slot = minSlot; slot <= maxSlot; ++slot) {
            if (inv.getStackInSlot(slot).isEmpty()) {
                return slot;
            }
        }

        return -1;
    }

    public static int getOccupiedStacks(IItemHandler inv) {
        return getOccupiedStacks(inv, 0, inv.getSlots() - 1);
    }

    public static int getOccupiedStacks(IItemHandler inv, int min, int max) {
        int count = 0;

        for (int i = min; i <= max; ++i) {
            if (inv.getStackInSlot(i).isEmpty()) {
                ++count;
            }
        }

        return count;
    }

    public static int countItemStacksInInventory(IItemHandler inv, ItemStack toCheck) {
        int itemCount = 0;
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (ItemStack.areItemsEqual(toCheck, stack) ||
                        (toCheck.getItem() == stack.getItem() && toCheck.getItemDamage() == OreDictionary.WILDCARD_VALUE)) {
                    if (toCheck.hasTagCompound()) {
                        if (ItemStack.areItemStackTagsEqual(toCheck, stack))
                            itemCount += stack.getCount();
                    } else
                        itemCount += stack.getCount();
                }
            }
        }
        return itemCount;
    }

    public static int countItemsInInventory(IItemHandler inv, Item item) {
        return countItemsInInventory(inv, item, OreDictionary.WILDCARD_VALUE);
    }

    public static int countItemsInInventory(IItemHandler inv, Item item, int meta) {
        int itemCount = 0;
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (stack.getItem() == item) {
                    if ((meta == OreDictionary.WILDCARD_VALUE) || (stack.getItemDamage() == meta)) {
                        itemCount += inv.getStackInSlot(i).getCount();
                    }
                }
            }
        }
        return itemCount;
    }

    public static int countOresInInventory(IItemHandler inv, List<ItemStack> list) {
        int ret = 0;
        if (list != null && !list.isEmpty()) {
            for (ItemStack oreStack : list) {
                ret += countItemStacksInInventory(inv, oreStack);
            }
        }
        return ret;
    }

    public static boolean consumeItemsInInventoryStrict(IItemHandlerModifiable inv, Ingredient ingredient, int sizeOfStack, boolean simulate) {
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) {
                if (ingredient.apply(stack)) {
                    if (stack.getCount() >= sizeOfStack) {
                        decrStackSize(inv, i, sizeOfStack);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean consumeItemsInInventory(IItemHandler inv, ItemStack toCheck, int sizeOfStack, boolean simulate) {
        boolean extracted = false;
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack inSlot = inv.getStackInSlot(i);
            if (InvUtils.matches(toCheck, inSlot)) {
                ItemStack container = ForgeHooks.getContainerItem(inSlot);
                if (inv.extractItem(i, sizeOfStack, simulate).getCount() >= sizeOfStack) {
                    extracted = true;
                    InvUtils.insert(inv, container, false);
                    break;
                }
            }
        }
        return extracted;
    }

    public static boolean consumeItemsInInventory(IItemHandler inv, Ingredient ingredient, boolean simulate, NonNullList<ItemStack> containers) {
        int count;
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack inSlot = inv.getStackInSlot(i);
            if (ingredient.apply(inSlot)) {
                ItemStack container = ForgeHooks.getContainerItem(inSlot);
                if (!container.isEmpty())
                    containers.add(container);
                if (ingredient instanceof StackIngredient)
                    count = ((StackIngredient) ingredient).getCount(inSlot);
                else
                    count = 1;
                return inv.extractItem(i, count, simulate).getCount() >= count;
            }
        }
        return false;
    }

    public static boolean consumeItemsInInventory(IItemHandler inv, StackIngredient ingredient, boolean simulate, NonNullList<ItemStack> containers) {
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack inSlot = inv.getStackInSlot(i);
            if (ingredient.apply(inSlot)) {
                int sizeOfStack = ingredient.getCount(inSlot);
                ItemStack container = ForgeHooks.getContainerItem(inSlot);
                if (!container.isEmpty())
                    containers.add(container);
                return inv.extractItem(i, sizeOfStack, simulate).getCount() >= sizeOfStack;
            }
        }
        return false;
    }

    public static boolean consumeItemsInInventory(IItemHandlerModifiable inv, Item item, int meta, int stackSize) {
        for (int i = 0; i < inv.getSlots(); ++i) {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem() == item && (meta == OreDictionary.WILDCARD_VALUE || stack.getItemDamage() == meta)) {
                if (stack.getCount() >= stackSize) {
                    decrStackSize(inv, i, stackSize);
                    return false;
                }

                stackSize -= stack.getCount();
                inv.setStackInSlot(i, ItemStack.EMPTY);
            }
        }
        return false;
    }

    public static boolean consumeOresInInventory(IItemHandlerModifiable inv, List<?> list, int stackSize) {
        if (list.size() > 0) {
            for (Object aList : list) {
                ItemStack tempStack = (ItemStack) aList;
                Item item = tempStack.getItem();
                int meta = tempStack.getItemDamage();

                for (int j = 0; j < inv.getSlots(); ++j) {
                    ItemStack stack = inv.getStackInSlot(j);
                    if (!stack.isEmpty() && stack.getItem() == item && (stack.getItemDamage() == meta || meta == OreDictionary.WILDCARD_VALUE)) {
                        if (tempStack.hasTagCompound()) {
                            if (ItemStack.areItemStackTagsEqual(tempStack, stack)) {
                                if (stack.getCount() >= stackSize) {
                                    decrStackSize(inv, j, stackSize);
                                    return false;
                                }

                                stackSize -= stack.getCount();
                                inv.setStackInSlot(j, ItemStack.EMPTY);
                            }
                        } else {
                            if (stack.getCount() >= stackSize) {
                                decrStackSize(inv, j, stackSize);
                                return false;
                            }

                            stackSize -= stack.getCount();
                            inv.setStackInSlot(j, ItemStack.EMPTY);
                        }
                    }
                }
            }
        }

        return false;
    }

    public static int getFirstOccupiedStackNotOfItem(IItemHandler inv, Item item) {
        return getFirstOccupiedStackNotOfItem(inv, item, OreDictionary.WILDCARD_VALUE);
    }

    public static int getFirstOccupiedStackNotOfItem(IItemHandler inv, Item item, int meta) {
        for (int i = 0; i < inv.getSlots(); ++i) {
            if (!inv.getStackInSlot(i).isEmpty()) {
                int tempMeta = inv.getStackInSlot(i).getItemDamage();
                if (inv.getStackInSlot(i).getItem() != item && (meta == OreDictionary.WILDCARD_VALUE || tempMeta != meta)) {
                    return i;
                }
            }
        }

        return -1;
    }

    public static int getFirstOccupiedStackOfItem(IItemHandler inv, Item item) {
        return getFirstOccupiedStackOfItem(inv, item);
    }

    public static int getFirstOccupiedStackOfItem(IItemHandler inv, ItemStack stack) {
        return getFirstOccupiedStackOfItem(inv, StackIngredient.fromStacks(stack));
    }

    public static int getFirstOccupiedStackOfItem(IItemHandler inv, Ingredient ingred) {
        for (int i = 0; i < inv.getSlots(); ++i) {
            if (!inv.getStackInSlot(i).isEmpty()) {
                if (ingred.apply(inv.getStackInSlot(i))) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static boolean spawnStack(World world, BlockPos pos, List<ItemStack> stacks) {
        boolean spawned = true;
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty())
                if (!spawnStack(world, pos, stack, 10))
                    spawned = false;
        }
        return spawned;
    }

    public static boolean spawnStack(World world, BlockPos pos, ItemStack stack, int pickupDelay) {
        return spawnStack(world, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, stack, pickupDelay);
    }


    public static boolean spawnStack(World world, double x, double y, double z, ItemStack stack, int pickupDelay) {
        EntityItem item = new EntityItem(world, x, y, z, stack);
        item.motionX = 0;
        item.motionY = 0;
        item.motionZ = 0;
        item.setPickupDelay(pickupDelay);
        return world.spawnEntity(item);
    }

    public static void spawnStack(World world, double x, double y, double z, int count, ItemStack stack) {
        ItemStack copy = stack.copy();
        if (copy.getCount() > count)
            copy.setCount(count);
        spawnStack(world, x, y, z, copy, 10);
    }

    public static void ejectStackWithOffset(World world, BlockPos pos, List<ItemStack> stacks) {
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty())
                ejectStackWithOffset(world, pos, stack.copy());
        }
    }

    public static void ejectStackWithOffset(World world, BlockPos pos, ItemStack... stacks) {
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty())
                ejectStackWithOffset(world, pos, stack.copy());
        }
    }


    public static StackEjector EJECT_OFFSET = new StackEjector(new VectorBuilder().rand(0.5f).offset(0.25f), new VectorBuilder().setGaussian(0.05f)),
            EJECT_EXACT = new StackEjector(new VectorBuilder(), new VectorBuilder());

    public static void ejectStackWithOffset(World world, BlockPos pos, ItemStack stack) {
        if (stack.isEmpty())
            return;
        EJECT_OFFSET.setStack(stack).ejectStack(world, new Vec3d(pos), Vec3d.ZERO);
    }


    public static void ejectStack(@Nonnull World world, double x, double y, double z, ItemStack stack, int pickupDelay) {
        if (world.isRemote)
            return;
        EJECT_EXACT.setStack(stack).setPickupDelay(pickupDelay).ejectStack(world, new Vec3d(x, y, z), Vec3d.ZERO);
    }

    public static void ejectStack(@Nonnull World world, double x, double y, double z, ItemStack stack) {
        ejectStack(world, x, y, z, stack, 10);
    }


    public static void ejectBrokenItems(World world, BlockPos pos, ResourceLocation lootLocation) {
        if (!world.isRemote) {
            LootContext.Builder build = new LootContext.Builder((WorldServer) world);
            List<ItemStack> stacks = world.getLootTableManager().getLootTableFromLocation(lootLocation).generateLootForPools(world.rand, build.build());
            if (!stacks.isEmpty()) {
                ejectStackWithOffset(world, pos, stacks);
            }
        }
    }

    public static void writeToStack(IItemHandler inv, ItemStack stack) {
        NonNullList<ItemStack> list = NonNullList.withSize(inv.getSlots(), ItemStack.EMPTY);
        for (int i = 0; i < inv.getSlots(); i++) {
            if (!inv.getStackInSlot(i).isEmpty()) {
                list.set(i, inv.getStackInSlot(i).copy());
            }
        }
        NBTTagCompound tag = ItemStackHelper.saveAllItems(new NBTTagCompound(), list);

        if (!tag.isEmpty())
            stack.setTagCompound(tag);
    }

    public static void readFromStack(IItemHandler inv, ItemStack stack) {
        if (!stack.isEmpty() && stack.hasTagCompound()) {
            NonNullList<ItemStack> list = NonNullList.withSize(inv.getSlots(), ItemStack.EMPTY);
            NBTTagCompound tag = stack.getTagCompound();
            if (tag != null) {
                ItemStackHelper.loadAllItems(tag, list);
                for (int i = 0; i < inv.getSlots(); i++) {
                    inv.insertItem(i, list.get(i), false);
                }
            }
        }
    }

    public static int calculateComparatorLevel(@Nonnull IItemHandler inventory) {
        int i = 0;
        float f = 0.0F;
        for (int j = 0; j < inventory.getSlots(); ++j) {
            ItemStack itemstack = inventory.getStackInSlot(j);
            if (!itemstack.isEmpty()) {
                f += (float) itemstack.getCount() / (float) itemstack.getMaxStackSize();
                ++i;
            }
        }
        f = f / (float) inventory.getSlots();
        return MathHelper.floor(f * 14.0F) + (i > 0 ? 1 : 0);
    }

    public static ItemStack setCount(ItemStack input, int count) {
        if (input.isEmpty())
            return input;
        ItemStack stack = input.copy();
        stack.setCount(count);
        return stack;
    }


    public static boolean matches(ItemStack one, ItemStack two) {
        if (one.isItemEqual(two))
            return true;
        if (one.getItem() == two.getItem() && (one.getMetadata() == OreDictionary.WILDCARD_VALUE || two.getMetadata() == OreDictionary.WILDCARD_VALUE))
            return true;
        return false;
    }

    public static boolean matchesSize(ItemStack one, ItemStack two) {
        return one.getCount() == two.getCount() && matches(one, two);
    }


    public static <T> boolean matchesPredicate(List<T> oneList, List<T> twoList, BiPredicate<T, T> matches) {
        if (oneList.size() != twoList.size())
            return false; //trivial case
        HashSet<T> alreadyMatched = new HashSet<>();
        for (T one : oneList) {
            Optional<T> found = twoList.stream().filter(two -> !alreadyMatched.contains(two) && matches.test(one, two)).findFirst();
            if (found.isPresent())
                alreadyMatched.add(found.get()); //Don't match twice
            else
                return false; //This T doesn't match, thus the two lists don't match
        }
        return true;
    }


    public static boolean matchesExact(List<ItemStack> oneList, List<ItemStack> twoList) {
        return matchesPredicate(oneList, twoList, InvUtils::matchesSize);
    }

    public static boolean matches(List<ItemStack> oneList, List<ItemStack> twoList) {
        return matchesPredicate(oneList, twoList, InvUtils::matches);
    }

    public static <T> List<List<T>> splitIntoBoxes(List<T> stacks, int boxes) {
        ArrayList<List<T>> splitStacks = new ArrayList<>();
        for (int i = 0; i < boxes; i++) {
            final int finalI = i;
            splitStacks.add(IntStream.range(0, stacks.size()).filter(index -> index % boxes == finalI).mapToObj(stacks::get).collect(Collectors.toList()));
        }
        return splitStacks;
    }

    public static boolean isEmpty(IItemHandler inventory) {
        int inventorySize = inventory.getSlots();
        for (int i = 0; i < inventorySize; i++) {
            if (!inventory.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static EnumHand otherHand(EnumHand hand) {
        switch (hand) {
            default:
            case MAIN_HAND:
                return EnumHand.OFF_HAND;
            case OFF_HAND:
                return EnumHand.MAIN_HAND;
        }
    }
}