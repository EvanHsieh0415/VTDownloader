package me.bymartrixx.vtd.gui.widget;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ReloadButtonWidget extends ButtonWidget {
    private static final Text ICON = Text.literal("\u21BB"); // Clockwise arrow ↻

    private static final int BUTTON_SIZE = 20;

    public ReloadButtonWidget(int x, int y, Text message, PressAction onPress) {
        super(x, y, BUTTON_SIZE, BUTTON_SIZE, message, onPress, ButtonWidget.DEFAULT_NARRATION);
    }

    @Override
    protected void drawScrollingText(GuiGraphics graphics, TextRenderer textRenderer, int xOffset, int color) {
        // ClickableWidget#drawScrollableText
        int scale = 2;
        int left = (this.getX() + xOffset) / scale;
        int right = (this.getX() + this.getWidth() - xOffset) / scale;

        graphics.getMatrices().push();
        graphics.getMatrices().scale(scale, scale, scale);
        drawScrollingText(graphics, textRenderer, ICON, left, this.getY() / scale, right, (this.getY() + this.getHeight()) / scale, color);
        graphics.getMatrices().pop();
    }
}
