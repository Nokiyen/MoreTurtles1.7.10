package noki.moreturtles.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import noki.moreturtles.MoreTurtlesData;
import noki.moreturtles.blocks.BlockExtendedBlocks;
import noki.moreturtles.blocks.RegisterBlocks;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;


/**********
 * @class RenderBlockExtendedBlocks
 *
 * @description BlockExtendedBlocksをレンダーするためのクラスです。<br>
 * ここで、作成元のテクスチャに、専用のテクスチャを重ねる処理をしています。<br>
 * レンダーの登録は、ProxyClientで行っています。
 * @description_en A class for rendering BlockExtendedBlocks.<br>
 * Here I write the code to overlay the block's texture over the ingredient block's.<br>
 * This is registered in ProxyClient.
 * 
 * @see BlockExtendedBlocks, RenderBlocks, ProxyClient.
 */
public class RenderBlockExtendedBlocks implements ISimpleBlockRenderingHandler {
	
	//******************************//
	// define member variables.
	//******************************//
	
	
	//******************************//
	// define member methods.
	//******************************//
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
		
		Block currentBlock = ((BlockExtendedBlocks)block).getEachExtendedBlock(metadata);
		int currentMeta = ((BlockExtendedBlocks)block).getEachExtendedMetadata(metadata);
		
		if(currentBlock != null) {
			switch(metadata) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
				renderer.setRenderBounds(0.0D, 0.25D, 0.0D, 1.0D, 0.75D, 1.0D);
				break;
			case 6:
				renderer.setRenderBounds(0.0D, 0.3125D, 0.0D, 1.0D, 0.6875D, 1.0D);
				break;
			case 7:
				int j = 16777215;
				float f1 = (float)(j >> 16 & 255) / 255.0F;
				float f2 = (float)(j >> 8 & 255) / 255.0F;
				float f3 = (float)(j & 255) / 255.0F;
				GL11.glColor4f(f1 * 1.0F, f2 * 1.0F, f3 * 1.0F, 1.0F);
				renderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
				break;
			case 8:
			case 9:
			case 15:
				renderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
				break;
			}
			this.renderStarndardInventoryBlock(currentBlock, currentMeta, renderer, 0.6F);
		}

		renderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
		this.renderStarndardInventoryBlock(block, 0, renderer, 1.0F);
				
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		
		BlockExtendedBlocks extendedBlock = (BlockExtendedBlocks)block;
		int metadata = world.getBlockMetadata(x, y, z);
		Block currentBlock = extendedBlock.getEachExtendedBlock(metadata);
		
		if(currentBlock != null) {
			switch(metadata) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
				renderer.setRenderBounds(0.2D, 0.275D, 0.2D, 0.8D, 0.725D, 0.8D);
				renderer.renderStandardBlock(currentBlock, x, y, z);
				break;
			case 6:
				renderer.setRenderBounds(0.2D, 0.3875D, 0.2D, 0.8D, 0.6125D, 0.8D);
				renderer.renderStandardBlock(currentBlock, x, y, z);
				break;
			case 7:
				renderer.setRenderBounds(0.2D, 0.2D, 0.2D, 0.8D, 0.8D, 0.8D);
				renderer.renderStandardBlock(currentBlock, x, y, z);
				break;
			case 9:
				renderer.setRenderBounds(0.2D, 0.2D, 0.2D, 0.8D, 0.8D, 0.8D);
				renderer.renderStandardBlock(currentBlock, x, y, z);
				break;
			case 8:
			case 15:
				renderer.setOverrideBlockTexture(renderer.getBlockIconFromSideAndMetadata(currentBlock, 0, 0));
				renderer.setRenderBounds(0.2D, 0.2D, 0.2D, 0.8D, 0.8D, 0.8D);
				renderer.renderStandardBlock(block, x, y, z);
				break;
			}
			renderer.renderAllFaces = true;
		}
		renderer.setOverrideBlockTexture(renderer.getBlockIcon(RegisterBlocks.extendedBlocks));
		renderer.setRenderBounds(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
		renderer.renderStandardBlock(block, x, y, z);
        renderer.renderAllFaces = false;
        renderer.clearOverrideBlockTexture();

		return true;

	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		
		return true;
		
	}

	@Override
	public int getRenderId() {
		
		return MoreTurtlesData.renderID_extendedBlocks;
		
	}
	
	private void renderStarndardInventoryBlock(Block block, int metadata, RenderBlocks renderer, float scale) {
		
		GL11.glPushMatrix();
		GL11.glScalef(scale, scale, scale);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		
		Tessellator tessellator = Tessellator.instance;
		
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 0, metadata));
		tessellator.draw();
		
        if (block == Blocks.grass && renderer.useInventoryTint) {
            int k = block.getRenderColor(metadata);
            float f2 = (float)(k >> 16 & 255) / 255.0F;
            float f3 = (float)(k >> 8 & 255) / 255.0F;
            float f4 = (float)(k & 255) / 255.0F;
            GL11.glColor4f(f2 * 1.0F, f3 * 1.0F, f4 * 1.0F, 1.0F);
        }
		
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 1, metadata));
		tessellator.draw();
		
        if (block == Blocks.grass && renderer.useInventoryTint) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
		
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 2, metadata));
		tessellator.draw();
		
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 3, metadata));
		tessellator.draw();
		
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 4, metadata));
		tessellator.draw();
		
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, renderer.getBlockIconFromSideAndMetadata(block, 5, metadata));
		tessellator.draw();
		
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		GL11.glPopMatrix();
		renderer.clearOverrideBlockTexture();
		
	}
	
}
