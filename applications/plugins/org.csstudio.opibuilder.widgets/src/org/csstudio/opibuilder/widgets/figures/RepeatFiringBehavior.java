package org.csstudio.opibuilder.widgets.figures;

import java.util.Timer;
import java.util.TimerTask;

public class RepeatFiringBehavior
{
	protected static final int
		INITIAL_DELAY = 250,
		STEP_DELAY = 40;
	
	protected int
		stepDelay = INITIAL_DELAY,
		initialDelay = STEP_DELAY;
	
	protected Timer timer;
	
	private Runnable runTask;
	
	public void pressed() {
		runTask.run();
		
		timer = new Timer();
		TimerTask runAction = new Task();

		timer.scheduleAtFixedRate(runAction, INITIAL_DELAY, STEP_DELAY);
	}

	public void canceled() {
		suspend();
	}	
	public void released() {
		suspend();
	}
	
	public void resume() {
		timer = new Timer();
		
		TimerTask runAction = new Task();
		
		timer.scheduleAtFixedRate(runAction, STEP_DELAY, STEP_DELAY);
	}
	
	public void suspend() {
		if (timer == null) return;
		timer.cancel();
		timer = null;
	}

	
	public void setRunTask(Runnable runTask) {
		this.runTask = runTask;
	}

class Task 
	extends TimerTask {
	
	public void run() {
		org.eclipse.swt.widgets.Display.getDefault().syncExec(runTask);
	}
}

}