package noki.moreturtles.proxy;

import noki.moreturtles.MoreTurtlesData;


/**********
 * @class ProxyServer
 *
 * @description サーバ用proxyクラス。
 * @description_en Proxy class for Server.
 */
public class ProxyServer implements ProxyCommon {
	
	//******************************//
	// define member variables.
	//******************************//

	
	//******************************//
	// define member methods.
	//******************************//
	@Override
	public void registerRenderers() {
		
		MoreTurtlesData.renderID_extendedBlocks = -1;
		
	}

}
