package noki.moreturtles.turtle.peripheral;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldSettings.GameType;
import noki.moreturtles.other.HelperArgs;
import noki.moreturtles.turtle.common.EFailureReason;
import noki.moreturtles.turtle.common.MTTurtleAccess;
import noki.moreturtles.turtle.peripheral.PeripheralDanmakuCommand.EDanmakuFormType;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.TurtleSide;
import thKaguyaMod.LaserData;
import thKaguyaMod.ShotData;
import thKaguyaMod.THShotLib;
import thKaguyaMod.init.THKaguyaConfig;
import thKaguyaMod.item.ItemTHLaser;
import thKaguyaMod.item.ItemTHShot;


/**********
 * @class PeripheralDanmakuHosted
 *
 * @description Danmaku Turtleの実際の動作などを定義するクラスです。
 */
public class PeripheralDanmakuHosted implements IPeripheral {
	
	//******************************//
	// define member variables.
	//******************************//
	private MTTurtleAccess turtle;
	@SuppressWarnings("unused")
	private TurtleSide side;
	
	private double relativePosX = 0;
	private double relativePosY = 0;
	private double relativePosZ = 0;
	private int dir = 0;
	//yawとpitchは角度。内部計算で弧度に変換される。
	private float yaw = 0;
	private float pitch = 0;
	
	private boolean setColor = false;
	private int color = 0;
	
	private int delay = 0;
	private int end = 80;
	
	private boolean setDamage = false;
	private float damage;
	
	private boolean setSpeed = false;
	private double firstSpeed = 0.0D;
	private double limitSpeed = 0.0D;
	private double acceleration = 0.0D;
	
	private float rotationX = 0.0F;
	private float rotationY = 0.0F;
	private float rotationZ = 0.0F;
	private float rotationSpeed = 0.0F;
	private int rotationEnd = 9999;
	
	private float gravityY = 0.0F;
	private float gravityX = 0.0F;
	private float gravityZ = 0.0F;
	
	private int gamemode = 0;
	
	private final int[] dirSet = {0, 2, 3, 1};
	
	private boolean stackFlag = false;
	private PeripheralDanmakuCommand command = null;
		
	
	//******************************//
	// define member methods.
	//******************************//
	//----------//
	// default turtle's methods.
	//----------//
	public PeripheralDanmakuHosted(ITurtleAccess turtle, TurtleSide side) {
		
		this.turtle = new MTTurtleAccess(turtle);
		this.side = side;
		
	}

	@Override
	public String getType() {
		
		return "MoreTurtles";
		
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
	public String[] getMethodNames() {
		
		return new String[] {
				"getName",
				
				"shoot",			//shoot one bullet.
				"shootWide", 		//shoot bullets in the shape of a fun.
				"shootCircle",		//shoot bullets in the shape of a circle.
				"shootRing",		//shoot bullets in the shape of a ring.
				"shootRandomRing",	//shoot bullets randomly in a ring.
				"shootSphere",		//shoot bullets in the shape of a sphere.
				"stack",			//stack bullets.
				"release",			//release bullets.
				"remove",			//remove the bullets in a range.
				
				"setColor",			//set bullet's color.
				"unsetColor",		//unset bullet's color.
				"setSpeed",			//set bullet's speed.
				"unsetSpeed",		//unset bullet's speed.
				"setRotation",		//set bullet's rotation.
				"setGravity",		//set bullet's gravity.
				"setDelay",			//set bullet's delay for emerging.
				"setEnd",			//set bullet's end time for vanishing.
				"setDamage",		//set bullet's damage.
				"unsetDamage",		//unset bullet's damage.
				
				"setPosition",		//set turtle's relative positions.
				"setAngle",			//set turtle's yaw and pitch.
				"setAngleToNearestPlayer",	//set turtle's angle to the nearest player.
				"getPositionOfNearestPlayer",
				"setGameMode",		//set turtle's game mode.
		};
		
	}
	
	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments)
			throws LuaException, InterruptedException {
		
		this.turtle.setTurtleInfo();
		this.dir = this.dirSet[this.turtle.getDirection()-2];
		this.applyPosition();
		this.applyAngle();
		this.applyGameMode();

		switch(method) {
			case 0:
				return this.getName();
			case 1:
				return this.shoot(context, arguments, EDanmakuFormType.SINGLE);
			case 2:
				return this.shoot(context, arguments, EDanmakuFormType.WIDE);
			case 3:
				return this.shoot(context, arguments, EDanmakuFormType.CIRCLE);
			case 4:
				return this.shoot(context, arguments, EDanmakuFormType.RING);
			case 5:
				return this.shoot(context, arguments, EDanmakuFormType.RANDOM_RING);
			case 6:
				return this.shoot(context, arguments, EDanmakuFormType.SPHERE);
			case 7:
				return this.stack();
			case 8:
				return this.release(context);
			case 9:
				return this.remove(context, arguments);
			case 10:
				return this.setColor(arguments);
			case 11:
				return this.unsetColor();
			case 12:
				return this.setSpeed(arguments);
			case 13:
				return this.unsetSpeed();
			case 14:
				return this.setRotation(arguments);
			case 15:
				return this.setGravity(arguments);
			case 16:
				return this.setDelay(arguments);
			case 17:
				return this.setEnd(arguments);
			case 18:
				return this.setDamage(arguments);
			case 19:
				return this.unsetDamage();
			case 20:
				return this.setPosition(arguments);
			case 21:
				return this.setAngle(arguments);
			case 22:
				return this.setAngleToNearestPlayer();
			case 23:
				return this.getPositionOfNearestPlayer();
			case 24:
				return this.setGameMode(arguments);
			default:
				return new Object[] {false, EFailureReason.UNKNOWN.getMessage()};
		}

	}
	//----------//
	// end
	//----------//

