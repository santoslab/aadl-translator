package edu.ksu.cis.projects.mdcf.aadltranslator;

import java.util.LinkedList;

import org.osate.aadl2.DirectionType;
import org.osate.aadl2.PortCategory;

public class ProcessModel {

	private String objectName;
	private LinkedList<PortModel> ports;

	public ProcessModel() {
		ports = new LinkedList<>();
	}

	public void setObjectName(String name) {
		objectName = name;
	}

	public void addPort(String name, DirectionType direction,
			PortCategory category, String representation) {
		ports.add(new PortModel(name, direction, category, representation));
	}

	public class PortModel {
		private String portName;
		private DirectionType direction;
		private PortCategory category;
		private String representation;

		public PortModel(String name, DirectionType direction,
				PortCategory category, String representation) {
			portName = name;
			this.direction = direction;
			this.category = category;
			this.representation = representation;
		}

		public String getRepresentation() {
			return representation;
		}
		
		public String getPortName() {
			return portName;
		}

		public DirectionType getDirection() {
			return direction;
		}

		public PortCategory getCategory() {
			return category;
		}

	}
}
