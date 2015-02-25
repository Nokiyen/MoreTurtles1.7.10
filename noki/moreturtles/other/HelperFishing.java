package noki.moreturtles.other;

import java.util.Arrays;
import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemFishFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandomFishable;
import net.minecraft.world.World;


/**********
 * @class HelperFishing
 *
 * @description 釣りに関するヘルパークラスです。
 * @description_en Helper class of fishing.
 */
public class HelperFishing {
	
	//******************************//
	// define member variables.
	//******************************//
	//same as the fields of EntityFishHook.
	private static List<WeightedRandomFishable> junk = Arrays.asList(
			new WeightedRandomFishable[] {
					(new WeightedRandomFishable(new ItemStack(Items.leather_boots), 10)).func_150709_a(0.9F),
					new WeightedRandomFishable(new ItemStack(Items.leather), 10),
					new WeightedRandomFishable(new ItemStack(Items.bone), 10),
					new WeightedRandomFishable(new ItemStack(Items.potionitem), 10),
					new WeightedRandomFishable(new ItemStack(Items.string), 5),
					(new WeightedRandomFishable(new ItemStack(Items.fishing_rod), 2)).func_150709_a(0.9F),
					new WeightedRandomFishable(new ItemStack(Items.bowl), 10),
					new WeightedRandomFishable(new ItemStack(Items.stick), 5),
					new WeightedRandomFishable(new ItemStack(Items.dye, 10, 0), 1),
					new WeightedRandomFishable(new ItemStack(Blocks.tripwire_hook), 10),
					new WeightedRandomFishable(new ItemStack(Items.rotten_flesh), 10)
			});
	private static List<WeightedRandomFishable> treasure = Arrays.asList(
			new WeightedRandomFishable[] {
					new WeightedRandomFishable(new ItemStack(Blocks.waterlily), 1),
					new WeightedRandomFishable(new ItemStack(Items.name_tag), 1),
					new WeightedRandomFishable(new ItemStack(Items.saddle), 1),
					(new WeightedRandomFishable(new ItemStack(Items.bow), 1)).func_150709_a(0.25F).func_150707_a(),
					(new WeightedRandomFishable(new ItemStack(Items.fishing_rod), 1)).func_150709_a(0.25F).func_150707_a(),
					(new WeightedRandomFishable(new ItemStack(Items.book), 1)).func_150707_a()
			});
	private static List<WeightedRandomFishable> fish = Arrays.asList(
			new WeightedRandomFishable[] {
					new WeightedRandomFishable(new ItemStack(Items.fish, 1, ItemFishFood.FishType.COD.func_150976_a()), 60),
					new WeightedRandomFishable(new ItemStack(Items.fish, 1, ItemFishFood.FishType.SALMON.func_150976_a()), 25),
					new WeightedRandomFishable(new ItemStack(Items.fish, 1, ItemFishFood.FishType.CLOWNFISH.func_150976_a()), 2),
					new WeightedRandomFishable(new ItemStack(Items.fish, 1, ItemFishFood.FishType.PUFFERFISH.func_150976_a()), 13)
			});

	
	//******************************//
	// define member methods.
	//******************************//
	//same as the EntithFishHook.func_146033_f().
	public static ItemStack getFishable(World world, int luckLevel, int speedLevel) {
		
		Float targetProv = world.rand.nextFloat();
		float firstProv = 0.1F - (float)luckLevel * 0.025F - (float)speedLevel * 0.01F;
		float secondProv = 0.05F + (float)luckLevel * 0.01F - (float)speedLevel * 0.01F;
		firstProv = MathHelper.clamp_float(firstProv, 0.0F, 1.0F);
		secondProv = MathHelper.clamp_float(secondProv, 0.0F, 1.0F);
		
		if(targetProv < firstProv) {
			return ((WeightedRandomFishable)WeightedRandom.getRandomItem(world.rand, junk)).func_150708_a(world.rand);
		}
		else {
			targetProv -= 0.1F;
			if(targetProv < secondProv) {
				return ((WeightedRandomFishable)WeightedRandom.getRandomItem(world.rand, treasure)).func_150708_a(world.rand);				
			}
			else {
				return ((WeightedRandomFishable)WeightedRandom.getRandomItem(world.rand, fish)).func_150708_a(world.rand);
			}
		}
		
	}

}
