package io.github.bymartrixx.vtd.gui.widget;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.bymartrixx.vtd.VTDMod;
import io.github.bymartrixx.vtd.gui.VTDScreen;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;

public class PackListWidget extends EntryListWidget<PackListWidget.PackEntry> {
    public final String categoryName;
    public final boolean oneEntry; // If it should keep only one entry selected at once
    private final boolean displayEntries;

    public PackListWidget(JsonArray packs, String categoryName) {
        super(VTDScreen.getInstance().getClient(), VTDScreen.getInstance().width - 180, VTDScreen.getInstance().height, 60, VTDScreen.getInstance().height - 40, 32);
        this.displayEntries = true;

        this.setRenderHeader(true, 16);

        this.categoryName = categoryName;
        this.oneEntry = this.categoryName.equals("Menu Panoramas") || this.categoryName.equals("Options Background");

        for (int i = 0; i < packs.size(); ++i) {
            JsonObject pack = packs.get(i).getAsJsonObject();

            this.addEntry(new PackListWidget.PackEntry(pack));
        }
    }

    public PackListWidget() {
        super(VTDScreen.getInstance().getClient(), VTDScreen.getInstance().width - 180, VTDScreen.getInstance().height, 60, VTDScreen.getInstance().height - 40, 32);
        this.displayEntries = false;
        this.categoryName = "Error!";
        this.oneEntry = false;
    }

    public int getRowWidth() {
        return this.width - 20;
    }

    protected int getScrollbarPositionX() {
        return this.width - 10;
    }

    // TODO: Incompatible packs warning

    public void setSelected(@Nullable PackListWidget.PackEntry entry) {
        this.setSelected(entry, true);
    }

    public void setSelected(@Nullable PackListWidget.PackEntry entry, boolean child) {
        if (entry == null) return;

        if (this.children().contains(entry) || !child) {
            String packName = entry.name;
            if (!VTDScreen.getInstance().isPackSelected(this.categoryName, packName)) {
                VTDScreen.getInstance().addSelectedPack(this.categoryName, packName, this.oneEntry);
            } else {
                VTDScreen.getInstance().removeSelectedPack(this.categoryName, packName);
            }
        }
    }

    public boolean isSelected(PackListWidget.PackEntry entry) {
        if (!this.children().contains(entry)) return false;

        return VTDScreen.getInstance().isPackSelected(this.categoryName, entry.name);
    }

    protected boolean isSelectedItem(int index) {
        return this.isSelected(this.children().get(index));
    }

    protected void renderHeader(MatrixStack matrices, int x, int y, Tessellator tessellator) {
        Text text = new LiteralText(this.categoryName).formatted(Formatting.BOLD, Formatting.UNDERLINE);
        VTDScreen.getInstance().getTextRenderer().draw(matrices, text, ((float) (this.width / 2 - VTDScreen.getInstance().getTextRenderer().getWidth(text) / 2)), Math.min(this.top + 3, y), 16777215);
    }

    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        if (this.displayEntries) {
            super.render(matrices, mouseX, mouseY, delta);
        } else {
            Text msgHeader = new TranslatableText("vtd.packError.title.1").formatted(Formatting.BOLD, Formatting.ITALIC);
            Text msgHeader2 = new TranslatableText("vtd.packError.title.2").formatted(Formatting.BOLD, Formatting.ITALIC);
            Text msgBody = new TranslatableText("vtd.packError.body.1");
            Text msgBody2 = new TranslatableText("vtd.packError.body.2");
            Text msgBody3 = new TranslatableText("vtd.packError.body.3", VTDMod.BASE_URL);

            VTDScreen.getInstance().getTextRenderer().draw(matrices, msgHeader, ((float) (this.width / 2 - VTDScreen.getInstance().getTextRenderer().getWidth(msgHeader) / 2)), ((float) (this.height / 2) - 32), 16777215);
            VTDScreen.getInstance().getTextRenderer().draw(matrices, msgHeader2, ((float) (this.width / 2 - VTDScreen.getInstance().getTextRenderer().getWidth(msgHeader2) / 2)), ((float) (this.height / 2) - 16), 16777215);
            VTDScreen.getInstance().getTextRenderer().draw(matrices, msgBody, ((float) (this.width / 2 - VTDScreen.getInstance().getTextRenderer().getWidth(msgBody) / 2)), ((float) (this.height / 2)), 16777215);
            VTDScreen.getInstance().getTextRenderer().draw(matrices, msgBody2, ((float) (this.width / 2 - VTDScreen.getInstance().getTextRenderer().getWidth(msgBody2) / 2)), ((float) (this.height / 2) + 16), 16777215);
            VTDScreen.getInstance().getTextRenderer().draw(matrices, msgBody3, ((float) (this.width / 2 - VTDScreen.getInstance().getTextRenderer().getWidth(msgBody3) / 2)), ((float) (this.height / 2) + 32), 16777215);
        }
    }

    public class PackEntry extends EntryListWidget.Entry<PackListWidget.PackEntry> {
        public final String name;
        public final String displayName;
        public final String description;
        public final String[] incompatiblePacks;

        PackEntry(JsonObject pack) {
            this.name = pack.get("name").getAsString();

            this.displayName = pack.get("display").getAsString();
            this.description = pack.get("description").getAsString();

            Iterator<JsonElement> incompatiblePacksIterator = pack.get("incompatible").getAsJsonArray().iterator();
            ArrayList<String> incompatiblePacks = new ArrayList<>();

            while (incompatiblePacksIterator.hasNext()) {
                incompatiblePacks.add(incompatiblePacksIterator.next().getAsString());
            }

            this.incompatiblePacks = incompatiblePacks.toArray(new String[0]);
        }

        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0) {
                this.setSelected();
            }

            return false;
        }

        private void setSelected() {
            PackListWidget.this.setSelected(this);
        }

        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            VTDScreen.getInstance().getTextRenderer().drawWithShadow(matrices, this.displayName, ((float) (PackListWidget.this.width / 2 - VTDScreen.getInstance().getTextRenderer().getWidth(this.displayName) / 2)), y + 1, 16777215);
            this.renderDescription(matrices, y);
        }

        private void renderDescription(MatrixStack matrices, int y) {
            int textWidth = VTDScreen.getInstance().getTextRenderer().getWidth(this.description);
            int maxWidth = Math.min(280, PackListWidget.this.getRowWidth() - 4);

            if (textWidth > maxWidth) {
                String description = VTDScreen.getInstance().getTextRenderer().trimToWidth(this.description, maxWidth - VTDScreen.getInstance().getTextRenderer().getWidth("...")) + "...";
                VTDScreen.getInstance().getTextRenderer().drawWithShadow(matrices, description, ((float) (PackListWidget.this.width / 2 - VTDScreen.getInstance().getTextRenderer().getWidth(description) / 2)), y + 13, 16777215);
            } else {
                VTDScreen.getInstance().getTextRenderer().drawWithShadow(matrices, this.description, ((float) (PackListWidget.this.width / 2 - VTDScreen.getInstance().getTextRenderer().getWidth(this.description) / 2)), y + 13, 16777215);
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof String) {
                return equals((String) obj);
            }

            return super.equals(obj);
        }

        public boolean equals(String packName) {
            return this.name.equals(packName);
        }
    }
}