	//----------//
	// unique lua methods for Danmaku Turtle.
	//----------//
	private Object[] getName() {
		
		return new Object[] {"Danmaku"};
		
	}

	private Object[] shoot(ILuaContext context, Object[] arguments, EDanmakuFormType formType)
			throws LuaException, InterruptedException {
		
		//	check the item stack.
		int currentSlot = this.turtle.getSelectedSlot();
		ItemStack currentItem = this.turtle.getSlotContents(currentSlot);
		if(currentItem == null) {
			return new Object[] {false, EFailureReason.NO_ITEM.getMessage()};
		}
		else if(!(currentItem.getItem() instanceof ItemTHShot || currentItem.getItem() instanceof ItemTHLaser)) {
			return new Object[] {false, EFailureReason.NO_ITEM.getMessage()};
		}
		
		if(currentItem.getItem() instanceof ItemTHShot) {
			return this.shootBullet(currentItem, context, arguments, formType);
		}
		else {
			return this.shootLaser(currentItem, context, arguments, formType);
		}
	
	}
	
	private Object[] shootBullet(ItemStack stack, ILuaContext context, Object[] arguments, EDanmakuFormType formType)
			throws LuaException, InterruptedException {
		
		//	special arguments for each form type.
		int way = 1;
		double distance = 0.0D;	//never change.
		float span = 0.0F;
		float angle = 0.0F;
		
		//	check given arguments.
		switch(formType) {
			case SINGLE:	//no args.
				break;
			case WIDE:	//way, span, and angle.
			case RING:	//way, span, and angle.
				if(!HelperArgs.checkArguments(arguments, 1, new String[] {"number"})) {
					throw new LuaException(EFailureReason.LUA_WRONG_ARG.getMessage());
				}
				way = (int)Math.round(HelperArgs.getDouble(arguments[0]));
				
				if(arguments.length > 1 && HelperArgs.isType("number", arguments[1])) {
					span = HelperArgs.getDouble(arguments[1]).floatValue();
				}
				if(arguments.length > 2 && HelperArgs.isType("number", arguments[2])) {
					angle = HelperArgs.getDouble(arguments[2]).floatValue();
				}
				
				break;
			case CIRCLE:	//way, and angle.
			case SPHERE:	//way, and angle.
				if(!HelperArgs.checkArguments(arguments, 1, new String[] {"number"})) {
					throw new LuaException(EFailureReason.LUA_WRONG_ARG.getMessage());
				}
				way = (int)Math.round(HelperArgs.getDouble(arguments[0]));
	
				if(arguments.length > 1 && HelperArgs.isType("number", arguments[1])) {
					angle = HelperArgs.getDouble(arguments[1]).floatValue();
				}
				
				break;
			case RANDOM_RING:	//	way, and span.
				if(!HelperArgs.checkArguments(arguments, 1, new String[] {"number"})) {
					throw new LuaException(EFailureReason.LUA_WRONG_ARG.getMessage());
				}
				way = (int)Math.round(HelperArgs.getDouble(arguments[0]));
				
				if(arguments.length > 1 && HelperArgs.isType("number", arguments[1])) {
					span = HelperArgs.getDouble(arguments[1]).floatValue();
				}
				
				break;
		}
		
		//	check way.
		if(way < 1) {
			return new Object[] {true};
		}
		if(way > THKaguyaConfig.shotMaxNumber) {
			way = THKaguyaConfig.shotMaxNumber;
		}
		
		//	check the stack size.
		if(this.gamemode == 0 || this.turtle.world.getWorldInfo().getGameType() == GameType.SURVIVAL) {
			if(stack.stackSize < way) {
				return new Object[] {false, EFailureReason.NO_ITEM};
			}
			stack.stackSize -= way;
		}
		if(stack.stackSize == 0) {
			this.turtle.setSlotContents(this.turtle.getSelectedSlot(), null);
		}
		
		//	arguments for shoot.
		int bulletType = stack.getItemDamage();
		int bulletType2 = ItemTHShot.form[bulletType];
		
		int color = this.turtle.world.rand.nextInt(7);
		if(this.setColor) {
			color = this.color;
		}
		
		float damage = THShotLib.DAMAGE[bulletType2];
		if(this.setDamage) {
			damage = this.damage;
		}

		ShotData shot = ShotData.shot(bulletType2, color, THShotLib.SIZE[bulletType2], damage, this.delay, this.end, 0);

		Vec3 pos = THShotLib.pos(this.turtle.playerTurtle.posX, this.turtle.playerTurtle.posY, this.turtle.playerTurtle.posZ);
		Vec3 look = THShotLib.angle(this.turtle.playerTurtle.rotationYaw, this.turtle.playerTurtle.rotationPitch);
		
		Vec3 rotate = Vec3.createVectorHelper(this.rotationX, this.rotationY, this.rotationZ);

		double firstSpeed = ItemTHShot.speed[bulletType];
		double limitSpeed = firstSpeed;
		double acceleration = 0.0D;
		if(this.setSpeed) {
			firstSpeed = this.firstSpeed;
			limitSpeed = this.limitSpeed;
			acceleration = this.acceleration;
		}
		
		Vec3 gravity = THShotLib.gravity(this.gravityX, this.gravityY, this.gravityZ);
		
		//	shoot.
		if(this.stackFlag == false) {
			this.command = new PeripheralDanmakuCommand();
			this.command.addStack(
					formType,
					this.turtle.playerTurtle, pos, look,
					rotate, this.rotationSpeed, this.rotationEnd,
					firstSpeed, limitSpeed, acceleration,
					gravity, shot, null,
					way, distance, span, angle
					);

			this.turtle.executeCommand(context, command);
			this.command = null;
		}
		else {
			if(this.command == null) {
				this.command = new PeripheralDanmakuCommand();
			}
			this.command.addStack(
					formType,
					this.turtle.playerTurtle, pos, look,
					rotate, this.rotationSpeed, this.rotationEnd,
					firstSpeed, limitSpeed, acceleration,
					gravity, shot, null,
					way, distance, span, angle
					);
		}
				
		return new Object[] {true};
		
	}
	
