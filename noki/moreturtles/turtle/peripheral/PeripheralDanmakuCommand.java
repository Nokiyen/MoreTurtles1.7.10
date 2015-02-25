package noki.moreturtles.turtle.peripheral;

import java.util.LinkedList;

import thKaguyaMod.LaserData;
import thKaguyaMod.ShotData;
import thKaguyaMod.THShotLib;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import dan200.computercraft.api.turtle.ITurtleAccess;
import dan200.computercraft.api.turtle.ITurtleCommand;
import dan200.computercraft.api.turtle.TurtleCommandResult;


/**********
 * @class PeripheralDanmakuCommand
 *
 * @description
 * @description_en
 */
public class PeripheralDanmakuCommand implements ITurtleCommand {
	
	//******************************//
	// define member variables.
	//******************************//
	private LinkedList<ShootStack> stacks = new LinkedList<ShootStack>();

	
	//******************************//
	// define member methods.
	//******************************//
	public void addStack(
			EDanmakuFormType formType,
			EntityPlayer playerTurtle, Vec3 pos, Vec3 look,
			Vec3 rotate, float rotationSpeed, int rotationEnd,
			double firstSpeed, double limitSpeed, double acceleration,
			Vec3 gravity, ShotData shot, LaserData laser,
			int way, double distance, float span, float angle) {
		
		this.stacks.offer(new ShootStack(
				formType, playerTurtle, pos, look,
				rotate, rotationSpeed, rotationEnd,
				firstSpeed, limitSpeed, acceleration,
				gravity, shot, laser,
				way, distance, span, angle)
		);
		
	}
	
	@Override
	public TurtleCommandResult execute(ITurtleAccess turtle) {
		
		while(this.stacks.size() > 0) {
			
			ShootStack stack = this.stacks.poll();
		
			switch(stack.itemType) {
			case BULLET:
				switch(stack.formType) {
				case SINGLE:
					THShotLib.createShot(
							(EntityLivingBase)stack.playerTurtle, (Entity)stack.playerTurtle,
							stack.pos, stack.look, 0F, stack.rotate, stack.rotationSpeed, stack.rotationEnd,
							stack.firstSpeed, stack.limitSpeed, stack.acceleration, stack.gravity, stack.shot);
					break;
				case WIDE:
					THShotLib.createWideShot(
							(EntityLivingBase)stack.playerTurtle, (Entity)stack.playerTurtle,
							stack.pos, stack.look, stack.rotationSpeed, stack.rotationEnd,
							stack.firstSpeed, stack.limitSpeed, stack.acceleration, stack.gravity, stack.shot,
							stack.way, stack.span, stack.distance, stack.angle);
					break;
				case CIRCLE:
					THShotLib.createCircleShot(
							(EntityLivingBase)stack.playerTurtle, (Entity)stack.playerTurtle,
							stack.pos, stack.look, stack.rotationSpeed, stack.rotationEnd,
							stack.firstSpeed, stack.limitSpeed, stack.acceleration, stack.gravity, stack.shot,
							stack.way, stack.distance, stack.angle);
					break;
				case RING:
					THShotLib.createRingShot(
							(EntityLivingBase)stack.playerTurtle, (Entity)stack.playerTurtle,
							stack.pos, stack.look, stack.rotationSpeed, stack.rotationEnd,
							stack.firstSpeed, stack.limitSpeed, stack.acceleration, stack.gravity, stack.shot,
							stack.way, stack.distance, stack.span, stack.angle);
					break;
				case RANDOM_RING:
					THShotLib.createRandomRingShot(
							(EntityLivingBase)stack.playerTurtle, (Entity)stack.playerTurtle,
							stack.pos, stack.look, stack.rotationSpeed, stack.rotationEnd,
							stack.firstSpeed, stack.limitSpeed, stack.acceleration, stack.gravity, stack.shot,
							stack.way, stack.distance, stack.span);
					break;
				case SPHERE:
					THShotLib.createSphereShot(
							(EntityLivingBase)stack.playerTurtle, (Entity)stack.playerTurtle,
							stack.pos, stack.look, (float)stack.look.zCoord, stack.rotate, stack.rotationSpeed, stack.rotationEnd,
							stack.firstSpeed, stack.limitSpeed, stack.acceleration, stack.gravity, stack.shot,
							stack.way, stack.distance, stack.angle);
					break;
				}
				break;
			case LASER:
				switch(stack.formType) {
				case SINGLE:
					THShotLib.createLaserA(
							(EntityLivingBase)stack.playerTurtle, (Entity)stack.playerTurtle,
							stack.pos, stack.look,
							stack.firstSpeed, stack.limitSpeed, stack.acceleration, stack.gravity, stack.laser);
					break;
				case WIDE:
					THShotLib.createWideLaserA(
							(EntityLivingBase)stack.playerTurtle, (Entity)stack.playerTurtle,
							stack.pos, stack.look,
							stack.firstSpeed, stack.limitSpeed, stack.acceleration, stack.gravity, stack.laser,
							stack.way, stack.span, stack.distance);
					break;
				case CIRCLE:
					THShotLib.createCircleLaserA(
							(EntityLivingBase)stack.playerTurtle, (Entity)stack.playerTurtle,
							stack.pos, stack.look,
							stack.firstSpeed, stack.limitSpeed, stack.acceleration, THShotLib.gravity_Zero(), stack.laser,
							stack.way, stack.distance);
					break;
				case RING:
					THShotLib.createRingLaserA(
							(EntityLivingBase)stack.playerTurtle, (Entity)stack.playerTurtle,
							stack.pos, stack.look,
							stack.firstSpeed, stack.limitSpeed, stack.acceleration, stack.gravity, stack.laser,
							stack.way, stack.distance, stack.span, stack.angle);
					break;
				case RANDOM_RING:
					THShotLib.createRandomRingLaser(
							(EntityLivingBase)stack.playerTurtle, (Entity)stack.playerTurtle,
							stack.pos, stack.look,
							stack.firstSpeed, stack.limitSpeed, stack.acceleration, stack.gravity, stack.laser,
							stack.way, stack.distance, stack.span);
					break;
				case SPHERE:
					THShotLib.createSphereLaserA(
							(EntityLivingBase)stack.playerTurtle, (Entity)stack.playerTurtle,
							stack.pos, stack.look,
							stack.firstSpeed, stack.limitSpeed, stack.acceleration, stack.gravity, stack.laser,
							stack.way, stack.distance, stack.angle);
					break;
				}
				break;
			}
			THShotLib.playShotSound(stack.playerTurtle);
		}
		
		return TurtleCommandResult.success();

	}
	
