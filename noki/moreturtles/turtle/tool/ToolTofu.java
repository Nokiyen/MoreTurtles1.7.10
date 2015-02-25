package noki.moreturtles.turtle.tool;
 
import tsuteto.tofu.block.BlockMisoBarrel;
import tsuteto.tofu.material.TcMaterial;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
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
 * @class ToolTofu
 *
 * @description Tofu Turtleを定義するクラスです。
 * @description_en Class of Tofu Turtle.
 */
public class ToolTofu implements ITurtleUpgrade {

	//******************************//
	// define member variables.
	//******************************//
	public int upgradeMeta = 14;
	public ItemStack upgradeItem = new ItemStack(RegisterItems.extendedItems, 1, this.upgradeMeta);
	
	
	//******************************//
	// define member methods.
	//******************************//
	@Override
	public int getUpgradeID() {
		
		return MoreTurtlesData.tofuTurtleTID;
		
	}
 
	@Override
	public String getUnlocalisedAdjective() {
		
		return "Tofu";
		
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
		
		//case of tofu.
		if(block.getMaterial() == TcMaterial.tofu) {
			Item droppedItem = block.getItemDropped(metadata, turtle.world.rand, 0);
			int quantity = block.quantityDropped(turtle.world.rand);
			ItemStack dropped = new ItemStack(droppedItem, quantity ,0);
			
			turtle.world.playAuxSFX(2001, newX, newY, newZ, Block.getIdFromBlock(block) + metadata * 4096);
			turtle.world.setBlockToAir(newX, newY, newZ);
			turtle.store(dropped);
			
			return MTTurtleAccess.result(true);
		}
		
		int currentSlot = turtle.getSelectedSlot();
		ItemStack currentItem = turtle.getSlotContents(currentSlot);		
		if(currentItem == null) {
			return MTTurtleAccess.result(false, EFailureReason.NO_ITEM);
		}

		//case of bucket to soymilk, soymilkHell, and soysauce.
		if(currentItem.getItem() == Items.bucket) {
			if ((block == MoreTurtlesData.tofu_soymilkB || block == MoreTurtlesData.tofu_soymilkHellB
					|| block == MoreTurtlesData.tofu_soysauceB) && metadata == 0) {
				int bucketVolume = FluidContainerRegistry.BUCKET_VOLUME;
				
				//	directly.
				Fluid fluid = FluidRegistry.lookupFluidForBlock(block);
				if(fluid != null && metadata == 0) {
					ItemStack filled = FluidContainerRegistry.fillFluidContainer(new FluidStack(fluid, bucketVolume), currentItem);
					if(filled != null) {
						turtle.consume(currentSlot);
						turtle.store(filled);
						turtle.world.setBlockToAir(newX, newY, newZ);
						
						return MTTurtleAccess.result(true);
					}
				}
			}
			else if(block == MoreTurtlesData.tofu_misoBarrelB && ((BlockMisoBarrel)block).hasSoySauce(metadata)) {
				ItemStack stack = new ItemStack(MoreTurtlesData.tofu_bucketSoysauceI);
				turtle.consume(currentSlot);
				turtle.store(stack);
				((BlockMisoBarrel)block).removeSoySauce(turtle.world, newX, newY, newZ);
				
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
//		int metadata = this.world.getBlockMetadata(newX, newY, newZ);
		int currentSlot = turtle.getSelectedSlot();
		ItemStack currentItem = turtle.getSlotContents(currentSlot);
		
		if(currentItem == null) {
			return MTTurtleAccess.result(false, EFailureReason.NO_ITEM);
		}
		
		//case of soymilk bucket, soymilkHell bucket, or soysauce bucket to air.
		if(currentItem.getItem() == MoreTurtlesData.tofu_bucketSoymilkI
				|| currentItem.getItem() == MoreTurtlesData.tofu_bucketSoymilkHellI
				|| currentItem.getItem() == MoreTurtlesData.tofu_bucketSoysauceI) {
			ItemBucket currentBucket = (ItemBucket)currentItem.getItem();
			boolean flag = currentBucket.tryPlaceContainedLiquid(turtle.world, newX, newY, newZ);
			if(flag == true) {
				turtle.consume(currentSlot);
				turtle.store(new ItemStack(Items.bucket));
				
				return MTTurtleAccess.result(true);
			}
		}
		//case of nigari to soymilk and soymilkHell.
		else if(currentItem.getItem() == MoreTurtlesData.tofu_nigariI) {
			if(block == MoreTurtlesData.tofu_soymilkB) {
				if(turtle.consumeFuel(MoreTurtlesData.consumedFuelLevel)) {
					turtle.consume(currentSlot);
					ItemStack bottle = new ItemStack(Items.glass_bottle);
					turtle.store(bottle);
					turtle.world.setBlock(newX, newY, newZ, MoreTurtlesData.tofu_kinuB, 0, 3);
					
					return MTTurtleAccess.result(true);
				}
				else {
					return MTTurtleAccess.result(false, EFailureReason.NO_FUEL);
				}
			}
			else if (block == MoreTurtlesData.tofu_soymilkHellB) {
				if(turtle.consumeFuel(MoreTurtlesData.consumedFuelLevel)) {
					turtle.consume(currentSlot);
					ItemStack bottle = new ItemStack(Items.glass_bottle);
					turtle.store(bottle);
					turtle.world.setBlock(newX, newY, newZ, MoreTurtlesData.tofu_hellB, 0, 3);
					
					return MTTurtleAccess.result(true);
				}
				else {
					return MTTurtleAccess.result(false, EFailureReason.NO_FUEL);
				}
			}
		}
		
		return MTTurtleAccess.result(false, EFailureReason.NO_ATTACK_TARGET);
		
	}
 
}