	private Object[] shootLaser(ItemStack stack, ILuaContext context, Object[] arguments, EDanmakuFormType formType)
			throws LuaException, InterruptedException {
		
		//	special arguments for each form type.
		int way = 1;
		double distance = 0.0D;	//	never change.
		float span = 0.0F;
		float angle = 0.0F;
		
		//	check arguments.
		switch(formType) {
			case SINGLE:	//	no args.
				break;
			case WIDE:	//	way, and span.
			case RANDOM_RING:
				if(!HelperArgs.checkArguments(arguments, 1, new String[] {"number"})) {
					throw new LuaException(EFailureReason.LUA_WRONG_ARG.getMessage());
				}			
				way = (int)Math.round(HelperArgs.getDouble(arguments[0]));
	
				if(arguments.length > 1 && HelperArgs.isType("number", arguments[1])) {
					span = HelperArgs.getDouble(arguments[1]).floatValue();				
				}
				
				break;
			case CIRCLE:	//	way.
				if(!HelperArgs.checkArguments(arguments, 1, new String[] {"number"})) {
					throw new LuaException(EFailureReason.LUA_WRONG_ARG.getMessage());
				}			
				way = (int)Math.round(HelperArgs.getDouble(arguments[0]));
				
				break;
			case RING:	//	way, span, and angle.
				if(!HelperArgs.checkArguments(arguments, 1, new String[] {"number"})) {
					throw new LuaException(EFailureReason.LUA_WRONG_ARG.getMessage());
				}			
				way = (int)Math.round(HelperArgs.getDouble(arguments[0]));
	
				if(arguments.length > 1 && HelperArgs.isType("number", arguments[1])) {
					span = HelperArgs.getDouble(arguments[1]).floatValue();
				}
				if(arguments.length > 2 && HelperArgs.isType("number", arguments[2])) {
					angle = HelperArgs.getDouble(arguments[2]).floatValue();
				}
				
				break;
			case SPHERE:	// way, and angle.
				if(!HelperArgs.checkArguments(arguments, 1, new String[] {"number"})) {
					throw new LuaException(EFailureReason.LUA_WRONG_ARG.getMessage());
				}			
				way = (int)Math.round(HelperArgs.getDouble(arguments[0]));
	
				if(arguments.length > 1 && HelperArgs.isType("number", arguments[1])) {
					angle = HelperArgs.getDouble(arguments[1]).floatValue();
				}
				
				break;
		}
		
		
		//check way.
		if(way < 1) {
			return new Object[] {true};
		}
		if(way > THKaguyaConfig.laserMaxNumber) {
			way = THKaguyaConfig.laserMaxNumber;
		}
		
		//	check the stack size.
		if(this.gamemode == 0 || this.turtle.world.getWorldInfo().getGameType() == GameType.SURVIVAL) {
			if(stack.stackSize < way) {
				return new Object[] {false, EFailureReason.NO_ITEM};
			}
			stack.stackSize -= way;
		}
		if(stack.stackSize == 0) {
			this.turtle.setSlotContents(this.turtle.getSelectedSlot(), null);
		}
				
		//	arguments for shoot.
		int bulletType = stack.getItemDamage();
		@SuppressWarnings("unused")
		int bulletType2 = ItemTHLaser.form[bulletType];
		
		int color = this.turtle.world.rand.nextInt(7);
		if(this.setColor) {
			color = this.color;
		}
		
		float length = 5.0F;
		if(bulletType == 1) {
			length = 2.0F;
		}
		else if(bulletType == 2) {
			length = 8.0F;
		}
		
		float damage = 5.0F;
		if(this.setDamage) {
			damage = this.damage;
		}

		LaserData laser = LaserData.laser(color, 0.1F, length, damage, this.delay, this.end, 0);

		Vec3 pos = THShotLib.pos(this.turtle.playerTurtle.posX, this.turtle.playerTurtle.posY, this.turtle.playerTurtle.posZ);
		Vec3 look = THShotLib.angle(this.turtle.playerTurtle.rotationYaw, this.turtle.playerTurtle.rotationPitch);
		
		Vec3 rotate = Vec3.createVectorHelper(this.rotationX, this.rotationY, this.rotationZ);

		double firstSpeed = ItemTHLaser.speed[bulletType];
		double limitSpeed = firstSpeed;
		double acceleration = 0.0D;
		if(this.setSpeed) {
			firstSpeed = this.firstSpeed;
			limitSpeed = this.limitSpeed;
			acceleration = this.acceleration;
		}
		
		Vec3 gravity = THShotLib.gravity(this.gravityX, this.gravityY, this.gravityZ);
		
		//	shoot.
		if(this.stackFlag == false) {
			this.command = new PeripheralDanmakuCommand();
			this.command.addStack(
					formType,
					this.turtle.playerTurtle, pos, look,
					rotate, this.rotationSpeed, this.rotationEnd,
					firstSpeed, limitSpeed, acceleration,
					gravity, null, laser,
					way, distance, span, angle
					);

			this.turtle.executeCommand(context, command);
			this.command = null;
		}
		else {
			if(this.command == null) {
				this.command = new PeripheralDanmakuCommand();
			}
			this.command.addStack(
					formType,
					this.turtle.playerTurtle, pos, look,
					rotate, this.rotationSpeed, this.rotationEnd,
					firstSpeed, limitSpeed, acceleration,
					gravity, null, laser,
					way, distance, span, angle
					);
		}
		
		return new Object[] {true};
		
	}
	
