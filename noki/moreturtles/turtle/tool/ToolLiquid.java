package noki.moreturtles.turtle.tool;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import noki.moreturtles.MoreTurtlesData;
import noki.moreturtles.items.ItemExtendedItems;
import noki.moreturtles.items.RegisterItems;
import noki.moreturtles.turtle.common.EFailureReason;
import noki.moreturtles.turtle.common.MTTurtleAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import dan200.computercraft.api.turtle.TurtleVerb;


/**********
 * @class TurtleLiquid
 *
 * @description Liquid Turtleを定義するクラスです。
 * @description_en Class of Liquid Turtle.
 */
public class ToolLiquid implements ITurtleUpgrade {

	//******************************//
	// define member variables.
	//******************************//
	public int upgradeMeta = 1;
	public ItemStack upgradeItem = new ItemStack(RegisterItems.extendedItems, 1, this.upgradeMeta);
	
	
	//******************************//
	// define member methods.
	//******************************//
	@Override
	public int getUpgradeID() {
		
		return MoreTurtlesData.liquidTurtleTID;
		
	}
 
	@Override
	public String getUnlocalisedAdjective() {
		
		return "Liquid";
		
	}
 
	@Override
	public TurtleUpgradeType getType() {
		
		return  TurtleUpgradeType.Tool;
		
	}
 
	@Override
	public ItemStack getCraftingItem() {
		
		return upgradeItem;
		
	}
 
	@Override
	public IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
		
