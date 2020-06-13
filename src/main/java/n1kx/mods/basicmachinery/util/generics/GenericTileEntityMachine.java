package n1kx.mods.basicmachinery.util.generics;

import mcp.MethodsReturnNonnullByDefault;
import n1kx.mods.basicmachinery.util.IRecipes;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public abstract class GenericTileEntityMachine extends GenericTileEntityInventory implements ITickable {

    private int progress;

    protected GenericTileEntityMachine( int inputSlots , int outputSlots , int fuelSlots , GenericBlock block , @Nullable IRecipes recipes ) {
        this( inputSlots , outputSlots , fuelSlots , block, recipes , null );
    }

    protected GenericTileEntityMachine( int inputSlots , int outputSlots , int fuelSlots , GenericBlock block , @Nullable IRecipes recipes , @Nullable String customName ) {
        super( inputSlots , outputSlots , fuelSlots , block , recipes , customName );

        this.progress = 0;
    }

    @Override
    public int getField( int id ) {
        return this.progress;
    }

    @Override
    public void setField( int id , int value ) {
        this.progress = value;
    }

    @Override
    public int getFieldCount() {
        return 1;
    }

    @Override
    public void readFromNBT( NBTTagCompound compound ) {
        super.readInventory( compound );
        if( compound.hasKey( "progress" ) ) progress = compound.getInteger( "progress" );
    }

    @Override
    public NBTTagCompound writeToNBT( NBTTagCompound compound ) {
        super.writeInventory( compound );
        compound.setInteger( "progress", this.progress );
        return compound;
    }

    @Override
    public void update() {
        if( !super.world.isRemote ) {
            if( this.progress > 0 ) {
                this.progress--;

                if( this.progress <= 0 ) {
                    this.attemptMachine();
                }

                super.markDirty();
            } else {
                this.startMachine();
            }
        }
    }

    public void startMachine() {
        if( super.recipes != null ) {
            ItemStack[] inputs = this.getInputs();

            int progress = this.recipes.getWorkTime( inputs );
            if( progress > -1 ) this.progress = progress;
        }
    }

    public void attemptMachine() {
        if( super.recipes != null ) {
            ItemStack[] inputs = this.getInputs();

            ItemStack[] output = super.recipes.getOutputs( inputs );

            if( output != null ) {
                boolean insertingPossible = true;
                for( int i = 0 ; i < output.length ; i++ ) {
                    if( !this.insertOutput( output[i] , i , false ) ) {
                        insertingPossible = false;
                        break;
                    }
                }
                if( insertingPossible ) {
                    for( int i = 0 ; i < output.length ; i++ ) {
                        this.insertOutput( output[i] , i , true );
                    }
                }
            }
        }
    }

    public ItemStack[] getInputs() {
        ItemStack[] inputs = new ItemStack[super.inputSlots];
        for( int i = 0 ; i < inputs.length ; i++ ) {
            inputs[i] = super.getStackInSlot( i );
        }
        return inputs;
    }

    private boolean insertOutput( ItemStack output , int outputIndex , boolean insert ) {
        ItemStack outputStack = super.getStackInSlot( super.inputSlots + super.fuelSlots + outputIndex );

        if( !ItemStack.areItemStacksEqual( output , outputStack ) ) {
            return false;
        }
        else if( insert ) {
            outputStack.setCount( outputStack.getCount() + output.getCount() );
        }

        return true;
    }

}
