package noki.moreturtles.turtle.peripheral;

import thKaguyaMod.THShotLib;
import noki.moreturtles.turtle.common.MTTurtleAccess;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleCommand;
import dan200.computercraft.api.turtle.TurtleCommandResult;


/**********
 * @class PeripheralDanmakuCommand
 *
 * @description
 * @description_en
 */
public class PeripheralDanmakuCommandRemove implements ITurtleCommand {
	
	//******************************//
	// define member variables.
	//******************************//
	MTTurtleAccess turtle;
	double range;

	
	//******************************//
	// define member methods.
	//******************************//
	public PeripheralDanmakuCommandRemove(MTTurtleAccess turtle, double range) {
		
		this.turtle = turtle;
		this.range = range;
		
	}
	
	@Override
	public TurtleCommandResult execute(ITurtleAccess turtle) {
		
		THShotLib.danmakuRemove(this.turtle.playerTurtle, this.range, "ALL", true);
		return MTTurtleAccess.result(true);

	}
	
}
