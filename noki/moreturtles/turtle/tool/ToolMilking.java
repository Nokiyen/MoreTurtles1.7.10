package noki.moreturtles.turtle.tool;
 
import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityMooshroom;
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
 * @class TurtleMilking
 *
 * @description Milking Turtleを定義するクラスです。
 * @description_en Class of Milking Turtle.
 */
public class ToolMilking implements ITurtleUpgrade {
	
	//******************************//
	// define member variables.
	//******************************//
	public int upgradeMeta = 4;
	public ItemStack upgradeItem = new ItemStack(RegisterItems.extendedItems, 1, this.upgradeMeta);
	
	
	//******************************//
	// define member methods.
	//******************************//
	@Override
	public int getUpgradeID() {
		
		return MoreTurtlesData.milkingTurtleTID;
		
	}
 
	@Override
	public String getUnlocalisedAdjective() {
		
		return "Milking";
		
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
		@SuppressWarnings("rawtypes")
		List list = turtle.world.getEntitiesWithinAABBExcludingEntity(turtle.playerTurtle, aabb);
		if(list == null || list.size() == 0) {
			return MTTurtleAccess.result(false, EFailureReason.NO_DIG_TARGET);
		}
		
		//get current slot.
		int currentSlot = turtle.getSelectedSlot();
		ItemStack currentItem = turtle.getSlotContents(currentSlot);
		if(currentItem == null) {
			return MTTurtleAccess.result(false, EFailureReason.NO_ITEM);
		}
		
		//try to get milk or stew.
		for(Object each: list) {
			if(each != null) {
				if(each instanceof EntityCow && currentItem.getItem() == Items.bucket) {
					turtle.consume(currentSlot);
					turtle.store(new ItemStack(Items.milk_bucket));
					
					return MTTurtleAccess.result(true);
				}
				if(each instanceof EntityMooshroom && currentItem.getItem() == Items.bowl) {
					turtle.consume(currentSlot);
					turtle.store(new ItemStack(Items.mushroom_stew));
					
					return MTTurtleAccess.result(true);
				}
			}
		}
		
		return MTTurtleAccess.result(false, EFailureReason.NO_ITEM);
		
	}
	
	private TurtleCommandResult attack(MTTurtleAccess turtle, int dir) {

		return MTTurtleAccess.result(false, EFailureReason.NO_ATTACK);
		
	}
 
}