		return null;
		
	}
	
	@Override
	public IIcon getIcon(ITurtleAccess turtle, TurtleSide side) {				
		
		return ((ItemExtendedItems)this.upgradeItem.getItem()).getEachExtendedIcon(this.upgradeMeta);
		
	}
	
	@Override
	public void update(ITurtleAccess turtle, TurtleSide side) {
		
	}
 
	@Override
	public TurtleCommandResult useTool(ITurtleAccess turtle, TurtleSide side, TurtleVerb verb, int direction) {
		
		MTTurtleAccess turtleAccess = new MTTurtleAccess(turtle);
		
		switch( verb ) {
			case Dig:
				return this.dig(turtleAccess, direction);
			case Attack:
				return this.attack(turtleAccess, direction);
		}
		return MTTurtleAccess.result(false, EFailureReason.UNKNOWN);
	}

	private TurtleCommandResult dig(MTTurtleAccess turtle, int dir) {
		
		int newX = turtle.posX + Facing.offsetsXForSide[dir];
		int newY = turtle.posY + Facing.offsetsYForSide[dir];
		int newZ = turtle.posZ + Facing.offsetsZForSide[dir];
		
		Block block = turtle.world.getBlock(newX, newY, newZ);
		int metadata = turtle.world.getBlockMetadata(newX, newY, newZ);
		int currentSlot = turtle.getSelectedSlot();
		ItemStack currentItem = turtle.getSlotContents(currentSlot);
		
		if(currentItem == null) {
			return MTTurtleAccess.result(false, EFailureReason.NO_ITEM);
		}
		
		//	case of buckets.
		if(FluidContainerRegistry.isBucket(currentItem) && FluidContainerRegistry.isEmptyContainer(currentItem)) {
			int bucketVolume = FluidContainerRegistry.BUCKET_VOLUME;
			
			//	directly.
			Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
			if(block == Blocks.water || block == Blocks.flowing_water) {
				fluid = FluidRegistry.WATER;
			}
			if(block == Blocks.lava || block == Blocks.flowing_lava) {
				fluid = FluidRegistry.LAVA;
			}
			if(fluid != null && metadata == 0) {
				ItemStack filled = FluidContainerRegistry.fillFluidContainer(new FluidStack(fluid, bucketVolume), currentItem);
				if(filled != null) {
					turtle.consume(currentSlot);
					turtle.store(filled);
					turtle.world.setBlockToAir(newX, newY, newZ);
					
					return MTTurtleAccess.result(true);
				}
			}
			
			//	to Tank.
			TileEntity tile = turtle.world.getTileEntity(newX, newY, newZ);
			if(tile != null && tile instanceof IFluidHandler) {
				ForgeDirection forgeDir = ForgeDirection.VALID_DIRECTIONS[dir];

				IFluidHandler tank = (IFluidHandler)tile;
				FluidTankInfo info = tank.getTankInfo(forgeDir)[0];
				Fluid tankFluid = info.fluid.getFluid();
				
				FluidStack reFluidStack = tank.drain(forgeDir, new FluidStack(tankFluid, bucketVolume), false);
				ItemStack filled = FluidContainerRegistry.fillFluidContainer(new FluidStack(tankFluid, bucketVolume), currentItem);
				if(reFluidStack.amount == bucketVolume && filled != null) {
					tank.drain(forgeDir, new FluidStack(tankFluid, bucketVolume), true);
					turtle.consume(currentSlot);
					turtle.store(filled);
					
					return MTTurtleAccess.result(true);
				}
			}
		}
		
		//	case of glass bottle for water.
		if((block == Blocks.water || block == Blocks.flowing_water) && metadata == 0) {
			if(currentItem.getItem() == Items.glass_bottle) {
				turtle.consume(currentSlot);
				turtle.store(new ItemStack(Items.potionitem, 1, 0));
				
				return MTTurtleAccess.result(true);
			}
		}

		//	case of glass bottle for Cauldron.
		//	a player can't get water bucket by empty bucket from filled Cauldron.
		if(block == Blocks.cauldron) {
/*			if(currentItem.getItem() == Items.bucket && metadata == 3) {
				this.world.setBlockMetadataWithNotify(newX, newY, newZ, 0, 3);
				this.consume(turtle, currentSlot);
				this.store(turtle, new ItemStack(Items.water_bucket));
				
				return result(true);
			}*/
			if(currentItem.getItem() == Items.glass_bottle && metadata > 0) {
				turtle.world.setBlockMetadataWithNotify(newX, newY, newZ, metadata-1, 3);
				turtle.consume(currentSlot);
				turtle.store(new ItemStack(Items.potionitem, 1, 0));
				
				return MTTurtleAccess.result(true);
			}
		}
		
		return MTTurtleAccess.result(false, EFailureReason.NO_DIG_TARGET);
		
	}
	
	private TurtleCommandResult attack(MTTurtleAccess turtle, int dir) {

		int newX = turtle.posX + Facing.offsetsXForSide[dir];
		int newY = turtle.posY + Facing.offsetsYForSide[dir];
		int newZ = turtle.posZ + Facing.offsetsZForSide[dir];
		
		Block block = turtle.world.getBlock(newX, newY, newZ);
		int metadata = turtle.world.getBlockMetadata(newX, newY, newZ);
		int currentSlot = turtle.getSelectedSlot();
		ItemStack currentItem = turtle.getSlotContents(currentSlot);
		
		if(currentItem == null) {
			return MTTurtleAccess.result(false, EFailureReason.NO_ITEM);
		}
		
		//	case of bucket.
		if(FluidContainerRegistry.isBucket(currentItem) && FluidContainerRegistry.isFilledContainer(currentItem)) {
			//	directly.
			ItemBucket currentBucket = (ItemBucket)currentItem.getItem();
			boolean flag = currentBucket.tryPlaceContainedLiquid(turtle.world, newX, newY, newZ);			
			if(flag == true) {
				turtle.consume(currentSlot);
				turtle.store(new ItemStack(Items.bucket));
				
				return MTTurtleAccess.result(true);
			}
			
			//	to Tank.
			TileEntity tile = turtle.world.getTileEntity(newX, newY, newZ);
			if(tile != null && tile instanceof IFluidHandler) {
				IFluidHandler tank = (IFluidHandler)tile;
				int amount = tank.fill(ForgeDirection.VALID_DIRECTIONS[dir],
						FluidContainerRegistry.getFluidForFilledItem(currentItem), false);
				if(amount != 0) {
					tank.fill(ForgeDirection.VALID_DIRECTIONS[dir],
							FluidContainerRegistry.getFluidForFilledItem(currentItem), true);
					turtle.consume(currentSlot);
					turtle.store(new ItemStack(Items.bucket));
					
					return MTTurtleAccess.result(true);
				}
			}
		}
		
		//	case of water bucket for Cauldron.
		//	a player can't fill fully filled Cauldron with water bucket.
		//	a player can't fill Cauldron with water bottle.
		if(block == Blocks.cauldron) {
			//	water bucket.
			if(currentItem.getItem() == Items.water_bucket && metadata < 3) {
				turtle.consume(currentSlot);
				turtle.store(new ItemStack(Items.bucket));
				turtle.world.setBlockMetadataWithNotify(newX, newY, newZ, 3, 3);
				
				return MTTurtleAccess.result(true);
			}
			//	water bottle.
/*			if(currentItem.getItem() == Items.potionitem && currentItem.getItemDamage() == 0) {
				if(metadata < 3) {
					this.consume(turtle, currentSlot);
					this.store(turtle, new ItemStack(Items.glass_bottle));
					this.world.setBlockMetadataWithNotify(newX, newY, newZ, metadata+1, 3);
					
					return result(true);
				}
				if(metadata >=3) {
					this.consume(turtle, currentSlot);
					this.store(turtle, new ItemStack(Items.glass_bottle));
	
					return result(true);
				}
			}*/
		}
		
		return MTTurtleAccess.result(false, EFailureReason.NO_ATTACK_TARGET);
		
	}
 
}
