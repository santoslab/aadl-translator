package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import com.google.common.base.Predicate;

public class ModelUtil {
	public static enum ProcessType {
		PSEUDODEVICE, DISPLAY, LOGIC
	};
	
	public static enum ComponentType {
		SENSOR, ACTUATOR, CONTROLLER, CONTROLLEDPROCESS
	};

	public static enum Keyword {
		NOTPROVIDING, PROVIDING, EARLY, LATE, APPLIEDTOOLONG, STOPPEDTOOSOON, VALUELOW, VALUEHIGH, PARAMSMISSING, PARAMSWRONG, PARAMSOUTOFORDER
	};

	public final static PropagationModel FLOW_SOURCE = new PropagationModel(
			false, null, null);
	public final static PropagationModel FLOW_SINK = new PropagationModel(true,
			null, null);

	public final static Predicate<PortModel> receivePortFilter = new Predicate<PortModel>() {
		public boolean apply(PortModel pm) {
			return pm.isSubscribe();
		}
	};

	public final static Predicate<PortModel> receiveEventDataPortFilter = new Predicate<PortModel>() {
		public boolean apply(PortModel pm) {
			return pm.isSubscribe() && pm.isEventData();
		}
	};

	public final static Predicate<PortModel> receiveEventPortFilter = new Predicate<PortModel>() {
		public boolean apply(PortModel pm) {
			return pm.isSubscribe() && pm.isEvent();
		}
	};

	public final static Predicate<PortModel> receiveDataPortFilter = new Predicate<PortModel>() {
		public boolean apply(PortModel pm) {
			return pm.isSubscribe() && pm.isData();
		}
	};

	public final static Predicate<PortModel> sendPortFilter = new Predicate<PortModel>() {
		public boolean apply(PortModel pm) {
			return !pm.isSubscribe();
		}
	};

	public final static Predicate<TaskModel> periodicTaskFilter = new Predicate<TaskModel>() {
		public boolean apply(TaskModel tm) {
			return !tm.isSporadic();
		}
	};

	public final static Predicate<TaskModel> sporadicTaskFilter = new Predicate<TaskModel>() {
		public boolean apply(TaskModel tm) {
			return tm.isSporadic();
		}
	};

	public final static Predicate<StpaPreliminaryModel> accidentLevelFilter = new Predicate<StpaPreliminaryModel>() {
		public boolean apply(StpaPreliminaryModel prelim) {
			return prelim instanceof AccidentLevelModel;
		}
	};

	public final static Predicate<StpaPreliminaryModel> accidentFilter = new Predicate<StpaPreliminaryModel>() {
		public boolean apply(StpaPreliminaryModel prelim) {
			return prelim instanceof AccidentModel;
		}
	};

	public final static Predicate<StpaPreliminaryModel> hazardFilter = new Predicate<StpaPreliminaryModel>() {
		public boolean apply(StpaPreliminaryModel prelim) {
			return prelim instanceof HazardModel;
		}
	};

	public final static Predicate<StpaPreliminaryModel> constraintFilter = new Predicate<StpaPreliminaryModel>() {
		public boolean apply(StpaPreliminaryModel prelim) {
			return prelim instanceof ConstraintModel;
		}
	};

	public final static Predicate<ConnectionModel> rangedChannelFilter = new Predicate<ConnectionModel>() {
		public boolean apply(ConnectionModel connection) {
			return !(connection.getPublisher().getPortByName(connection.getPubPortName()).getType().equals("Object") || 
					connection.getPublisher().getPortByName(connection.getPubPortName()).getType().equals("Boolean"));
		}
	};

	public final static Predicate<ConnectionModel> controlActionFilter = new Predicate<ConnectionModel>() {
		public boolean apply(ConnectionModel connection) {
			return connection.getSubscriber().getComponentType() == ComponentType.ACTUATOR;
		}
	};
	
	public final static Predicate<ConnectionModel> devicePublishedFilter = new Predicate<ConnectionModel>() {
		public boolean apply(ConnectionModel connection) {
			return connection.isDevicePublished();
		}
	};
	
	public final static Predicate<ConnectionModel> deviceSubscribedFilter = new Predicate<ConnectionModel>() {
		public boolean apply(ConnectionModel connection) {
			return connection.isDeviceSubscribed();
		}
	};
}
