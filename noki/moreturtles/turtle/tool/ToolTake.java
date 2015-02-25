package noki.moreturtles.turtle.tool;

import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
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
 * @class ToolTake
 *
 * @description Take Turtleを定義するクラスです。
 * @description_en Class of Take Turtle.
 * 
 * @caution 竹modのバージョンアップで各種不具合が修正されたので、凍結。
 * @caution_en This turtles is outed because of the version up and bug fixes of Bamboo Mod.
 */
public class ToolTake implements ITurtleUpgrade {

	//******************************//
	// define member variables.
	//******************************//
	public int upgradeMeta = 15;
	public ItemStack upgradeItem = new ItemStack(RegisterItems.extendedItems, 1, this.upgradeMeta);
	
	
	//******************************//
	// define member methods.
	//******************************//
	@Override
	public int getUpgradeID() {
		
		return MoreTurtlesData.takeTurtleTID;
		
	}
 
	@Override
	public String getUnlocalisedAdjective() {
		
		return "Take";
		
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
				
		return MTTurtleAccess.result(false, EFailureReason.UNDEFINED);
		
	}
	
	private TurtleCommandResult attack(MTTurtleAccess turtle, int dir) {

		return MTTurtleAccess.result(false, EFailureReason.UNDEFINED);
		
	}
	
}
