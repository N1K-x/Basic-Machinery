package n1kx.mods.basicmachinery.util.generics;

import n1kx.mods.basicmachinery.util.Methods;
import n1kx.mods.basicmachinery.util.generics.tileentity.GenericTEMachineFueled;
import n1kx.mods.basicmachinery.util.generics.tileentity.GenericTEInventory;
import n1kx.mods.basicmachinery.util.generics.tileentity.GenericTEMachine;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public abstract class GenericGui extends GuiContainer {

    public final ResourceLocation textureLocation;

    protected final InventoryPlayer playerInventory;
    protected final GenericTEInventory tileEntity;


    public GenericGui( GenericContainer inventorySlotsIn ) {
        super( inventorySlotsIn );

        this.playerInventory = inventorySlotsIn.playerInventory;
        this.tileEntity = inventorySlotsIn.tileEntity;

        this.textureLocation = Methods.newGuiLocation( Objects.requireNonNull( tileEntity.block.getRegistryName() ) );
    }

    @Override
    protected void drawGuiContainerForegroundLayer( int mouseX , int mouseY ) {}

    @Override
    protected void drawGuiContainerBackgroundLayer( float partialTicks , int mouseX , int mouseY ) {
        GlStateManager.color( 1.0f , 1.0f , 1.0f , 1.0f );
        super.mc.getTextureManager().bindTexture( this.textureLocation );
        super.drawTexturedModalRect( super.guiLeft , super.guiTop , 0 , 0 , super.xSize , super.ySize );
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();
        super.drawScreen( mouseX , mouseY , partialTicks );
        super.renderHoveredToolTip( mouseX , mouseY );
    }

    protected int getProgressScaled( int pixels ) {
        if( this.tileEntity instanceof GenericTEMachine ) {
            return this.getCalc( 0 , 1 , ++pixels);
        }
        return -1;
    }

    protected int getBurnTimeScaled( int pixels ) {
        if( this.tileEntity instanceof GenericTEMachineFueled ) {
            return this.getCalc( 2 , 3 , ++pixels );
        }
        return -1;
    }

    protected int getCalc( int field1Index , int field2Index , int pixels ) {
        double field1 = this.tileEntity.getField( field1Index );
        double field2 = this.tileEntity.getField( field2Index );

        double div = field1 != 0 && field2 != 0 ? field1 / field2 : -1;
        return div == -1 ? -1 : (int)( div * pixels );
    }
}
