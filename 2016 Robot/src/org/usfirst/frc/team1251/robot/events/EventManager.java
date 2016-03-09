package org.usfirst.frc.team1251.robot.events;

import java.util.EventListener;
import javax.swing.event.EventListenerList;

import org.usfirst.frc.team1251.robot.events.listeners.*;

public final class EventManager {
	private static final EventListenerList listenerList = new EventListenerList();

	public synchronized <T extends Event> void fireEvent(Class<T> type) {
		try {
			if (type == AutoInitEvent.class)
				fireAutoInit();
			else if (type == AutoPeriodicEvent.class)
				fireAutoPeriodic();
			else if (type == DisabledInitEvent.class)
				fireDisabledInit();
            else if (type == DisabledPeriodicEvent.class)
                fireDisabledPeriodic();
			else if (type == RobotInitEvent.class)
				fireRobotInit();
			else if (type == TeleopInitEvent.class)
				fireTeleopInit();
			else if (type == TeleopPeriodicEvent.class)
				fireTeleopPeriodic();
			else if (type == TestInitEvent.class)
				fireTestInit();
			else if (type == TestPeriodicEvent.class)
				fireTestPeriodic();
			else
				throw new IllegalArgumentException("Invalid event type: " + type.getName());

		} catch (Exception e) {
			System.out.println("Exception" + e + " was thrown after " + type.getName());
			System.out.println("This occurred after: ");
			e.printStackTrace();
		}
	}

	private void fireAutoInit() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2)
			if (listeners[i] == AutoInitListener.class)
				((AutoInitListener) listeners[i + 1]).onAutoInit();
	}

	private void fireAutoPeriodic() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2)
			if (listeners[i] == AutoPeriodicListener.class)
				((AutoPeriodicListener) listeners[i + 1]).onAutoPeriodic();
	}
	
	private void fireDisabledInit() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2)
			if (listeners[i] == DisabledInitListener.class)
				((DisabledInitListener) listeners[i + 1]).onDisabledInit();
	}

    private void fireDisabledPeriodic() {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2)
            if (listeners[i] == DisabledPeriodicListener.class)
                ((DisabledPeriodicListener) listeners[i + 1]).onDisabledPeriodic();
    }
	
	private void fireRobotInit() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2)
			if (listeners[i] == RobotInitListener.class)
				((RobotInitListener) listeners[i + 1]).onRobotInit();
	}
	
	private void fireTeleopInit() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2)
			if (listeners[i] == TeleopInitListener.class)
				((TeleopInitListener) listeners[i + 1]).onTeleopInit();
	}
	
	private void fireTeleopPeriodic() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2)
			if (listeners[i] == TeleopPeriodicListener.class)
				((TeleopPeriodicListener) listeners[i + 1]).onTeleopPeriodic();
	}
	
	private void fireTestInit() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2)
			if (listeners[i] == TestInitListener.class)
				((TestInitListener) listeners[i + 1]).onTestInit();
	}
	
	private void fireTestPeriodic() {
		Object[] listeners = listenerList.getListenerList();
		for (int i = listeners.length - 2; i >= 0; i -= 2)
			if (listeners[i] == TestPeriodicListener.class)
				((TestPeriodicListener) listeners[i + 1]).onTestPeriodic();
	}

	public <T extends EventListener> void add(Class<T> type, T listener) {
		listenerList.add(type, listener);
	}

	public <T extends EventListener> void remove(Class<T> type, T listener) {
		listenerList.remove(type, listener);
	}
}
