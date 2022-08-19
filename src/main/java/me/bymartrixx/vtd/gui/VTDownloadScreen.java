package me.bymartrixx.vtd.gui;

import me.bymartrixx.vtd.VTDMod;
import me.bymartrixx.vtd.data.Category;
import me.bymartrixx.vtd.data.Pack;
import me.bymartrixx.vtd.gui.widget.CategorySelectionWidget;
import me.bymartrixx.vtd.gui.widget.PackSelectionListWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.ScreenTexts;
import net.minecraft.text.Text;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VTDownloadScreen extends Screen {
    private static final int PACK_SELECTOR_TOP_HEIGHT = 66;
    private static final int PACK_SELECTOR_BOTTOM_HEIGHT = 32;
    private static final int PACK_SELECTOR_ITEM_HEIGHT = 32;

    private final Screen parent;
    private final Text subtitle;
    private final List<Category> categories;

    private Category currentCategory;

    private CategorySelectionWidget categorySelector;
    private PackSelectionListWidget packSelector;

    private final Map<Category, List<Pack>> selectedPacks = new LinkedHashMap<>();

    public VTDownloadScreen(Screen parent, Text subtitle) {
        super(Text.literal("VTDownloader"));
        this.parent = parent;
        this.subtitle = subtitle;

        this.categories = VTDMod.rpCategories.getCategories();
        this.currentCategory = this.categories.size() > 0 ? this.categories.get(0) : null;
    }

    public boolean selectCategory(Category category) {
        if (this.currentCategory != category) {
            this.currentCategory = category;
            this.packSelector.setCategory(category);
            return true;
        }

        return false;
    }

    @SuppressWarnings("ConstantConditions") // client is marked as nullable
    @Override
    public void closeScreen() {
        this.client.setScreen(this.parent);
    }

    @Override
    protected void init() {
        this.packSelector = this.addDrawableChild(new PackSelectionListWidget(this.client, this.width, this.height,
                PACK_SELECTOR_TOP_HEIGHT, this.height - PACK_SELECTOR_BOTTOM_HEIGHT, PACK_SELECTOR_ITEM_HEIGHT,
                selectedPacks, currentCategory));

        this.addDrawableChild(new ButtonWidget(this.width - 90, this.height - 30, 80, 20, ScreenTexts.DONE, button -> this.closeScreen()));

        this.categorySelector = this.addDrawableChild(new CategorySelectionWidget(this, 32));
        this.categorySelector.setCategories(this.categories);
        this.categorySelector.initCategoryButtons();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(0);
        super.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
        drawCenteredText(matrices, this.textRenderer, this.subtitle, this.width / 2, 20, 0xFFFFFF);
    }
}