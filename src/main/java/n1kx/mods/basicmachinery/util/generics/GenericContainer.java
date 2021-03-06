package n1kx.mods.basicmachinery.util.generics;

import mcp.MethodsReturnNonnullByDefault;
import n1kx.mods.basicmachinery.util.Methods;
import n1kx.mods.basicmachinery.util.RecipePart;
import n1kx.mods.basicmachinery.util.generics.tileentity.GenericTEInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class GenericContainer extends Container {

    public final InventoryPlayer playerInventory;
    public final GenericTEInventory tileEntity;

    protected GenericContainer( InventoryPlayer playerInventory , GenericTEInventory tileEntity ) {
        this.playerInventory = playerInventory;
        this.tileEntity = tileEntity;
    }

    protected void addInventorySlots() {
        for( int y = 0 ; y < 3 ; y++ ) {
            for( int x = 0 ; x < 9 ; x++ ) {
                this.addSlotToContainer( new Slot( this.playerInventory , x + y * 9 + 9 , 8 + x * 18 , 84 + y * 18 ) );
            }
        }
        for( int x = 0 ; x < 9 ; x++ ) {
            this.addSlotToContainer( new Slot( this.playerInventory , x , 8 + x * 18 , 142 ) );
        }
    }

    @Override
    public ItemStack transferStackInSlot( EntityPlayer playerIn , int index ) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = super.inventorySlots.get( index );

        if( slot != null && slot.getHasStack() ) {
            ItemStack stack1 = slot.getStack();
            stack = stack1.copy();

            if( index < this.tileEntity.inventorySize - this.tileEntity.outputSlots ) {
                if( !super.mergeItemStack( stack1 , this.tileEntity.inventorySize , super.inventoryItemStacks.size() , false ) ) return ItemStack.EMPTY;
            }
            else if( index >= this.tileEntity.inputSlots + this.tileEntity.fuelSlots && index < this.tileEntity.inventorySize ) {
                if( !this.mergeItemStack( stack1 , this.tileEntity.inventorySize , 36 + this.tileEntity.inventorySize , true ) ) return ItemStack.EMPTY;
            }
            else if( index >= this.tileEntity.inventorySize ) {
                if( this.tileEntity.recipes != null ) if( this.tileEntity.recipes.areInRecipe( RecipePart.INPUT , stack1 ) && this.tileEntity.inputSlots > 0 ) {
                    if( !this.mergeItemStack( stack1 , 0 , this.tileEntity.inputSlots , false ) ) return ItemStack.EMPTY;
                }
                else if( Methods.isFuel( stack1 ) && this.tileEntity.fuelSlots > 0 ) {
                    if( !this.mergeItemStack( stack1 , this.tileEntity.inputSlots + this.tileEntity.fuelSlots - 1 , this.tileEntity.inventorySize - this.tileEntity.outputSlots , false ) ) return ItemStack.EMPTY;
                }
                else if( index > this.tileEntity.inventorySize && index < this.tileEntity.inventorySize + 27 ) {
                    if( !this.mergeItemStack( stack1 , this.tileEntity.inventorySize + 27 , this.tileEntity.inventorySize + 36 , false ) ) return ItemStack.EMPTY;
                }
                else if( index >= this.tileEntity.inventorySize + 27 && index < this.tileEntity.inventorySize + 36 ) {
                    if( !this.mergeItemStack( stack1 , this.tileEntity.inventorySize , this.tileEntity.inventorySize + 27 , false ) ) return ItemStack.EMPTY;
                }
            }
            if( stack1.isEmpty() ) slot.putStack(ItemStack.EMPTY);
            else slot.onSlotChanged();

            if( stack1.getCount() == stack.getCount() ) return ItemStack.EMPTY;

            slot.onTake( playerIn, stack1 );
        }
        return stack;
    }

    @Override
    public boolean canInteractWith( EntityPlayer playerIn ) {
        return this.tileEntity.canInteractWith( playerIn );
    }

}