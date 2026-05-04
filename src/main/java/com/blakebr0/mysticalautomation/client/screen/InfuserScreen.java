package com.blakebr0.mysticalautomation.client.screen;

import com.blakebr0.cucumber.client.screen.BaseContainerScreen;
import com.blakebr0.cucumber.client.screen.widget.EnergyBarWidget;
import com.blakebr0.cucumber.client.screen.widget.FuelWidget;
import com.blakebr0.mysticalautomation.MysticalAutomation;
import com.blakebr0.mysticalautomation.client.screen.widget.InfuserProgressWidget;
import com.blakebr0.mysticalautomation.client.screen.widget.InfuserSelectedWidget;
import com.blakebr0.mysticalautomation.compat.MysticalCompat;
import com.blakebr0.mysticalautomation.container.InfuserContainer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class InfuserScreen extends BaseContainerScreen<InfuserContainer> {
    public static final Identifier BACKGROUND = MysticalAutomation.resource("textures/gui/infuser.png");

    public InfuserScreen(InfuserContainer container, Inventory inv, Component title) {
        super(container, inv, title, BACKGROUND, 217, 194);
    }

    @Override
    protected void init() {
        super.init();

        int x = this.getLeftPos();
        int y = this.getTopPos();

        this.addRenderableWidget(new EnergyBarWidget(x + 7, y + 17, this.menu::getEnergyStored, this.menu::getMaxEnergyStored));
        this.addRenderableWidget(new FuelWidget(x + 30, y + 39, this.menu::getFuelItemValue, this.menu::getFuelLeft));

        for (var i = 0; i < 6; i++) {
            var index = i;

            this.addRenderableWidget(new InfuserProgressWidget(x + 103 + i * 18, y + 52, this.menu::getProgress, this.menu::getOperationTime, () -> this.menu.getProgressingIndex() == index));

            // no need to have a button for Inferium since it doesn't do anything
            if (index > 0) {
                this.addRenderableWidget(new InfuserSelectedWidget(x + 107 + i * 18, y + 57, this.menu.getBlockPos(), i, () -> this.menu.getSelectedIndex() == index));
            }
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor gfx, int mouseX, int mouseY) {
        gfx.text(this.font, this.title, (this.imageWidth / 2 - this.font.width(this.title) / 2), 6, -12566464, false);
        gfx.text(this.font, this.playerInventoryTitle, 28, (this.imageHeight - 96 + 2), -12566464, false);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor gfx, int mouseX, int mouseY, float a) {
        super.extractBackground(gfx, mouseX, mouseY, a);

        var x = this.getLeftPos();
        var y = this.getTopPos();

        this.renderGhostItem(gfx, 1, x + 62, y + 33, MysticalCompat.Items.INFUSION_CRYSTAL);
        this.renderGhostItem(gfx, 2, x + 102, y + 33, MysticalCompat.Items.INFERIUM_ESSENCE);
        this.renderGhostItem(gfx, 3, x + 120, y + 33, MysticalCompat.Items.PRUDENTIUM_ESSENCE);
        this.renderGhostItem(gfx, 4, x + 138, y + 33, MysticalCompat.Items.TERTIUM_ESSENCE);
        this.renderGhostItem(gfx, 5, x + 156, y + 33, MysticalCompat.Items.IMPERIUM_ESSENCE);
        this.renderGhostItem(gfx, 6, x + 174, y + 33, MysticalCompat.Items.SUPREMIUM_ESSENCE);
        this.renderGhostItem(gfx, 7, x + 192, y + 33, MysticalCompat.Items.INSANIUM_ESSENCE);
    }

    private void renderGhostItem(GuiGraphicsExtractor gfx, int index, int x, int y, Holder<Item> item) {
        if (!item.isBound())
            return;

        if (this.menu.slots.get(index).hasItem())
            return;

        this.extractGhostItem(gfx, x, y, new ItemStack(item));
    }
}
