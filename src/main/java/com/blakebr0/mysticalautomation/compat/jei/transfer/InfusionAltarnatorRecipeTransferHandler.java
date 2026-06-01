package com.blakebr0.mysticalautomation.compat.jei.transfer;

import com.blakebr0.mysticalagriculture.api.crafting.IInfusionRecipe;
import com.blakebr0.mysticalautomation.compat.jei.JeiCompat;
import com.blakebr0.mysticalautomation.container.InfusionAltarnatorContainer;
import com.blakebr0.mysticalautomation.container.slot.FakeSlot;
import com.blakebr0.mysticalautomation.init.ModMenuTypes;
import com.blakebr0.mysticalautomation.network.payload.SetFakeRecipeSlotPayload;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class InfusionAltarnatorRecipeTransferHandler implements IRecipeTransferHandler<InfusionAltarnatorContainer, RecipeHolder<IInfusionRecipe>> {
    @Override
    public Class<? extends InfusionAltarnatorContainer> getContainerClass() {
        return InfusionAltarnatorContainer.class;
    }

    @Override
    public Optional<MenuType<InfusionAltarnatorContainer>> getMenuType() {
        return Optional.of(ModMenuTypes.INFUSION_ALTARNATOR.get());
    }

    @Override
    public IRecipeType<RecipeHolder<IInfusionRecipe>> getRecipeType() {
        return JeiCompat.INFUSION_ALTAR_RECIPE_TYPE;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(InfusionAltarnatorContainer container, RecipeHolder<IInfusionRecipe> recipe, IRecipeSlotsView recipeSlots, Player player, boolean maxTransfer, boolean doTransfer) {
        if (doTransfer) {
            var views = recipeSlots.getSlotViews(RecipeIngredientRole.INPUT);
            var index = 0;

            for (var slot : container.slots) {
                if (index > views.size())
                    return null;

                if (slot instanceof FakeSlot) {
                    var stack = views.get(index).getDisplayedItemStack().orElse(ItemStack.EMPTY);

                    ClientPacketDistributor.sendToServer(new SetFakeRecipeSlotPayload(slot.index, stack));

                    index++;
                }
            }
        }

        return null;
    }
}
