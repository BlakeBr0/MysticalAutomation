package com.blakebr0.mysticalautomation.compat.jei.transfer;

import com.blakebr0.mysticalagriculture.api.crafting.IEnchanterRecipe;
import com.blakebr0.mysticalautomation.compat.jei.JeiCompat;
import com.blakebr0.mysticalautomation.container.EnchanternatorContainer;
import com.blakebr0.mysticalautomation.container.slot.FakeSlot;
import com.blakebr0.mysticalautomation.init.ModMenuTypes;
import com.blakebr0.mysticalautomation.network.payload.SetFakeRecipeSlotPayload;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Optional;

public class EnchanternatorRecipeTransferHandler implements IRecipeTransferHandler<EnchanternatorContainer, IEnchanterRecipe> {
    @Override
    public Class<? extends EnchanternatorContainer> getContainerClass() {
        return EnchanternatorContainer.class;
    }

    @Override
    public Optional<MenuType<EnchanternatorContainer>> getMenuType() {
        return Optional.of(ModMenuTypes.ENCHANTERNATOR.get());
    }

    @Override
    public RecipeType<IEnchanterRecipe> getRecipeType() {
        return JeiCompat.ENCHANTER_RECIPE_TYPE;
    }

    @Override
    public IRecipeTransferError transferRecipe(EnchanternatorContainer container, IEnchanterRecipe recipe, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
        if (doTransfer) {
            var views = recipeSlots.getSlotViews(RecipeIngredientRole.INPUT);
            var index = 0;

            for (var slot : container.slots) {
                if (index > views.size())
                    return null;

                if (slot instanceof FakeSlot) {
                    var stack = views.get(index).getDisplayedItemStack().orElse(ItemStack.EMPTY);

                    PacketDistributor.sendToServer(new SetFakeRecipeSlotPayload(slot.index, stack));

                    index++;
                }
            }
        }

        return null;
    }
}
