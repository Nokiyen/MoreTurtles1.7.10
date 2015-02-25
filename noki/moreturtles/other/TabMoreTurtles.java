package noki.moreturtles.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import noki.moreturtles.MoreTurtlesData;


/**********
 * @class TabMoreTurtles
 *
 * @description MoreTurtlesのクリエイティブタブです。
 * @description_en MoreTurtles' creative tab.
 */
public class TabMoreTurtles extends CreativeTabs {

	//******************************//
	// define member variables.
	//******************************//
	public static String label = "MoreTurtles";

	
	//******************************//
	// define member methods.
	//******************************//
	public TabMoreTurtles() {
		
		super(label);
		
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem() {
		
		return Item.getItemFromBlock(MoreTurtlesData.cc_turtle);

	}

}
