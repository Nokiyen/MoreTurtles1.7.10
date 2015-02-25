package noki.moreturtles.turtle.peripheral;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldServer;
import noki.moreturtles.MoreTurtlesData;
import noki.moreturtles.turtle.common.EFailureReason;
import noki.moreturtles.turtle.common.MTTurtleAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleCommand;
import dan200.computercraft.api.turtle.TurtleCommandResult;


/**********
 * @class PeripheralDimensionalCommand
 *
 * @description Dimensional Turtleのマンドを定義するクラスです。
 * @description_en Class of the command of Dimensional Turtle.
 */
public class PeripheralDimensionalCommand implements ITurtleCommand {
	
	//******************************//
	// define member variables.
	//******************************//
	MTTurtleAccess turtle;
	int dimensionID;

	
	//******************************//
	// define member methods.
	//******************************//
	public PeripheralDimensionalCommand(MTTurtleAccess turtle, int dimensionID) {
		
		this.turtle = turtle;
		this.dimensionID = dimensionID;
		
	}

	@Override
	public TurtleCommandResult execute(ITurtleAccess givenTurtle) {
		
		//check portal block.
		int newX = 0;
		int newY = 0;
		int newZ = 0;
		Block block = null;
		String className = null;
		boolean flag1 = false;
		for(int i=0; i<6; i++) {
			newX = this.turtle.posX + Facing.offsetsXForSide[i];
			newY = this.turtle.posY + Facing.offsetsYForSide[i];
			newZ = this.turtle.posZ + Facing.offsetsZForSide[i];
			block = this.turtle.world.getBlock(newX, newY, newZ);
			className = block.getClass().getSimpleName();
			
			if(block.getMaterial() == Material.portal || className.indexOf("portal") != -1 || className.indexOf("Portal") != -1) {
				flag1 = true;
				break;
			}
		}
		if(flag1 == false) {
			return MTTurtleAccess.result(false, EFailureReason.NO_TRAVEL);
		}
		
		//check destination.
		if(this.turtle.world.provider.dimensionId == this.dimensionID) {
			return MTTurtleAccess.result(false, EFailureReason.NO_TRAVEL);
		}
		MinecraftServer minecraftServer = MinecraftServer.getServer();
		WorldServer currentWorld;
		WorldServer nextWorld;
		try {
			currentWorld = minecraftServer.worldServerForDimension(this.turtle.world.provider.dimensionId);
			nextWorld = minecraftServer.worldServerForDimension(this.dimensionID);
		}
		catch (Exception e){
			return MTTurtleAccess.result(false, EFailureReason.NO_DIM);
		}
		if(currentWorld == null || nextWorld == null) {
			return MTTurtleAccess.result(false, EFailureReason.NO_DIM);
		}
		
		int nextWorldX = 0;
		int nextWorldY = 0;
		int nextWorldZ = 0;
		
		if(block == Blocks.end_portal) {
			ChunkCoordinates coords;
			if(this.turtle.world.provider.dimensionId == 0 && this.dimensionID == 1) {
				coords = nextWorld.getEntrancePortalLocation();
				nextWorldX = coords.posX;
				nextWorldY = coords.posY+2;
				nextWorldZ = coords.posZ;
			}
			else if(this.turtle.world.provider.dimensionId == 1 && this.dimensionID == 0){
				coords = nextWorld.getSpawnPoint();
				nextWorldX = coords.posX;
				nextWorldY = coords.posY;
				nextWorldZ = coords.posZ;
				
				if(nextWorld.getBlock(nextWorldX, nextWorldY, nextWorldZ) != Blocks.air) {
					return MTTurtleAccess.result(false, EFailureReason.NO_TRAVEL);
				}
			}
			else {
				return MTTurtleAccess.result(false, EFailureReason.NO_TRAVEL);				
			}
		}
		else {
			double moveFactor = currentWorld.provider.getMovementFactor() / nextWorld.provider.getMovementFactor();
			int nextWorldStartX = MathHelper.floor_double(this.turtle.posX * moveFactor);
			int nextWorldStartZ = MathHelper.floor_double(this.turtle.posZ * moveFactor);
	
			boolean flag2 = false;
			search:
			for(int i = nextWorldStartX-128; i < nextWorldStartX + 128; ++i) {
				for(int j = nextWorldStartZ-128; j < nextWorldStartZ + 128; ++j) {
					for(int k = nextWorld.getActualHeight() - 1; k >= 0; --k) {
						if(nextWorld.getBlock(i, k, j) == block) {
							nextWorldX = i;
							nextWorldZ = j;
							nextWorldY = k;
							while(nextWorld.getBlock(i, k-1, j) == block) {
								--nextWorldY;
								--k;
							}
							flag2 = true;
							break search;
						}
					}
				}
			}
			if(flag2 == false) {
				return MTTurtleAccess.result(false, EFailureReason.NO_TRAVEL);
			}
			
			boolean flag3 = false;
			int checkX = 0;
			int checkY = 0;
			int checkZ = 0;
			Block checkBlock;
			for(int i=0; i<6; i++) {
				checkX = nextWorldX + Facing.offsetsXForSide[i];
				checkY = nextWorldY + Facing.offsetsYForSide[i];
				checkZ = nextWorldZ + Facing.offsetsZForSide[i];
				checkBlock = nextWorld.getBlock(checkX, checkY, checkZ);
				if(checkBlock == Blocks.air) {
					nextWorldX = nextWorldX + Facing.offsetsXForSide[i];
					nextWorldY = nextWorldY + Facing.offsetsYForSide[i];
					nextWorldZ = nextWorldZ + Facing.offsetsZForSide[i];
					flag3 = true;
					break;
				}
			}
			if(flag3 == false) {
				return MTTurtleAccess.result(false, EFailureReason.NO_TRAVEL);
			}
		}
		
		if(this.turtle.consumeFuel(MoreTurtlesData.consumedFuelLevel*10) == false) {
			return MTTurtleAccess.result(false, EFailureReason.NO_FUEL);
		}

		this.turtle.teleportTo(nextWorld, nextWorldX, nextWorldY, nextWorldZ);
		
		return MTTurtleAccess.result(true);
		
	}

}
