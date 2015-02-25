package noki.moreturtles.turtle.peripheral;

import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import noki.moreturtles.MoreTurtlesData;
import noki.moreturtles.turtle.common.EFailureReason;
import noki.moreturtles.turtle.common.MTTurtleAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleCommand;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.api.turtle.TurtleSide;


/**********
 * @class PeripheralFortuneCommand
 *
 * @description Fortune Turtleのdig系コマンドを定義するクラスです。
 * @description_en Class of the dig-type commands of Fortune Turtle.
 */
public class PeripheralFortuneCommand implements ITurtleCommand {
	
	//******************************//
	// define member variables.
	//******************************//
	private TurtleSide side;
	private int dir;
	private static String[] allowedTool = {
		MoreTurtlesData.cc_Axe,
		MoreTurtlesData.cc_Hoe,
		MoreTurtlesData.cc_Pickaxe,
		MoreTurtlesData.cc_Shovel
	};

	
	//******************************//
	// define member methods.
	//******************************//
	public PeripheralFortuneCommand(TurtleSide side, int dir) {
		
		this.side = side;
		this.dir = dir;
		
	}

	@Override
	public TurtleCommandResult execute(ITurtleAccess givenTurtle) {
		
		MTTurtleAccess turtle = new MTTurtleAccess(givenTurtle);
		
		//check the other side upgrade.
		if(!this.checkTool(turtle, this.side)) {
			return MTTurtleAccess.result(false, EFailureReason.NO_DIG_TOOL);
		}
		
		//try to dig.
		//set startup info.
		int newX = turtle.posX + Facing.offsetsXForSide[this.dir];
		int newY = turtle.posY + Facing.offsetsYForSide[this.dir];
		int newZ = turtle.posZ + Facing.offsetsZForSide[this.dir];
		Block block = turtle.world.getBlock(newX, newY, newZ);
		int metadata = turtle.world.getBlockMetadata(newX, newY, newZ);
		ItemStack item = turtle.getOtherUpgradeItem(this.side);
		
		//check whether the block is air
		if(turtle.world.isAirBlock(newX, newY, newZ)) {
			return MTTurtleAccess.result(false, EFailureReason.NO_DIG_TARGET);
		}
		//check the block hardness.
		if(block.getBlockHardness(turtle.world, newX, newY, newZ) <= -1.0F) {
			return MTTurtleAccess.result(false, EFailureReason.NO_DIG_TARGET);
		}
		//check fuel level.
		if(turtle.consumeFuel(MoreTurtlesData.consumedFuelLevel) == false) {
			return MTTurtleAccess.result(false, EFailureReason.NO_FUEL);
		}
		
		//dig.
		if(turtle.canTurtleHarvest(item, block, metadata)) {
			ArrayList<ItemStack> items = block.getDrops(turtle.world, newX, newY, newZ, metadata, 3);
			if(items != null) {
				for(ItemStack each : items) {
					turtle.store(each);
				}
			}
		}
		turtle.playAnimation(MTTurtleAccess.getOtherSwingAnimation(this.side));
		turtle.world.setBlockToAir(newX, newY, newZ);
		turtle.world.playAuxSFX(2001, newX, newY, newZ, Block.getIdFromBlock(block) + metadata * 4096);

		return MTTurtleAccess.result(true);

	}
	
	private boolean checkTool(MTTurtleAccess turtle, TurtleSide side) {
		
		if(turtle.getOtherUpgrade(side) == null) {
			return false;
		}
		
		return Arrays.asList(allowedTool).contains(turtle.getOtherUpgradeName(side));
		
	}

}
