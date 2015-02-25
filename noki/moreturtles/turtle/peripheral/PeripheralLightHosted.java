package noki.moreturtles.turtle.peripheral;

import java.util.Arrays;

import net.minecraft.util.Facing;
import net.minecraft.world.EnumSkyBlock;
import noki.moreturtles.other.HelperArgs;
import noki.moreturtles.turtle.common.EFailureReason;
import noki.moreturtles.turtle.common.MTTurtleAccess;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;


/**********
 * @class PeripheralLightHosted
 *
 * @description Light Detecting Turtleの周辺機器部分を定義するクラスです。
 * @description_en Class of the peripheral of Light Detecting Turtle.
 */
public class PeripheralLightHosted implements IPeripheral {
	
	//******************************//
	// define member variables.
	//******************************//
	private MTTurtleAccess turtle;
	@SuppressWarnings("unused")
	private TurtleSide side;
	
	private static String[] dirs = new String[] {"bottom", "top", "front", "back", "left", "right"};

	
	//******************************//
	// define member methods.
	//******************************//
	public PeripheralLightHosted(ITurtleAccess turtle, TurtleSide side) {

		this.turtle = new MTTurtleAccess(turtle);
		this.side = side;
		
	}

	@Override
	public String getType() {
		
		return "MoreTurtles";
		
	}

	@Override
	public String[] getMethodNames() {
		
		return new String[] {
				"getName",
				"detectLight",
				"detectSkyLight",
				"detectSkyLightRaw",
				"detectBlockLight", 
				"canSeeTheSky"
		};
		
	}
 
	@Override
	public void attach(IComputerAccess computer) {
 
	}

	@Override
	public void detach(IComputerAccess computer) {
		
	}
	
	@Override
	public boolean equals(IPeripheral other) {
		
		return false;
		
	}
 
	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments)
			throws LuaException, InterruptedException {

		this.turtle.setTurtleInfo();
		
		switch(method) {
			case 0:
				return this.getName();
			case 1:
			case 2:
			case 3:
			case 4:
				return this.detectLight(method, arguments);				
			case 5:
				return this.canSeeTheSky(arguments);
			default:
				return new Object[] {false, EFailureReason.UNKNOWN.getMessage()};
		}

	}
	
	private Object[] getName() {
		
		return new Object[] {"Light"};
		
	}
	
	private Object[] detectLight(int method, Object[] arguments) throws LuaException, InterruptedException {
		
		if(!HelperArgs.checkArguments(arguments, 1, new String[] {"string"})) {
			throw new LuaException(EFailureReason.LUA_WRONG_ARG.getMessage());
		}
		
		String dirName = HelperArgs.getString(arguments[0]);
		if(!Arrays.asList(dirs).contains(dirName)) {
			throw new LuaException(EFailureReason.LUA_WRONG_ARG.getMessage());
		}
		
		int dirNum = Arrays.asList(dirs).indexOf(dirName);
		int dir = 0;
		int turtleDir = this.turtle.getDirection();
		switch(dirNum) {
		case 0:	//bottom.
			dir = 0;
			break;
		case 1:	//top.
			dir = 1;
			break;
		case 2:	//front.
			dir = turtleDir;
			break;
		case 3:	//back.
			dir = Facing.oppositeSide[turtleDir];
			break;
		case 4:	//left.
			if(turtleDir == 2 || turtleDir == 3) {
				dir = turtleDir + 2;
			}
			else if(turtleDir == 4) {
				dir = 3;
			}
			else if(turtleDir == 5) {
				dir = 2;
			}
			break;
		case 5:	//right.
			if(turtleDir == 2) {
				dir = 5;
			}
			else if(turtleDir == 3) {
				dir = 4;
			}
			else if(turtleDir == 4 || turtleDir == 5) {
				dir = turtleDir -2;
			}
			break;
		default:
			dir = 0;
		}
		
		int newX = this.turtle.posX + Facing.offsetsXForSide[dir];
		int newY = this.turtle.posY + Facing.offsetsYForSide[dir];
		int newZ = this.turtle.posZ + Facing.offsetsZForSide[dir];
		
		//	return the light level for each method.
		int lightLevel = 0;
		switch(method) {
		case 1:
			lightLevel = this.turtle.world.getBlockLightValue(newX, newY, newZ);
			break;
		case 2:
			lightLevel = this.turtle.world.getSavedLightValue(EnumSkyBlock.Sky, newX, newY, newZ);
			lightLevel -= this.turtle.world.skylightSubtracted;
			if(lightLevel < 0) {
				lightLevel = 0;
			}
			break;
		case 3:
			lightLevel = this.turtle.world.getSavedLightValue(EnumSkyBlock.Sky, newX, newY, newZ);			
			break;
		case 4:
			lightLevel = this.turtle.world.getSavedLightValue(EnumSkyBlock.Block, newX, newY, newZ);
			break;
		}
		
		return new Object[] {lightLevel};
		
	}
	
	private Object[] canSeeTheSky(Object[] arguments) throws LuaException, InterruptedException {
		
		boolean result  = this.turtle.world.canBlockSeeTheSky(this.turtle.posX, this.turtle.posY, this.turtle.posZ);
		
		return new Object[] {result};
		
	}
	
}