	private Object[] stack() throws LuaException {
		
		this.stackFlag = true;
		this.command = null;
		
		return new Object[] {null};
		
	}
	
	private Object[] release(ILuaContext context) throws LuaException, InterruptedException {
		
		if(this.stackFlag == false || this.command == null) {
			this.stackFlag = false;
			this.command = null;
			return new Object[] {false, "No shoot stacks."};
		}
		
		this.turtle.executeCommand(context, command);
		this.stackFlag = false;
		this.command = null;
		
		return new Object[] {true};
		
	}
	
	private Object[] remove(ILuaContext context, Object[] arguments) throws LuaException, InterruptedException {
		
		if(!HelperArgs.checkArguments(arguments, 1, new String[] {"number"})) {
			throw new LuaException(EFailureReason.LUA_WRONG_ARG.getMessage());
		}
		
		int range = (int)Math.round(HelperArgs.getDouble(arguments[0]));
		this.turtle.executeCommand(context, new PeripheralDanmakuCommandRemove(this.turtle, range));
		
		return new Object[] {null};
		
	}
	
	private Object[] setColor(Object[] arguments) throws LuaException {
		
		if(!HelperArgs.checkArguments(arguments, 1, new String[] {"number"})) {
			throw new LuaException(EFailureReason.LUA_WRONG_ARG.getMessage());
		}
		
		int meta = (int)Math.round(HelperArgs.getDouble(arguments[0]));
		if(meta < 0 || meta > 7) {
			throw new LuaException(EFailureReason.LUA_WRONG_ARG.getMessage());
		}
		
		this.setColor = true;
		this.color = meta;
		
		return new Object[] {null};
		
	}
	
