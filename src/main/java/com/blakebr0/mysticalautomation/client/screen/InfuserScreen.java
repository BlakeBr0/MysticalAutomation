package com.blakebr0.mysticalautomation.client.screen;

import com.blakebr0.cucumber.client.render.GhostItemRenderer;
import com.blakebr0.cucumber.client.screen.BaseContainerScreen;
import com.blakebr0.cucumber.client.screen.widget.EnergyBarWidget;
import com.blakebr0.cucumber.client.screen.widget.FuelWidget;
import com.blakebr0.mysticalautomation.MysticalAutomation;
import com.blakebr0.mysticalautomation.client.screen.widget.InfuserProgressWidget;
import com.blakebr0.mysticalautomation.client.screen.widget.InfuserSelectedWidget;
import com.blakebr0.mysticalautomation.compat.MysticalCompat;
import com.blakebr0.mysticalautomation.container.InfuserContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class InfuserScreen extends BaseContainerScreen<InfuserContainer> {
    public static final ResourceLocation BACKGROUND = MysticalAutomation.resource("textures/gui/infuser.png");

    public InfuserScreen(InfuserContainer container, Inventory inv, Component title) {
        super(container, inv, title, BACKGROUND, 217, 194);
    }

    @Override
    protected void init() {
        super.init();

        int x = this.getGuiLeft();
        int y = this.getGuiTop();

        this.addRenderableWidget(new EnergyBarWidget(x + 7, y + 17, this.menu::getEnergyStored, this.menu::getMaxEnergyStored));
        this.addRenderableWidget(new FuelWidget(x + 30, y + 39, this.menu::getFuelItemValue, this.menu::getFuelLeft));

        for (var i = 0; i < 6; i++) {
            var index = i;

            this.addRenderableWidget(new InfuserProgressWidget(x + 103 + i * 18, y + 52, this.menu::getProgress, this.menu::getOperationTime, () -> this.menu.getProgressingIndex() == index));
            this.addRenderableWidget(new InfuserSelectedWidget(x + 107 + i * 18, y + 57, this.menu.getBlockPos(), i, () -> this.menu.getSelectedIndex() == index));
        }
    }

    @Override
    protected void renderLabels(GuiGraphics gfx, int mouseX, int mouseY) {
        gfx.drawString(this.font, this.title, (this.imageWidth / 2 - this.font.width(this.title) / 2), 6, 4210752, false);
        gfx.drawString(this.font, this.playerInventoryTitle, 28, (this.imageHeight - 96 + 2), 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics gfx, float partialTicks, int mouseX, int mouseY) {
        super.renderBg(gfx, partialTicks, mouseX, mouseY);

        var x = this.getGuiLeft();
        var y = this.getGuiTop();

        this.renderGhostItem(1, x + 62, y + 33, MysticalCompat.Items.INFUSION_CRYSTAL);
        this.renderGhostItem(2, x + 102, y + 33, MysticalCompat.Items.INFERIUM_ESSENCE);
        this.renderGhostItem(3, x + 120, y + 33, MysticalCompat.Items.PRUDENTIUM_ESSENCE);
        this.renderGhostItem(4, x + 138, y + 33, MysticalCompat.Items.TERTIUM_ESSENCE);
        this.renderGhostItem(5, x + 156, y + 33, MysticalCompat.Items.IMPERIUM_ESSENCE);
        this.renderGhostItem(6, x + 174, y + 33, MysticalCompat.Items.SUPREMIUM_ESSENCE);
        this.renderGhostItem(7, x + 192, y + 33, MysticalCompat.Items.INSANIUM_ESSENCE);
    }

    private void renderGhostItem(int index, int x, int y, Holder<Item> item) {
        if (this.minecraft == null)
            return;

        if (!item.isBound())
            return;

        if (this.menu.slots.get(index).hasItem())
            return;

        GhostItemRenderer.renderItemIntoGui(new ItemStack(item), x, y, this.minecraft.getItemRenderer());
    }
}
