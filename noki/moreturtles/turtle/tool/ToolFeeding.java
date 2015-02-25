package noki.moreturtles.turtle.tool;
 
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.entity.passive.EntityAnimal;
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
 * @class TurtleFeeding
 *
 * @description Feeding Turtleを定義するクラスです。
 * @description_en Class of Feeding Turtle.
 */
public class ToolFeeding implements ITurtleUpgrade {
	
	//******************************//
	// define member variables.
	//******************************//
	private int upgradeMeta = 3;
	private ItemStack upgradeItem = new ItemStack(RegisterItems.extendedItems, 1, this.upgradeMeta);
	
	
	//******************************//
	// define member methods.
	//******************************//
	@Override
	public int getUpgradeID() {
		
		return MoreTurtlesData.feedingTurtleTID;
		
	}
 
	@Override
	public String getUnlocalisedAdjective() {
		
		return "Feeding";
		
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
		
		return MTTurtleAccess.result(false, EFailureReason.NO_DIG);
		
	}
	
	private TurtleCommandResult attack(MTTurtleAccess turtle, int dir) {

		int newX = turtle.posX + Facing.offsetsXForSide[dir];
		int newY = turtle.posY + Facing.offsetsYForSide[dir];
		int newZ = turtle.posZ + Facing.offsetsZForSide[dir];
		
		//define the region for searching targets.
//		AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(newX-0.5, newY-0.5, newZ-0.5, newX+0.5, newY+0.5, newZ+0.5);
		AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(newX, newY, newZ, newX+1.0D, newY+1.0D, newZ+1.0D);
		//get entities.
		@SuppressWarnings("rawtypes")
		List list = turtle.world.getEntitiesWithinAABBExcludingEntity(turtle.playerTurtle, aabb);
		if(list == null || list.size() == 0) {
			return MTTurtleAccess.result(false, EFailureReason.NO_ATTACK_TARGET);
		}
		
		//get current slot.
		int currentSlot = turtle.getSelectedSlot();
		ItemStack currentItem = turtle.getSlotContents(currentSlot);
		if(currentItem == null) {
			return MTTurtleAccess.result(false, EFailureReason.NO_ITEM);
		}
		
		//try to feed.
		for(Object each: list) {
			if(each != null && each instanceof EntityAnimal) {
				EntityAnimal target = (EntityAnimal)each;
				if(target.isBreedingItem(currentItem) && target.getGrowingAge() == 0 && target.isInLove() == false) {
					turtle.consume(currentSlot);
					target.func_146082_f(turtle.playerTurtle);
					
					return MTTurtleAccess.result(true);
				}
			}
		}

		return MTTurtleAccess.result(false, EFailureReason.NO_ITEM);
		
	}
 
}