	private Object[] unsetColor() throws LuaException {
		
		this.setColor = false;
		this.color = 0;
		
		return new Object[] {null};
		
	}
	
	private Object[] setSpeed(Object[] arguments) throws LuaException {
		
		String[] types = new String[] {"number", "number", "number"};
		if(!HelperArgs.checkArguments(arguments, 3, types)) {
			throw new LuaException(EFailureReason.LUA_WRONG_ARG.getMessage());
		}
		
		double firstSpeed = HelperArgs.getDouble(arguments[0]);
		double limitSpeed = HelperArgs.getDouble(arguments[1]);
		double acceleration = HelperArgs.getDouble(arguments[2]);
		
		this.setSpeed = true;
		this.firstSpeed = firstSpeed;
		this.limitSpeed = limitSpeed;
		this.acceleration = acceleration;
		
		return new Object[] {null};
		
	}
	
	private Object[] unsetSpeed() throws LuaException {

		this.setSpeed = false;
		this.firstSpeed = 0.0D;
		this.limitSpeed = 0.0D;
		this.acceleration = 0.0D;
		
		return new Object[] {null};
		
	}
	
	private Object[] setRotation(Object[] arguments) throws LuaException {
		
		String[] types = new String[] {"number", "number", "number", "number", "number"};
		if(!HelperArgs.checkArguments(arguments, 5, types)) {
			throw new LuaException(EFailureReason.LUA_WRONG_ARG.getMessage());
		}
		
		float rotationX = HelperArgs.getDouble(arguments[0]).floatValue();
		float rotationY = HelperArgs.getDouble(arguments[1]).floatValue();
		float rotationZ = HelperArgs.getDouble(arguments[2]).floatValue();
		float rotationSpeed = HelperArgs.getDouble(arguments[3]).floatValue();
		int rotationEnd = (int)Math.round(HelperArgs.getDouble(arguments[4]));
		if(rotationEnd < 0 || rotationEnd > 9999) {
			throw new LuaException(EFailureReason.LUA_WRONG_ARG.getMessage());
		}
		
		this.rotationX = rotationX;
		this.rotationY = rotationY;
		this.rotationZ = rotationZ;
		this.rotationSpeed = rotationSpeed;
		this.rotationEnd = rotationEnd;
		
		return new Object[] {null};
		
	}
	
