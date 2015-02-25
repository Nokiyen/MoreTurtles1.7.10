package noki.moreturtles.turtle.tool;
 
import java.util.Random;

import mmm.littleMaidMob.entity.EntityLittleMaidBase;
import mmm.littleMaidMob.inventory.InventoryLittleMaid;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.entity.Entity;
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
 * @class ToolMaid
 *
 * @description Maid Turtleを定義するクラスです。
 */
public class ToolMaid implements ITurtleUpgrade {
	
	//******************************//
	// define member variables.
	//******************************//]
	private int upgradeMeta = 13;
	private ItemStack upgradeItem = new ItemStack(RegisterItems.extendedItems, 1, this.upgradeMeta);
	
	
	//******************************//
	// define member methods.
	//******************************//
	@Override
	public int getUpgradeID() {
		
		return MoreTurtlesData.maidTurtleTID;
		
	}
 
	@Override
	public String getUnlocalisedAdjective() {
		
		return "Maid";
		
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
		
		//define the region for searching targets.
		AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(newX, newY, newZ, newX+1.0D, newY+1.0D, newZ+1.0D);
		//get entities.
		Entity entity = turtle.world.findNearestEntityWithinAABB(EntityLittleMaidBase.class, aabb, turtle.playerTurtle);
		if(entity == null) {
			return MTTurtleAccess.result(false, EFailureReason.NO_DIG_TARGET);
		}
		if(((EntityLittleMaidBase)entity).isTamed() != true) {
			return MTTurtleAccess.result(false, EFailureReason.NO_MAID);
		}
		
		//try to transfer.
		InventoryLittleMaid inventory = ((EntityLittleMaidBase)entity).inventory;
		inventory.markDirty();
		
		ItemStack tryStack = null;
		int selected = 0;
		for(int i=0; i < inventory.getSizeInventory(); ++i) {
			if(inventory.mainInventory[i] != null) {
				tryStack = inventory.mainInventory[i];
				selected = i;
				break;
			}
		}
		if(tryStack == null) {
			return MTTurtleAccess.result(false, EFailureReason.NO_SUCK);
		}
		
		ItemStack originalStack = tryStack.copy();
		if(turtle.storeItemStack(tryStack)) {
			inventory.mainInventory[selected] = null;
			
			return MTTurtleAccess.result(true);
		}
		else {
			if(originalStack.stackSize != tryStack.stackSize) {
				inventory.mainInventory[selected] = tryStack;
				
				return MTTurtleAccess.result(true);
			}
			else {
				return MTTurtleAccess.result(false, EFailureReason.NO_SPACE);
			}
		}
		
	}
	
	private TurtleCommandResult attack(MTTurtleAccess turtle, int dir) {

		int newX = turtle.posX + Facing.offsetsXForSide[dir];
		int newY = turtle.posY + Facing.offsetsYForSide[dir];
		int newZ = turtle.posZ + Facing.offsetsZForSide[dir];
		
		//define the region for searching targets.
		AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(newX, newY, newZ, newX+1.0D, newY+1.0D, newZ+1.0D);
		//get entities.
		Entity entity = turtle.world.findNearestEntityWithinAABB(EntityLittleMaidBase.class, aabb, turtle.playerTurtle);
		if(entity == null) {
			return MTTurtleAccess.result(false, EFailureReason.NO_ATTACK_TARGET);
		}
		if(((EntityLittleMaidBase)entity).isTamed() != true) {
			return MTTurtleAccess.result(false, EFailureReason.NO_MAID);
		}
		
		//get current slot.
		int currentSlot = turtle.getSelectedSlot();
		ItemStack currentItem = turtle.getSlotContents(currentSlot);
		if(currentItem == null) {
			return MTTurtleAccess.result(false, EFailureReason.NO_DROP);
		}
		
		//try to transfer.
		InventoryLittleMaid inventory = ((EntityLittleMaidBase)entity).inventory;
		inventory.markDirty();
		
		if(inventory.getFirstEmptyStack() >= 0) {
			inventory.addItemStackToInventory(currentItem);
			turtle.setSlotContents(currentSlot, null);
			//randomly consume in case of sugar.
			this.consumeSugar(currentItem, inventory, 2);
			
			return MTTurtleAccess.result(true);
		}
		else if(currentItem.getMaxStackSize() == 1) {
			return MTTurtleAccess.result(false, EFailureReason.FULL);
		}
		
		int firstSize = currentItem.stackSize;
		int currentSize = currentItem.stackSize;
		for(int j=0; j < inventory.getSizeInventory(); ++j) {
			if(inventory.mainInventory[j] == null) {
				continue;
			}
			if(inventory.mainInventory[j].getItem() == currentItem.getItem() &&
					inventory.mainInventory[j].getItemDamage() == currentItem.getItemDamage()) {
				int space = inventory.mainInventory[j].getMaxStackSize() - inventory.mainInventory[j].stackSize;
				if(space <= 0) {
					continue;
				}
				if(currentSize <= space) {
					inventory.mainInventory[j].stackSize += currentSize;
					currentSize = 0;
					break;
				}
				else {
					inventory.mainInventory[j].stackSize = inventory.mainInventory[j].getMaxStackSize();
					currentSize -= space;
					if(currentSize == 0) {
						break;
					}
				}
			}
		}
			
		if(currentSize == 0) {
			turtle.setSlotContents(currentSlot, null);
			//randomly consume in case of sugar.
			this.consumeSugar(currentItem, inventory, 2);
			
			return MTTurtleAccess.result(true);
		}
		else if(currentSize == firstSize) {
			return MTTurtleAccess.result(false, EFailureReason.FULL);
		}
		else {
			turtle.setSlotContents(currentSlot,
					new ItemStack(currentItem.getItem(), currentSize, currentItem.getItemDamage()));
			//randomly consume in case of sugar.
			this.consumeSugar(currentItem, inventory, 2);
			
			return MTTurtleAccess.result(true);
		}
		
	}
	
	private void consumeSugar(ItemStack currentItem, InventoryLittleMaid inventory, int provability) {
		
		Random rand = new Random();
		
		if(currentItem.getItem() == Items.sugar && rand.nextInt(provability) == 0) {
			for(int j=0; j < inventory.getSizeInventory(); ++j) {
				if(inventory.mainInventory[j] == null) {
					continue;
				}
				if(inventory.mainInventory[j].getItem() == Items.sugar) {
					inventory.mainInventory[j].stackSize -= 1;
					break;
				}
			}
		}
		
	}
	
}
