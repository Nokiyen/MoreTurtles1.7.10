package noki.moreturtles.turtle.tool;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import noki.moreturtles.MoreTurtlesData;
import noki.moreturtles.items.ItemExtendedItems;
import noki.moreturtles.items.RegisterItems;
import noki.moreturtles.other.HelperFishing;
import noki.moreturtles.turtle.common.MTTurtleAccess;
import noki.moreturtles.turtle.common.EFailureReason;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleUpgrade;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.api.turtle.TurtleSide;
import dan200.computercraft.api.turtle.TurtleUpgradeType;
import dan200.computercraft.api.turtle.TurtleVerb;


/**********
 * @class TurtleFishery
 *
 * @description Fishery Turtleを定義するクラスです。
 * @description_en Class of Fishery Turtle.
 */
public class ToolFishery implements ITurtleUpgrade {
	
	//******************************//
	// define member variables.
	//******************************//
	private int upgradeMeta = 5;
	private ItemStack upgradeItem = new ItemStack(RegisterItems.extendedItems, 1, this.upgradeMeta);
	
	private boolean start = false;
	private int direction = 0;
	private long startTime;
	
	private int luckLevel = 0;
	private int addedFuelLevel = 0;
	
	
	//******************************//
	// define member methods.
	//******************************//
	@Override
	public int getUpgradeID() {
		
		return MoreTurtlesData.fisheryTurtleTID;
		
	}
 
	@Override
	public String getUnlocalisedAdjective() {
		
		return "Fishery";
		
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
		
		if(this.start == false) {
			this.direction = -1;//reset.
			this.startTime = 0;//reset.
			return MTTurtleAccess.result(false, EFailureReason.NO_CAST);
		}
		this.start = false;//reset.
		
		if(this.direction != dir) {
			this.direction = -1;//reset.
			this.startTime = 0;//reset.
			return MTTurtleAccess.result(false, EFailureReason.WRONG_DIR);
		}
		this.direction = -1;//reset.
		
		int newX = turtle.posX + Facing.offsetsXForSide[dir];
		int newY = turtle.posY + Facing.offsetsYForSide[dir];
		int newZ = turtle.posZ + Facing.offsetsZForSide[dir];
		
		if(this.checkWater(turtle.world, newX, newY, newZ) == 0) {
			this.startTime = 0;//reset.
			return MTTurtleAccess.result(false, EFailureReason.NO_WATER);
		}
		
		//check waters in the 9*9*9 area and calculate the provability about waters.
		int waterCount = 0;
		for(int i = turtle.posX-4; i <= turtle.posX+4; i++) {
			for(int j = turtle.posY-4; j <= turtle.posY+4; j++) {
				for(int k = turtle.posZ-4; k <= turtle.posZ+4; k++) {
					waterCount += this.checkWater(turtle.world, i, j, k);
				}
			}
		}
		double waterValue = waterCount / (9*9*9-1);
		
		//calculate the provability about time;
		long currentTime = turtle.world.getTotalWorldTime();
		long passedTime = currentTime - this.startTime;
		this.startTime = 0;//reset.
		if(passedTime == 0) {
			return MTTurtleAccess.result(false, EFailureReason.NO_FISH);
		}
		double timeValue = passedTime/400;
		
		//check the final provability.
		double totalValue = waterValue + timeValue;
		double targetProv = Math.abs(turtle.world.rand.nextGaussian());
		if(targetProv <= totalValue) {
			//need fuel.
			if(turtle.consumeFuel(MoreTurtlesData.consumedFuelLevel + this.addedFuelLevel) == false) {
				return MTTurtleAccess.result(false, EFailureReason.NO_FUEL);
			}
			
			turtle.store(HelperFishing.getFishable(turtle.world, this.luckLevel, 0));
			return MTTurtleAccess.result(true);
		}
		
		return MTTurtleAccess.result(false, EFailureReason.NO_FISH);
		
	}
	
	private TurtleCommandResult attack(MTTurtleAccess turtle, int dir) {
		
		int newX = turtle.posX + Facing.offsetsXForSide[dir];
		int newY = turtle.posY + Facing.offsetsYForSide[dir];
		int newZ = turtle.posZ + Facing.offsetsZForSide[dir];
		
		if(this.checkWater(turtle.world, newX, newY, newZ) == 0) {
			return MTTurtleAccess.result(false, EFailureReason.NO_WATER);
		}
		
		this.start = true;
		this.direction = dir;
		this.startTime = turtle.world.getTotalWorldTime();

		return MTTurtleAccess.result(true);
		
	}
	
	private int checkWater(World world, int x, int y, int z) {
		
		Block block = world.getBlock(x, y, z);
		if(block == Blocks.water || block == Blocks.flowing_water) {
			return 1;
		}
		
		return 0;
		
	}
	
	public void setLuckLevel(int level) {
		
		this.luckLevel = level;
		
	}
	
	public void setAddedFuelLevel(int level) {
		
		this.addedFuelLevel = level;
		
	}
	 
}
