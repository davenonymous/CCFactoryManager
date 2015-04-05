package com.dave.ccfactorymanager.gui.render;

import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.dave.ccfactorymanager.gui.container.ContainerController;
import com.dave.ccfactorymanager.handler.PacketHandler;
import com.dave.ccfactorymanager.network.MessageNameChange;
import com.dave.ccfactorymanager.reference.Reference;
import com.dave.ccfactorymanager.reference.Textures;
import com.dave.ccfactorymanager.tileentity.TileEntityFactoryController;

public class GuiController extends GuiContainer {
	private TileEntityFactoryController tileEntity;
	private ArrayList<Object> targets;
	int offset = 0;
	private boolean wasClicking = false;
	private boolean isScrolling = false;
	private float currentScroll;
	private int selected = -1;
	int iEnergyCount = 0, iItemCount = 0, iFluidCount = 0;

	private int hoveringIndex = -1;
	private int hoveringTime = 0;

	private GuiTextField textField;
	private String hintText;

	public GuiController(InventoryPlayer inventoryPlayer, TileEntityFactoryController tileEntity) {
		super(new ContainerController(inventoryPlayer, tileEntity));

		this.tileEntity = tileEntity;
		this.targets = tileEntity.getTargets();

		for (int index = 0; index < this.targets.size(); index++) {
			HashMap<String, Object> target = (HashMap<String, Object>) this.targets.get(index);
			ArrayList<String> types = (ArrayList<String>) target.get("Types");

			boolean bEnergyCounted = false;
			for (String type : types) {

				if (type.equals("fluid")) {
					iFluidCount++;
				}
				if (!bEnergyCounted && (type.equals("energy-provider") || type.equals("energy-receiver"))) {
					iEnergyCount++;
					bEnergyCounted = true;
				}
				if (type.equals("item")) {
					iItemCount++;
				}
			}
		}

		xSize = 176;
		ySize = 239;
	}

	private int getScrollSteps() {
		return this.targets.size() - 9;
	}

	@Override
	public void initGui() {
		super.initGui();

		this.hintText = StatCollector.translateToLocal("textinput." + Reference.MOD_ID + ":setid.hint");

		this.textField = new GuiTextField(fontRendererObj, (width - xSize) / 2 + 8, (height - ySize) / 2 + 102, 160, 12);
		this.textField.setMaxStringLength(30);
		this.textField.setText(hintText);
		this.textField.setEnabled(false);
	}

	@Override
	protected void keyTyped(char chr, int keyCode) {
		if (textField.textboxKeyTyped(chr, keyCode)) {
			HashMap<String, Object> target = (HashMap<String, Object>) this.targets.get(this.selected - 1);
			HashMap<String, Integer> posMap = (HashMap<String, Integer>) target.get("Pos");
			PacketHandler.INSTANCE.sendToServer(new MessageNameChange(this.tileEntity, posMap.get("x"), posMap.get("y"), posMap.get("z"), textField.getText()));
			return;
		} else {
			super.keyTyped(chr, keyCode);
		}
	}

