package noki.moreturtles.proxy;

import cpw.mods.fml.client.registry.RenderingRegistry;
import noki.moreturtles.MoreTurtlesData;
import noki.moreturtles.proxy.ProxyCommon;
import noki.moreturtles.render.RenderBlockExtendedBlocks;


/**********
 * @class ProxyClient
 *
 * @description クライアント用proxyクラス。
 * @description_en Proxy class for Client.
 */
public class ProxyClient implements ProxyCommon {
	
	//******************************//
	// define member variables.
	//******************************//


	//******************************//
	// define member methods.
	//******************************//
	@Override
	public void registerRenderers() {
		
		//	extended blocks.
		MoreTurtlesData.renderID_extendedBlocks = RenderingRegistry.getNextAvailableRenderId();
		RenderingRegistry.registerBlockHandler(new RenderBlockExtendedBlocks());
		
	}
			
}
