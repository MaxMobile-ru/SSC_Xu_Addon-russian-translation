package xu_mod.SSCXuAddon.data.item.trinket;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.onixary.shapeShifterCurseFabric.items.accessory.AccessoryUtils;
import net.onixary.shapeShifterCurseFabric.items.accessory.CurioUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import static net.onixary.shapeShifterCurseFabric.items.accessory.AccessoryUtils.calcAutoMod;

public class AccessoryUtil {
    // 忘了留API了 我还是自己实现一套API吧

    public static @Nullable List<ItemStack> findAccessory(LivingEntity livingEntity, String AccessoryMod, Predicate<ItemStack> conditon) {
        List<ItemStack> Result = new ArrayList<>();
        switch (calcAutoMod(AccessoryMod)) {
            case "trinkets":
                if (!AccessoryUtils.LOADED_Trinkets) {
                    return null;
                }
                Optional<TrinketComponent> componentOptional = TrinketsApi.getTrinketComponent(livingEntity);
                if (componentOptional.isEmpty()) {
                    return null;
                }
                TrinketComponent component = componentOptional.get();
                List<Pair<SlotReference, ItemStack>> itemList = component.getAllEquipped();
                for (Pair<SlotReference, ItemStack> pair : itemList) {
                    if (conditon.test(pair.getRight())) {
                        Result.add(pair.getRight());
                    }
                }
                return Result;
            case "curios":
                if (!AccessoryUtils.LOADED_Curios) {
                    return null;
                }
                Map<String, List<ItemStack>> itemStackMap = CurioUtils.getEntitySlots(livingEntity);
                if (itemStackMap == null) {
                    return null;
                }
                for (List<ItemStack> itemStacks : itemStackMap.values()) {
                    for (ItemStack itemStack : itemStacks) {
                        if (conditon.test(itemStack)) {
                            Result.add(itemStack);
                        }
                    }
                }
                return Result;
            default:
                return null;
        }
    }
    public static @Nullable ItemStack getAccessory(LivingEntity livingEntity, String AccessoryMod, String GroupString, String SlotString, int Slot) {
        switch (calcAutoMod(AccessoryMod)) {
            case "trinkets":
                if (!AccessoryUtils.LOADED_Trinkets) {
                    return null;
                }
                Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(livingEntity);
                if (component.isEmpty()) {
                    return null;
                }
                Map<String, TrinketInventory> groupInv = component.get().getInventory().get(GroupString);
                if (groupInv == null) {
                    return null;
                }
                TrinketInventory inv = groupInv.get(SlotString);
                if (inv == null) {
                    return null;
                }
                return inv.getStack(Slot);
            case "curios":
                if (!AccessoryUtils.LOADED_Curios) {
                    return null;
                }
                List<ItemStack> itemStackList = CurioUtils.getEntitySlot(livingEntity, SlotString);
                if (itemStackList == null) {
                    return null;
                }
                if (Slot >= itemStackList.size()) {
                    return null;
                }
                return itemStackList.get(Slot);
            default:
                return null;
        }
    }
}