	@Override
	public void handleMouseInput() {
		int i = Mouse.getEventDWheel() * -1;

		if (i != 0 && this.needsScrollbar()) {
			int j = (this.targets.size() / 9) - 11;

			if (i > 0) {
				i = 1;
			}

			if (i < 0) {
				i = -1;
			}

			this.currentScroll = (float) (this.currentScroll - (double) i / (double) j);

			if (this.currentScroll < 0.0F) {
				this.currentScroll = 0.0F;
			}

			if (this.currentScroll > 1.0F) {
				this.currentScroll = 1.0F;
			}

			this.offset = Math.round(this.currentScroll * getScrollSteps());
		} else {
			super.handleMouseInput();
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button) {
		if (selected != -1 && mouseX >= textField.xPosition && mouseX < textField.xPosition + textField.width && mouseY >= textField.yPosition && mouseY < textField.yPosition + textField.height) {
			if (this.textField.getText().equals(hintText)) {
				this.textField.setText("");
			}
			this.textField.mouseClicked(mouseX, mouseY, button);
		} else {
			this.textField.setFocused(false);
		}

		if (button == 1) {
			selected = -1;
			this.textField.setEnabled(false);
			return;
		}

		int xStart = (width - xSize) / 2;
		int yStart = (height - ySize) / 2;

		//LogHelper.info("Mouse: (" + mouseX + "|" + mouseY +")");
		//LogHelper.info("Start: ("+xStart+"|"+yStart+")");

		if (mouseX >= xStart + 8 && mouseY > yStart + 8 && mouseX < xStart + 150 && mouseY < yStart + 98) {
			int foo = mouseY - (yStart - 1);
			int clickedRow = foo / 10;
			if (clickedRow <= this.targets.size()) {
				this.selected = this.offset + clickedRow;
				this.textField.setEnabled(true);

				String targetName = getTargetName(this.selected - 1);

				if (!targetName.equals("")) {
					this.textField.setText(targetName);
				} else {
					this.textField.setText(hintText);
				}
			} else {
				this.selected = -1;
			}
		}

		super.mouseClicked(mouseX, mouseY, button);
		return;
	}

	public String getTargetName(int index) {
		HashMap<String, Object> target = (HashMap<String, Object>) this.targets.get(index);
		HashMap<String, Integer> posMap = (HashMap<String, Integer>) target.get("Pos");

		return tileEntity.getName(posMap.get("x"), posMap.get("y"), posMap.get("z"));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float p_73863_3_) {
		super.drawScreen(mouseX, mouseY, p_73863_3_);

		int xStart = (width - xSize) / 2;
		int yStart = (height - ySize) / 2;

		int scrollX = xStart + xSize - 20;
		int scrollY = yStart + 9;

		boolean flag = Mouse.isButtonDown(0);
		if (!this.wasClicking && flag && mouseX >= scrollX && mouseY >= scrollY && mouseX < scrollX + 14 && mouseY < scrollY + 90) {
			this.isScrolling = needsScrollbar();
		}

		if (!flag) {
			this.isScrolling = false;
		}

		this.wasClicking = flag;

		if (this.isScrolling) {
			this.currentScroll = (mouseY - scrollY - 7.5F) / ((87) - 15.0F);

			if (this.currentScroll < 0.0F) {
				this.currentScroll = 0.0F;
			}

			if (this.currentScroll > 1.0F) {
				this.currentScroll = 1.0F;
			}

			//LogHelper.info("Current scroll: " + this.currentScroll);
			this.offset = Math.round(this.currentScroll * getScrollSteps());
			//((GuiContainerCreative.ContainerCreative)this.inventorySlots).scrollTo(this.currentScroll);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		//String containerName = StatCollector.translateToLocal(tileEntity.get);
		//String containerName = "Example GUI";
		//fontRendererObj.drawString(containerName, xSize / 2 - fontRendererObj.getStringWidth(containerName) / 2, 6, 4210752);

		int xStart = (width - xSize) / 2;
		int yStart = (height - ySize) / 2;

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int x, int y) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		this.mc.getTextureManager().bindTexture(Textures.Gui.FACTORY_MANAGER);

		int xStart = (width - xSize) / 2;
		int yStart = (height - ySize) / 2;
		this.drawTexturedModalRect(xStart, yStart, 0, 0, xSize, ySize);

		int scrollMaxHeight = 74;
		int scrollStepSize = 0;
		if (getScrollSteps() > 0) {
			scrollStepSize = Math.round(((1.0F * scrollMaxHeight) / getScrollSteps()) * offset);
		}

		int scrollOffsetX = xStart + xSize - 20;
		int scrollOffsetY = yStart + 9;
		this.drawTexturedModalRect(scrollOffsetX, scrollOffsetY + scrollStepSize, 176 + (getScrollSteps() > 0 ? 0 : 12), 8, 12, 15);

		ArrayList<String> lines = new ArrayList<String>();

		int iEntry = 0;
		int stepSize = 10;
		for (int index = 0; index < this.targets.size(); index++) {
			iEntry++;
			if (index < offset) {
				continue;
			}

			if (index > offset + 8) {
				break;
			}

			HashMap<String, Object> target = (HashMap<String, Object>) this.targets.get(index);
			ArrayList<String> types = (ArrayList<String>) target.get("Types");
			int xOffset = xStart + xSize - 35;

			boolean bEnergyDrawn = false;
			for (String type : types) {
				//RenderHelper.disableStandardItemLighting();
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glColor3f(1F, 1F, 1F); //Forge: Reset color in case Items change it.
				GL11.glEnable(GL11.GL_BLEND); //Forge: Make sure blend is enabled else tabs show a white border.

				if (type.equals("fluid")) {
					this.mc.getTextureManager().bindTexture(Textures.Gui.FACTORY_MANAGER);
					this.drawTexturedModalRect(xOffset, scrollOffsetY + stepSize * (index - offset), 176, 0, 8, 8);
					xOffset -= 8;
				}
				if (!bEnergyDrawn && (type.equals("energy-provider") || type.equals("energy-receiver"))) {
					this.mc.getTextureManager().bindTexture(Textures.Gui.FACTORY_MANAGER);
					this.drawTexturedModalRect(xOffset, scrollOffsetY + stepSize * (index - offset), 184, 0, 8, 8);
					bEnergyDrawn = true;
					xOffset -= 8;
				}
				if (type.equals("item")) {
					this.mc.getTextureManager().bindTexture(Textures.Gui.FACTORY_MANAGER);
					this.drawTexturedModalRect(xOffset, scrollOffsetY + stepSize * (index - offset), 192, 0, 8, 8);
					xOffset -= 8;
				}
				//RenderHelper.enableGUIStandardItemLighting();

			}
			xOffset++;

			String targetName = getTargetName(index);
			String name = (String) target.get("Name");
			String nameForList = name;
			if (!targetName.equals("") && !isShiftKeyDown()) {
				nameForList = EnumChatFormatting.ITALIC + targetName + EnumChatFormatting.RESET;
			}

			boolean trimmedName = false;
			while (fontRendererObj.getStringWidth(nameForList) > 110) {
				nameForList = nameForList.substring(0, nameForList.length() - 1);
				trimmedName = true;
			}
			if (trimmedName)
				nameForList += "...";

			int boxX = xStart + 10;
			int boxY = scrollOffsetY + 1 + stepSize * (index - offset);

			if (iEntry == selected) {
				fontRendererObj.drawString(nameForList, boxX, boxY, 0xFFFFFF);
			} else {
				fontRendererObj.drawString(nameForList, boxX, boxY, 0x404040);
			}

			if (x >= boxX - 2 && x < boxX + 140 && y >= boxY - 1 && y < boxY + 9 && y < yStart + 98) {
				if (hoveringIndex != offset + index) {
					hoveringIndex = offset + index;
					hoveringTime = 0;
				} else {
					hoveringTime++;
				}

				lines.add(EnumChatFormatting.YELLOW + name);

				if (!targetName.equals("")) {
					lines.add(EnumChatFormatting.WHITE + "ID: " + EnumChatFormatting.GRAY + targetName);
				}

				HashMap<String, Integer> posMap = (HashMap<String, Integer>) target.get("Pos");
				lines.add(EnumChatFormatting.WHITE + "X: " + EnumChatFormatting.GRAY + posMap.get("x") +
						EnumChatFormatting.WHITE + " Y: " + EnumChatFormatting.GRAY + posMap.get("y") +
						EnumChatFormatting.WHITE + " Z: " + EnumChatFormatting.GRAY + posMap.get("z"));
			}
		}

		if (selected != -1) {
			this.textField.drawTextBox();
		} else {
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glColor3f(1F, 1F, 1F); //Forge: Reset color in case Items change it.
			GL11.glEnable(GL11.GL_BLEND); //Forge: Make sure blend is enabled else tabs show a white border.

			this.mc.getTextureManager().bindTexture(Textures.Gui.FACTORY_MANAGER);
			this.drawTexturedModalRect(xStart + 110, yStart + 102, 176, 0, 8, 8);

			this.mc.getTextureManager().bindTexture(Textures.Gui.FACTORY_MANAGER);
			this.drawTexturedModalRect(xStart + 70, yStart + 102, 184, 0, 8, 8);

			this.mc.getTextureManager().bindTexture(Textures.Gui.FACTORY_MANAGER);
			this.drawTexturedModalRect(xStart + 30, yStart + 102, 192, 0, 8, 8);

			fontRendererObj.drawString(iItemCount + "", xStart + 39, yStart + 103, 4210752);
			fontRendererObj.drawString(iEnergyCount + "", xStart + 79, yStart + 103, 4210752);
			fontRendererObj.drawString(iFluidCount + "", xStart + 119, yStart + 103, 4210752);
		}

		if (lines.size() > 0) {
			if (hoveringTime > 100) {
				drawHoveringText(lines, x, y + 20, fontRendererObj);
			}
		} else {
			hoveringTime = 0;
		}

	}

	private boolean needsScrollbar() {
		return getScrollSteps() > 0;
	}

}