	private Object[] setGravity(Object[] arguments) throws LuaException {
		
		if(!HelperArgs.checkArguments(arguments, 3, new String[] {"number", "number", "number"})) {
			throw new LuaException(EFailureReason.LUA_WRONG_ARG.getMessage());
		}
		
		this.gravityX = HelperArgs.getDouble(arguments[0]).floatValue();
		this.gravityY = HelperArgs.getDouble(arguments[1]).floatValue();
		this.gravityZ = HelperArgs.getDouble(arguments[2]).floatValue();
			
		return new Object[] {null};
		
	}
	
	private Object[] setDelay(Object[] arguments) throws LuaException {
		
		if(!HelperArgs.checkArguments(arguments, 1, new String[] {"number"})) {
			throw new LuaException(EFailureReason.LUA_WRONG_ARG.getMessage());
		}
		
		this.delay = (int)Math.round(HelperArgs.getDouble(arguments[0]));
		
		return new Object[] {null};
		
	}
	
	private Object[] setEnd(Object[] arguments) throws LuaException {
		
		if(!HelperArgs.checkArguments(arguments, 1, new String[] {"number"})) {
			throw new LuaException(EFailureReason.LUA_WRONG_ARG.getMessage());
		}
		
		this.end = (int)Math.round(HelperArgs.getDouble(arguments[0]));
		
		return new Object[] {null};
		
	}
	
	private Object[] setDamage(Object[] arguments) throws LuaException {
		
		if(!HelperArgs.checkArguments(arguments, 1, new String[] {"number"})) {
			throw new LuaException(EFailureReason.LUA_WRONG_ARG.getMessage());
		}
		
		float damage = HelperArgs.getDouble(arguments[0]).floatValue();
		if(damage < 0) {
			throw new LuaException(EFailureReason.LUA_WRONG_ARG.getMessage());
		}
		
		this.setDamage = true;
		this.damage = damage;
		
		return new Object[] {null};
		
	}
	
	private Object[] unsetDamage() throws LuaException {
		
		this.setDamage = false;
		this.damage = 0.0F;
		
		return new Object[] {null};
		
	}
	
	private Object[] setPosition(Object[] arguments) throws LuaException {
		
		if(!HelperArgs.checkArguments(arguments, 3, new String[] {"number", "number", "number"})) {
			throw new LuaException(EFailureReason.LUA_WRONG_ARG.getMessage());
		}
		
		double givenX = HelperArgs.getDouble(arguments[0]);
		double givenY = HelperArgs.getDouble(arguments[1]);
		double givenZ = HelperArgs.getDouble(arguments[2]);
		if(Math.abs(givenX) > 50 || Math.abs(givenY) > 50 || Math.abs(givenZ) > 50) {
			throw new LuaException(EFailureReason.LUA_WRONG_ARG.getMessage());
		}
		
		this.relativePosX = givenX;
		this.relativePosY = givenY;
		this.relativePosZ = givenZ;
		
		return new Object[] {null};
		
	}
	
	private Object[] setAngle(Object[] arguments) throws LuaException {
		
		if(!HelperArgs.checkArguments(arguments, 2, new String[] {"number", "number"})) {
			throw new LuaException(EFailureReason.LUA_WRONG_ARG.getMessage());
		}
		
		this.yaw = MathHelper.wrapAngleTo180_float(HelperArgs.getDouble(arguments[0]).floatValue());
		this.pitch = MathHelper.wrapAngleTo180_float(HelperArgs.getDouble(arguments[1]).floatValue());
		
		return new Object[] {null};
		
	}
	
	private Object[] setAngleToNearestPlayer() throws LuaException {
				
		AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(
				this.turtle.posX-100.5, this.turtle.posY-50.5, this.turtle.posZ-100.5,
				this.turtle.posX+100.5, this.turtle.posY+50.5, this.turtle.posZ+100.5);

		Entity target = this.turtle.world.findNearestEntityWithinAABB(EntityPlayer.class, aabb, this.turtle.playerTurtle);
		if(target == null) {
			return new Object[] {false, "No entities"};
		}
		
		double paramX = target.posX - this.turtle.playerTurtle.posX;
		double paramY = target.posY - target.getYOffset() - this.turtle.playerTurtle.posY;
		double paramZ = target.posZ - this.turtle.playerTurtle.posZ;
//        double distXZ = (double)MathHelper.sqrt_double(paramX * paramX + paramZ * paramZ);
 //       double distXYZ = (double)MathHelper.sqrt_double(paramX*paramX + paramY*paramY + paramZ*paramZ);
		
        //Entity fairy から拝借。
		float angleXZ = (float)(-Math.atan2(paramX, paramZ) * 180.0D / Math.PI);
		float angleY  = 360F - (float)Math.atan2(paramY, Math.sqrt(paramX*paramX + paramZ*paramZ)) / 3.141593F*180F;
		
        this.yaw = angleXZ - (this.dir-2)*90;
        this.pitch = angleY;
        
		return new Object[] {true, this.yaw, this.pitch};
		
	}
	
