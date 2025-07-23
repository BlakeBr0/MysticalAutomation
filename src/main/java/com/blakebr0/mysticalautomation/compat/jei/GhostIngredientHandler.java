package com.blakebr0.mysticalautomation.compat.jei;

import com.blakebr0.cucumber.client.screen.BaseContainerScreen;
import com.blakebr0.mysticalautomation.container.slot.FakeSlot;
import com.blakebr0.mysticalautomation.network.payload.SetFakeRecipeSlotPayload;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class GhostIngredientHandler<T extends BaseContainerScreen<?>> implements IGhostIngredientHandler<T> {
    @Override
    public <I> List<Target<I>> getTargetsTyped(T screen, ITypedIngredient<I> ingredient, boolean doStart) {
        var menu = screen.getMenu();
        var targets = new ArrayList<Target<I>>();
        for (var slot : menu.slots) {
            if (slot instanceof FakeSlot) {
                targets.add(new Target<>() {
                    @Override
                    public Rect2i getArea() {
                        return new Rect2i(screen.getGuiLeft() + slot.x, screen.getGuiTop() + slot.y, 16, 16);
                    }

                    @Override
                    public void accept(I ingredient) {
                        if (ingredient instanceof ItemStack stack) {
                            var player = Minecraft.getInstance().player;
                            if (player == null)
                                return;

                            slot.set(stack);
                            PacketDistributor.sendToServer(new SetFakeRecipeSlotPayload(slot.index, stack));
                        }
                    }
                });
            }
        }

        return targets;
    }

    @Override
    public void onComplete() {

    }
}
