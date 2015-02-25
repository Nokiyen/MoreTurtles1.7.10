package noki.moreturtles.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import noki.moreturtles.turtle.common.PlayerTurtle;

public class EventTurtle{

	@SubscribeEvent
	public void OnLivingDropsByLootingTurtle(LivingDropsEvent event) {
		
		if(event.source.getEntity() instanceof PlayerTurtle) {
			event.setCanceled(true);
		}
		
	}

}