	private class ShootStack {
		
		EDanmakuItemType itemType;
		EDanmakuFormType formType;
		
		ShotData shot;
		LaserData laser;

		EntityPlayer playerTurtle;
		Vec3 pos;
		Vec3 look;
		Vec3 rotate;
		float rotationSpeed;
		int rotationEnd;
		double firstSpeed;
		double limitSpeed;
		double acceleration;
		Vec3 gravity;
		
		int way;
		double distance;
		float span;
		float angle;

		public ShootStack (
				EDanmakuFormType formType,
				EntityPlayer playerTurtle, Vec3 pos, Vec3 look,
				Vec3 rotate, float rotationSpeed, int rotationEnd,
				double firstSpeed, double limitSpeed, double acceleration,
				Vec3 gravity, ShotData shot, LaserData laser,
				int way, double distance, float span, float angle) {
			
			if(laser == null) {
				this.itemType = EDanmakuItemType.BULLET;
			}
			else {
				this.itemType = EDanmakuItemType.LASER;
			}
			this.shot = shot;
			this.laser = laser;
			
			this.formType = formType;
			
			this.playerTurtle = playerTurtle;
			this.pos = pos;
			this.look = look;
			this.rotate = rotate;
			this.rotationSpeed = rotationSpeed;
			this.rotationEnd = rotationEnd;
			this.firstSpeed = firstSpeed;
			this.limitSpeed = limitSpeed;
			this.acceleration = acceleration;
			this.gravity = gravity;
			
			this.way = way;
			this.distance = distance;
			this.span = span;
			this.angle = angle;
			
		}
				
	}
	
	public enum EDanmakuFormType {
		
		SINGLE,
		WIDE,
		CIRCLE,
		RING,
		RANDOM_RING,
		SPHERE;
		
	}
	
	public enum EDanmakuItemType {
		
		BULLET,
		LASER;

	}

}
