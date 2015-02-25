package noki.moreturtles.turtle.tool;
 
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.IShearable;
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
 * @class ToolShearing
 *
 * @description Shearing Turtleを定義するクラスです。
 * @description_en Class of Shearing Turtle.
 */
public class ToolShearing implements ITurtleUpgrade {
	
	//******************************//
	// define member variables.
	//******************************//
	public int upgradeMeta = 2;
	public ItemStack upgradeItem = new ItemStack(RegisterItems.extendedItems, 1, this.upgradeMeta);
	
	
	//******************************//
	// define member methods.
	//******************************//
	@Override
	public int getUpgradeID() {
		
		return MoreTurtlesData.shearsTurtleTID;
		
	}
 
	@Override
	public String getUnlocalisedAdjective() {
		
		return "Shearing";
		
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
		
		int newX = turtle.posX + Facing.offsetsXForSide[dir];
		int newY = turtle.posY + Facing.offsetsYForSide[dir];
		int newZ = turtle.posZ + Facing.offsetsZForSide[dir];
		Block block = turtle.world.getBlock(newX, newY, newZ);
		int metadata = turtle.world.getBlockMetadata(newX, newY, newZ);
		ItemStack item = new ItemStack(Items.shears);
		
		//check whether the block is air
		if(turtle.world.isAirBlock(newX, newY, newZ)) {
			return MTTurtleAccess.result(false, EFailureReason.NO_DIG_TARGET);
		}
		
		//check the block hardness.
		if(block.getBlockHardness(turtle.world, newX, newY, newZ) <= -1.0F) {
			return MTTurtleAccess.result(false, EFailureReason.NO_DIG_TARGET);
		}
		
		if(!(block instanceof IShearable) &&
				item.getItem().getDigSpeed(item, block, metadata) <= 1.0F) {
			return MTTurtleAccess.result(false, EFailureReason.NO_DIG_TARGET);
		}
		
		ArrayList<ItemStack> items;
		if(block instanceof IShearable) {
			items = ((IShearable)block).onSheared(item, turtle.world, newX, newY, newZ, 0);
		}
		else {
			items = block.getDrops(turtle.world, newX, newY, newZ, metadata, 0);
		}
		turtle.world.setBlockToAir(newX, newY, newZ);
		turtle.world.playAuxSFX(2001, newX, newY, newZ, Block.getIdFromBlock(block) + metadata * 4096);
		if(items != null) {
			for(ItemStack each : items) {
				turtle.store(each);
			}
		}
		
		return MTTurtleAccess.result(true);
		
	}
	
	private TurtleCommandResult attack(MTTurtleAccess turtle, int dir) {
		
		int newX = turtle.posX + Facing.offsetsXForSide[dir];
		int newY = turtle.posY + Facing.offsetsYForSide[dir];
		int newZ = turtle.posZ + Facing.offsetsZForSide[dir];
		
		//define the region for searching targets.
		AxisAlignedBB aabb = AxisAlignedBB.getBoundingBox(newX, newY, newZ, newX+1.0D, newY+1.0D, newZ+1.0D);
		//get entities.
		@SuppressWarnings("rawtypes")
		List list = turtle.world.getEntitiesWithinAABBExcludingEntity(turtle.playerTurtle, aabb);
		
		if(list == null || list.size() == 0) {
			return MTTurtleAccess.result(false, EFailureReason.NO_ATTACK_TARGET);
		}
		
		for(Object each: list) {
			if(each != null && each instanceof IShearable) {
				ItemStack shears = new ItemStack(Items.shears);
				Entity entity = (Entity)each;
				if(((IShearable)entity).isShearable(shears, entity.worldObj, (int)entity.posX, (int)entity.posY, (int)entity.posZ)) {
					ArrayList<ItemStack> ret
						= ((IShearable)entity).onSheared(shears, entity.worldObj, (int)entity.posX, (int)entity.posY, (int)entity.posZ, 0);
					if(ret != null) {
						for(ItemStack item: ret) {
							turtle.store(item);
						}
					}
					return MTTurtleAccess.result(true);
				}
			}
		}

		return MTTurtleAccess.result(false, EFailureReason.NO_ATTACK_TARGET);
		
	}
 
}
