package edu.ksu.cis.projects.mdcf.aadltranslator.model;

import com.google.common.base.Predicate;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.AccidentLevelModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.AccidentModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.ConstraintModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.HazardModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.PropagationModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.StpaPreliminaryModel;

public class ModelUtil {
	public static enum ProcessType {
		PSEUDODEVICE, DISPLAY, LOGIC
	};

	public static enum ComponentType {
		SENSOR, ACTUATOR, CONTROLLER, CONTROLLEDPROCESS, AGGREGATION, TOP
	};

	public static enum Keyword {
		NOTPROVIDING, PROVIDING, EARLY, LATE, APPLIEDTOOLONG, STOPPEDTOOSOON, VALUELOW, VALUEHIGH, PARAMSMISSING, PARAMSWRONG, PARAMSOUTOFORDER
	};

	public final static PropagationModel FLOW_SOURCE = new PropagationModel(
			false, null, null);
	public final static PropagationModel FLOW_SINK = new PropagationModel(true,
			null, null);

	public final static Predicate<DevOrProcModel> logicComponentFilter = new Predicate<DevOrProcModel>() {
		public boolean apply(DevOrProcModel dopm) {
			return (dopm instanceof ProcessModel);
		}
	};

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
			
			// Out is appended to the port name for some ports by the string
			// templates, so we have to check both
			// TODO: It should probably not be this way -- Out should always be
			// added by either the view or the controller, not both 
			PortModel pubPort = connection.getPublisher().getPortByName(connection.getPubPortName() + "Out");
			if(pubPort == null)
				pubPort = connection.getPublisher().getPortByName(connection.getPubPortName());
			String type = pubPort.getType();
			
			return !(type.equals("Object") || type.equals("Boolean"));
		}
	};

	public final static Predicate<ConnectionModel> controlActionFilter = new Predicate<ConnectionModel>() {
		public boolean apply(ConnectionModel connection) {
			// TODO: We need to think harder about what's a control action --
			// since threads (controllers) talk to their containing processes
			// (controllers in the system view, actuators in the thread view) we
			// may even need multi-role components, or per-view component roles.
//			return connection.getSubscriber().getComponentType() == ComponentType.ACTUATOR;
			return true;
		}
	};
	
	public final static Predicate<ConnectionModel> devicePublishedFilter = new Predicate<ConnectionModel>() {
		public boolean apply(ConnectionModel connection) {
			return connection.publisher instanceof DeviceModel;
		}
	};
	
	public final static Predicate<ConnectionModel> deviceSubscribedFilter = new Predicate<ConnectionModel>() {
		public boolean apply(ConnectionModel connection) {
			return connection.subscriber instanceof DeviceModel;
		}
	};
}