	private Object[] getPositionOfNearestPlayer() throws LuaException {
		
		AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(
				this.turtle.posX-100.5, this.turtle.posY-50.5, this.turtle.posZ-100.5,
				this.turtle.posX+100.5, this.turtle.posY+50.5, this.turtle.posZ+100.5);

		Entity target = this.turtle.world.findNearestEntityWithinAABB(EntityPlayer.class, aabb, this.turtle.playerTurtle);
		if(target == null) {
			return new Object[] {false, "No entities"};
		}
		
		double targetX = 0;
		double targetZ = 0;
		switch(this.turtle.getDirection()) {
		case 2:
			targetX = this.turtle.posZ - target.posZ;
			targetZ = target.posX - this.turtle.posX;
			break;
		case 3:
			targetX = target.posZ - this.turtle.posZ;
			targetZ = this.turtle.posX - target.posX;
			break;
		case 4:
			targetX = this.turtle.posX - target.posX;
			targetZ = this.turtle.posZ - target.posZ;
			break;
		case 5:
			targetX = target.posX - this.turtle.posX;
			targetZ = target.posZ - this.turtle.posZ;
			break;
		}
		double targetY = target.posY - this.turtle.posY;
        
		return new Object[] {targetX, targetY, targetZ};
		
	}
	
	private Object[] setGameMode(Object[] arguments) throws LuaException {
		
		if(!HelperArgs.checkArguments(arguments, 1, new String[] {"number"})) {
			throw new LuaException(EFailureReason.LUA_WRONG_ARG.getMessage());
		}
		
		int value = (int)Math.round(HelperArgs.getDouble(arguments[0]));
		if(value != 0 && value != 1) {
			throw new LuaException(EFailureReason.LUA_WRONG_ARG.getMessage());
		}
		
		//	0 = Survival, 1 == Creative
		this.gamemode = (int)value;
		
		return new Object[] {null};
		
	}
	//----------//
	// end
	//----------//
	
	//----------//
	//sub methods
	//----------//
	private void applyPosition() {
		
		switch(this.turtle.getDirection()) {
			case 2:
				this.turtle.playerTurtle.posX += 0.5D + this.relativePosZ;
				this.turtle.playerTurtle.posZ += 0.5D - this.relativePosX;
				break;
			case 3:
				this.turtle.playerTurtle.posX += 0.5D - this.relativePosZ;
				this.turtle.playerTurtle.posZ += 0.5D + this.relativePosX;
				break;
			case 4:
				this.turtle.playerTurtle.posX += 0.5D - this.relativePosX;
				this.turtle.playerTurtle.posZ += 0.5D - this.relativePosZ;
				break;
			case 5:
				this.turtle.playerTurtle.posX += 0.5D + this.relativePosX;
				this.turtle.playerTurtle.posZ += 0.5D + this.relativePosZ;		
				break;
		}
		this.turtle.playerTurtle.posY += 0.5D + this.relativePosY;
		
	}
	
	private void applyAngle() {
		
		this.turtle.playerTurtle.rotationYaw = MathHelper.wrapAngleTo180_float(this.yaw + (this.dir-2)*90);
		this.turtle.playerTurtle.rotationPitch = this.pitch;
		
	}
	
	private void applyGameMode() {
		
		if(this.gamemode == 1 && this.turtle.world.getWorldInfo().getGameType() == GameType.CREATIVE) {
			this.turtle.playerTurtle.capabilities.isCreativeMode = true;
		}
		else {
			this.turtle.playerTurtle.capabilities.isCreativeMode = false;
		}
		
	}
		
}
