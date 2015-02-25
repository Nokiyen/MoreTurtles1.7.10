package noki.moreturtles.turtle.peripheral;

import java.util.ArrayList;
import java.util.Arrays;

import tsuteto.tofu.material.TcMaterial;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleCommand;
import dan200.computercraft.api.turtle.TurtleCommandResult;
import dan200.computercraft.api.turtle.TurtleSide;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraftforge.common.IShearable;
import noki.moreturtles.MoreTurtlesData;
import noki.moreturtles.turtle.common.EFailureReason;
import noki.moreturtles.turtle.common.MTTurtleAccess;


/**********
 * @class PeripheralSilkTouchCommand
 *
 * @description SilkTouch Turtleのdig系コマンドを定義するクラスです。
 * @description_en Class of the dig-type commands of SilkTouch Turtle.
 */
public class PeripheralSilkTouchCommand implements ITurtleCommand{
	
	//******************************//
	// define member variables.
	//******************************//
	private TurtleSide side;
	private int dir;
	private static String[] allowedTool = {
		MoreTurtlesData.cc_Axe,
		MoreTurtlesData.cc_Hoe,
		MoreTurtlesData.cc_Pickaxe,
		MoreTurtlesData.cc_Shovel,
		"Shearing",
		"Tofu"
	};


	//******************************//
	// define member methods.
	//******************************//	
	public PeripheralSilkTouchCommand(TurtleSide side, int dir) {
		
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
		int newX = turtle.posX + Facing.offsetsXForSide[dir];
		int newY = turtle.posY + Facing.offsetsYForSide[dir];
		int newZ = turtle.posZ + Facing.offsetsZForSide[dir];
		Block block = turtle.world.getBlock(newX, newY, newZ);
		int metadata = turtle.world.getBlockMetadata(newX, newY, newZ);
		ItemStack item = turtle.getOtherUpgradeItem(this.side);
		String currentUpgradeName = turtle.getOtherUpgradeName(this.side);
		
		//check whether the block is air
		if(turtle.world.isAirBlock(newX, newY, newZ)) {
			return MTTurtleAccess.result(false, EFailureReason.NO_DIG_TARGET);
		}			
		//check the block harness.
		if(block.getBlockHardness(turtle.world, newX, newY, newZ) <= -1.0F) {
			return MTTurtleAccess.result(false, EFailureReason.NO_DIG_TARGET);
		}
		
		//case of Tofu.
		if(currentUpgradeName == "Tofu") {
			//check whether it is tofu.
			if(block.getMaterial() != TcMaterial.tofu) {
				return MTTurtleAccess.result(false, EFailureReason.NO_DIG_TARGET);
			}
			//check fuel level.
			if(turtle.consumeFuel(MoreTurtlesData.consumedFuelLevel) == false) {
				return MTTurtleAccess.result(false, EFailureReason.NO_FUEL);
			}
			
			ItemStack droppedItem = new ItemStack(block, 1, metadata);
			if(droppedItem != null) {
				turtle.store(droppedItem);
			}
			turtle.playAnimation(MTTurtleAccess.getOtherSwingAnimation(this.side));
			turtle.world.setBlockToAir(newX, newY, newZ);
			turtle.world.playAuxSFX(2001, newX, newY, newZ, Block.getIdFromBlock(block) + metadata * 4096);
			
			return MTTurtleAccess.result(true);
		}
		else {
			//check weak upgrade.
			if(currentUpgradeName == "Shearing") {
				item = new ItemStack(Items.shears);
				if(!(block instanceof IShearable) &&
						item.getItem().getDigSpeed(item, block, metadata) <= 1.0F) {
					return MTTurtleAccess.result(false, EFailureReason.NO_DIG_TARGET);
					
				}
			}
			//check fuel level.
			if(turtle.consumeFuel(MoreTurtlesData.consumedFuelLevel) == false) {
				return MTTurtleAccess.result(false, EFailureReason.NO_FUEL);
			}
			
			if(turtle.canTurtleHarvest(item, block, metadata)) {
				if(block.canSilkHarvest(turtle.world, turtle.playerTurtle, newX, newY, newZ, metadata)) {
					ItemStack droppedItem = new ItemStack(block, 1, metadata);
					if(droppedItem != null) {
						turtle.store(droppedItem);
					}
				}
				else if(currentUpgradeName == "Shearing" && block instanceof IShearable) {
					ArrayList<ItemStack> items = ((IShearable)block).onSheared(item, turtle.world, newX, newY, newZ, 0);
					if(items != null) {
						for(ItemStack each : items) {
							turtle.store(each);
						}
					}
				}
				else {
					ArrayList<ItemStack> items = block.getDrops(turtle.world, newX, newY, newZ, metadata, 0);
					if(items != null) {
						for(ItemStack each : items) {
							turtle.store(each);
						}
					}
				}
			}
			turtle.playAnimation(MTTurtleAccess.getOtherSwingAnimation(this.side));
			turtle.world.setBlockToAir(newX, newY, newZ);
			turtle.world.playAuxSFX(2001, newX, newY, newZ, Block.getIdFromBlock(block) + metadata * 4096);

			return MTTurtleAccess.result(true);
		}

	}
	
	public boolean checkTool(MTTurtleAccess turtle, TurtleSide side) {
		
		if(turtle.getOtherUpgrade(this.side) == null) {
			return false;
		}
		
		return Arrays.asList(allowedTool).contains(turtle.getOtherUpgradeName(this.side));
		
	}

}
