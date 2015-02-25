package noki.moreturtles.turtle.peripheral;

import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.util.IIcon;
import noki.moreturtles.MoreTurtlesData;
import noki.moreturtles.blocks.BlockExtendedBlocks;
import noki.moreturtles.blocks.RegisterBlocks;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import dan200.computercraft.api.turtle.TurtleVerb;


/**********
 * @class PeripheralDanmaku
 *
 * @description Danmaku Turtleを定義するクラスです。
 * @description_en Class of Danmaku Turtle.
 */
public class PeripheralDanmaku implements ITurtleUpgrade {
	
	//******************************//
	// define member variables.
	//******************************//
	private int upgradeMeta = 15;
	private Block upgradeBlock = RegisterBlocks.extendedBlocks;
	private ItemStack upgradeItem = new ItemStack(upgradeBlock, 1, upgradeMeta);
	
	
	//******************************//
	// define member methods.
	//******************************//
	@Override
	public int getUpgradeID() {
		
		return MoreTurtlesData.danmakuTurtlePID;
		
	}
 
	@Override
	public String getUnlocalisedAdjective() {
		
		return "Danmaku";
		
	}

	@Override
	public TurtleUpgradeType getType() {
		
		return  TurtleUpgradeType.Peripheral;
		
	}
 
	@Override
	public ItemStack getCraftingItem() {
		
		return upgradeItem;
		
	}
 
	@Override
	public IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
		
		return new PeripheralDanmakuHosted(turtle, side);
		
	}
	  
	@Override
	public IIcon getIcon(ITurtleAccess turtle, TurtleSide side) {
				
		return ((BlockExtendedBlocks)this.upgradeBlock).getEachExtendedIcon(this.upgradeMeta);
		
	}
 
	@Override
	public TurtleCommandResult useTool(ITurtleAccess turtle, TurtleSide side, TurtleVerb verb, int direction) {
		
		return null;
		
	}

	@Override
	public void update(ITurtleAccess turtle, TurtleSide side) {
		
	}
 
}
