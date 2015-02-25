package noki.moreturtles.blocks;

import java.util.HashMap;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import noki.moreturtles.ModInfo;
import noki.moreturtles.MoreTurtlesData;


/**********
 * @class BlockExtendedBlocks
 *
 * @description タートルアップグレード用のブロックです。<br>
 * 周辺機器系タートルに使います。機能は一切ありません。<br>
 * メタデータ依存のマルチブロックです(羊毛ブロックなどに近い)。<br>
 * 作成元のテクスチャそれぞれに独自のテクスチャを重ねています。
 * @description_en Block for turtle upgrades.<br>
 * It's used for peripheral-type turtles and has no functionality.<br>
 * It's a multi-block, dependent of its metadata (similar to Wool etc).<br>
 * Its texture overlaid the ingredient block's texture.
 * 
 * @see BlockExtendedBlocksItem, RenderBlockExtendedBlocks.
 * 
 * @note メタデータ依存マルチブロックを作るには、ItemBlockクラスを用意しなければなりません。<br>
 * 複数のテクスチャを重ねたブロックを作るには、専用のブロックレンダークラスを用意しなければなりません。
 * @note_en You have to create ItemBlock class to make metadata-sensitive multi-block.<br>
 * You have to create a class for special block rendering, to make an overlaid textures block.
 */
public class BlockExtendedBlocks extends Block {

	//******************************//
	// define member variables.
	//******************************//
	@SuppressWarnings("serial")
	private static HashMap<Integer, EachBlock> extendedBlocks = 
			new HashMap<Integer, EachBlock>() {{ put(0, new EachBlock(0, null, 0, "extensionCube")); }};

	
	//******************************//
	// define member methods.
	//******************************//
	protected BlockExtendedBlocks(String unlocalizedName) {
		
		super(Material.glass);
		this.setBlockName(unlocalizedName);
		this.setHardness(0.3F);
		this.setStepSound(soundTypeGlass);
		this.setCreativeTab(MoreTurtlesData.tabMoreTurtles);
		this.setBlockTextureName(ModInfo.ID.toLowerCase() + ":" + unlocalizedName);
		
	}
	
	@Override
	public int damageDropped(int metadata) {
		
		return metadata;
		
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	@SuppressWarnings({ "rawtypes", "unchecked" })	//about List.
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		
		for(EachBlock each: extendedBlocks.values()) {
			list.add(new ItemStack(item, 1, each.index));
		}
		
	}
	
	@Override
	public boolean isOpaqueCube() {
		
		return false;
		
	}
	
	@Override
	public boolean renderAsNormalBlock(){
		
		return false;
		
	}
	
	@Override
	public int getRenderType() {
		
		return MoreTurtlesData.renderID_extendedBlocks;
		
	}
		
	
	//**********
	// methods and class for control extended blocks.
	//**********	
	public static void addBlock(int index, Block block, int metadata, String name) {
		
		extendedBlocks.put(index, new EachBlock(index, block, metadata, name));
		
	}
	
	private static class EachBlock {
		
		public int index;
		public Block block;
		public int metadata;
		public String name;
		
		public EachBlock(int index, Block block, int metadata, String name) {
			this.index = index;
			this.block = block;
			this.metadata = metadata;
			this.name = name;
		}
		
	}
	
	public Block getEachExtendedBlock(int metadata) {
		
		int meta = extendedBlocks.containsKey(metadata) ? metadata : 0;
		
		return extendedBlocks.get(meta).block;
		
	}
	
	public int getEachExtendedMetadata(int metadata) {
		
		int meta = extendedBlocks.containsKey(metadata) ? metadata : 0;
		
		return extendedBlocks.get(meta).metadata;
		
	}

	public String getEachExtendedName(int metadata) {
		
		int meta = extendedBlocks.containsKey(metadata) ? metadata : 0;
		
		return extendedBlocks.get(meta).name;
		
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getEachExtendedIcon(int metadata) {
		
		int meta = extendedBlocks.containsKey(metadata) ? metadata : 0;
		
		if(meta == 0) {
			return this.getIcon(1, 0);
		}
		else {
			EachBlock each = extendedBlocks.get(meta);
			return each.block.getIcon(1, each.metadata);
		}
		
	}

}
