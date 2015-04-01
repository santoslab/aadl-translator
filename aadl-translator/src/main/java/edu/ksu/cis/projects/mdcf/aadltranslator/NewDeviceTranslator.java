package edu.ksu.cis.projects.mdcf.aadltranslator;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.AbstractRule;
import org.eclipse.xtext.nodemodel.BidiTreeIterator;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.impl.CompositeNode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AbstractNamedValue;
import org.osate.aadl2.Comment;
import org.osate.aadl2.Element;
import org.osate.aadl2.EnumerationLiteral;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.EventPort;
import org.osate.aadl2.Feature;
import org.osate.aadl2.FeatureGroupType;
import org.osate.aadl2.IntegerLiteral;
import org.osate.aadl2.ModalPropertyValue;
import org.osate.aadl2.NamedValue;
import org.osate.aadl2.Port;
import org.osate.aadl2.Property;
import org.osate.aadl2.PropertyAssociation;
//import org.osate.aadl2.PropertyExpression;
import org.osate.aadl2.RangeValue;
import org.osate.aadl2.RecordValue;
import org.osate.aadl2.StringLiteral;
import org.osate.aadl2.SystemImplementation;
import org.osate.aadl2.SystemType;
import org.osate.aadl2.modelsupport.errorreporting.MarkerParseErrorReporter;
import org.osate.aadl2.modelsupport.errorreporting.ParseErrorReporter;
import org.osate.aadl2.modelsupport.errorreporting.ParseErrorReporterManager;
import org.osate.aadl2.modelsupport.modeltraversal.AadlProcessingSwitchWithProgress;
import org.osate.aadl2.modelsupport.resources.OsateResourceUtil;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.aadl2.properties.PropertyAcc;
import org.osate.aadl2.util.Aadl2Switch;
import org.osate.xtext.aadl2.properties.util.GetProperties;
import org.osate.xtext.aadl2.properties.util.PropertyUtils;

import edu.ksu.cis.projects.mdcf.aadltranslator.DeviceTranslator.TranslatorSwitch.ActionExecutorExchangeMethods;
import edu.ksu.cis.projects.mdcf.aadltranslator.DeviceTranslator.TranslatorSwitch.ActionInitiatorExchangeMethods;
import edu.ksu.cis.projects.mdcf.aadltranslator.DeviceTranslator.TranslatorSwitch.DML_Port_Properties;
import edu.ksu.cis.projects.mdcf.aadltranslator.DeviceTranslator.TranslatorSwitch.PublishPeriodicExchangeMethods;
import edu.ksu.cis.projects.mdcf.aadltranslator.DeviceTranslator.TranslatorSwitch.SetReceiveExchangeMethods;
import edu.ksu.cis.projects.mdcf.aadltranslator.DeviceTranslator.TranslatorSwitch.SetSendExchangeMethods;
import edu.ksu.cis.projects.mdcf.aadltranslator.exception.NotImplementedException;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.ActionExchangeModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.DeviceComponentModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.ExchangeModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.GetExchangeModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.PeriodicExchangeModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.PortDataTypeMap;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.PortInfoModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.SetExchangeModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.SporadicExchangeModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.PortInfoModel.PortDirection;

public class NewDeviceTranslator extends AadlProcessingSwitchWithProgress {
	private static final Logger log = Logger
			.getLogger(NewDeviceTranslator.class.getName());
	
	private static final String NONESTMT = "none ;";

	private ArrayList<String> propertySetNames = new ArrayList<>();
	private ParseErrorReporterManager errorManager;
	private DeviceComponentModel deviceComponentModel = null;
	private String packageName;
	private int systemCount = 0;
	private String systemName = null;


	public class TranslatorSwitch extends Aadl2Switch<String> {
		@Override
		public String caseAadlPackage(AadlPackage obj) {
			packageName = obj.getFullName();
			for (Element e : obj.getOwnedPublicSection().getOwnedElements()) {

				if (e instanceof SystemType) {
					if (((SystemType) e).getName().equals(packageName)) {
						process(e);
					}
				} else if (e instanceof SystemImplementation) {
					String[] systemImplName = ((SystemImplementation) e)
							.getName().split("\\.");

					if (systemImplName.length > 0
							&& systemImplName[0].equals(packageName)) {
						process(e);
					}
				} else if (e instanceof FeatureGroupType) {
					// All the port information reside in the FeatureGroupType.
					// Process it.
					process(e);
				} else {
					log.log(Level.FINE, "getOwnedPublicSection:Ignored:" + e);
				}
			}

			// Distribute exchanges
			deviceComponentModel.distributeExchanges();

			return DONE;
		}

		@Override
		public String caseSystemType(SystemType st){
			// find system here..if there is more than one system then trigger error.
			if (systemCount > 1) {
				handleException(st, new Exception(
						"Only one system is allowed in device AADL."));
				return DONE;
			} else if (!st.getName().equals(packageName)) {
				handleException(st, new Exception(
						"Mismatching name between the system and the package:"
								+ st.getName() + "<->" + packageName));
				return DONE;
			} else {
				log.log(Level.FINE,
						"System Type Reading:" + st.getName());
				systemName = st.getName();
				systemCount++;
			}
			deviceComponentModel.setName(st.getName());
			return DONE;
		}
		
		@Override
		public String caseSystemImplementation(SystemImplementation si) {

			// Get the properties for the system.
			boolean systemTypeExist = false;
			boolean manufacturerModelExist = false;
			for (PropertyAssociation pa : si.getOwnedPropertyAssociations()) {
				String propertyName = pa.getProperty().getFullName();
				if (propertyName.equals("IEEE11073_MDC_ATTR_SYS_TYPE")) {
					assignSystemType(pa.getOwnedValues().get(0));
					systemTypeExist = true;
				} else if (propertyName.equals("manufacturerModel")) {
					assignManufacturerModel(pa.getOwnedValues());
					manufacturerModelExist = true;
				}
			}

			if (!systemTypeExist) {
				handleException(si, new Exception(
						"Missing System Type Property:" + si.getFullName()));
				return DONE;
			} else if (!manufacturerModelExist) {
				handleException(si,
						new Exception("Missing ManufacturerModel Property:"
								+ si.getFullName()));
				return DONE;
			}

			return DONE;
		}

		public String caseFeatureGroupType(FeatureGroupType object) {
			processComments(object);
			System.err.println("FeatureGroupType:" + object.getName());
			EList<Feature> features = object.getOwnedFeatures();
			if (!(object.getOwnedFeatures() == null || features.isEmpty())) {
				processOptionalSection(features, "features", NONESTMT);
			}

			processCommunicationPropertySection(
					object.getOwnedPropertyAssociations(), object.getName(),
					object.isNoProperties());

			return DONE;
		}

		public void processCommunicationPropertySection(
				EList<PropertyAssociation> list, String featureGroupName,
				Boolean doNone) {
			if (list == null)
				return;
			if (!list.isEmpty()) {
				for (PropertyAssociation pa : list) {
					String propertyName = pa.getProperty().getFullName();
					if (propertyName.equals("MDC_ATTR_ID_PHYSIO")) {
						ModalPropertyValue physioProperty = pa.getOwnedValues()
								.get(0);
						if (physioProperty.getOwnedValue() instanceof StringLiteral) {
							StringLiteral sl = (StringLiteral) physioProperty
									.getOwnedValue();
							System.err.println("MDC_ATTR_ID_PHYSIO:"
									+ sl.getValue());
						}
					} else if (propertyName.equals("MDC_ATTR_UNIT_CODE")) {
						ModalPropertyValue unitProperty = pa.getOwnedValues()
								.get(0);
						if (unitProperty.getOwnedValue() instanceof StringLiteral) {
							StringLiteral sl = (StringLiteral) unitProperty
									.getOwnedValue();
							System.err.println("MDC_ATTR_UNIT_CODE:"
									+ sl.getValue());
						}
					}
				}
			} else if (doNone) {
				System.err.println("No Properties in FeatureGroupType:"
						+ featureGroupName);
			}
		}

		@Override
		public String caseEventDataPort(EventDataPort object) {
			
			processComments(object);
			System.err.println("caseEventDataPort:"
					+ object.getFullName()
					+ "("
					+ object.getDirection().getName()
					+ ")"
					+ AadlUtil.getSubcomponentTypeName(
							object.getDataFeatureClassifier(), object)
				    + "test:" + object.getNamespace().getFullName());
			System.err.println("DML_PORT PROPERTIES\n"
					+ getPortProperties(object));
			
			DML_Port_Properties dpp = getPortProperties(object);
			switch(dpp.role) {
			case "responder":
				if(object.getDirection().incoming())
					transformToExchangeModel(object, new GetRequestExchangeMethods(), dpp);
				else
					transformToExchangeModel(object, new GetResponseExchangeMethods(), dpp);
				break;
				
			case "receiver": 
				if(object.getDirection().incoming())
					transformToExchangeModel(object, new SetSendExchangeMethods(), dpp);
				else
					transformToExchangeModel(object, new SetReceiveExchangeMethods(), dpp);
				break;
				
			case "publisher": //TODO: Seems there is no distinction between sporadic and periodic at this moment. So treat everything periodic
				transformToExchangeModel(object, new PublishPeriodicExchangeMethods(), dpp);
				break;
				
			case "executor": 
				if(object.getDirection().incoming())
					transformToExchangeModel(object, new ActionInitiatorExchangeMethods(), dpp);
				else
					transformToExchangeModel(object, new ActionExecutorExchangeMethods(), dpp);
				break;
			
			case "requester":
			case "sender":
			case "subscriber":
			case "initiator":
				handleException(object,
						new Exception("Invalid Communication Pattern for a Device:"
								+ object.getFullName()));	
				break;
			
			}
			
			return DONE;
		}

		@Override
		public String caseEventPort(EventPort object) {
			processComments(object);
			System.err.println("caseEventPort:" + object.getFullName() + "("
					+ object.getDirection().getName() + ")");
			System.err.println("DML_PORT PROPERTIES\n"
					+ getPortProperties(object));
			DML_Port_Properties dpp = getPortProperties(object);
			switch(dpp.role) {
			case "responder":
				if(object.getDirection().incoming())
					transformToExchangeModel(object, new GetRequestExchangeMethods(), dpp);
				else
					transformToExchangeModel(object, new GetResponseExchangeMethods(), dpp);
				break;
				
			case "receiver": 
				if(object.getDirection().incoming())
					transformToExchangeModel(object, new SetSendExchangeMethods(), dpp);
				else
					transformToExchangeModel(object, new SetReceiveExchangeMethods(), dpp);
				break;
				
			case "publisher": //TODO: Seems there is no distinction between sporadic and periodic at this moment. So treat everything periodic
				transformToExchangeModel(object, new PublishPeriodicExchangeMethods(), dpp);
				break;
				
			case "executor": 
				if(object.getDirection().incoming())
					transformToExchangeModel(object, new ActionInitiatorExchangeMethods(), dpp);
				else
					transformToExchangeModel(object, new ActionExecutorExchangeMethods(), dpp);
				break;
			
			case "requester":
			case "sender":
			case "subscriber":
			case "initiator":
				handleException(object,
						new Exception("Invalid Communication Pattern for a Device:"
								+ object.getFullName()));	
				break;
			
			}
			
			return DONE;
		}


		public void processOptionalSection(EList<Feature> list, String sectionName,
				String emptyOption) {
			if (list == null)
				return;
			if (!list.isEmpty()) {
				processEList(list);
			} else if (emptyOption != null && emptyOption.length() > 0) {
			}
		}

		private void processComments(final Element obj) {
			if (obj != null) {
				EList<Comment> el = obj.getOwnedComments();
				if (!el.isEmpty()) {
					for (Comment comment : el) {
						String str = comment.getBody();
						if (!str.startsWith("--")) {
							str = "--" + (str.startsWith(" ") ? "" : " ") + str;
						}
						// aadlText.addOutputNewline(str);
					}
				} else {
					// see if there are comments in the parse tree
					processComment(obj);
				}
			}
		}

		public void processComment(EObject o) {
			//INode node = NodeModelUtils.findActualNodeFor(o);
			INode n2 = NodeModelUtils.getNode(o);
			if (n2 == null)
				return;
			BidiTreeIterator<INode> ti = n2.getAsTreeIterable().iterator();
			while (ti.hasNext()) {
				INode next = ti.next();
				if (next instanceof CompositeNode && next != n2)
					return;
				if (isCommentNode(next)) {
					String str = next.getText();
					if (!str.startsWith("--")) {
						str = "--" + (str.startsWith(" ") ? "" : " ") + str;
					}
					if (str.endsWith("\r\n")) {
						str = str.substring(0, str.length() - 2);
					}
				}
			}
		}

		public boolean isCommentNode(INode node) {
			if (node instanceof ILeafNode && ((ILeafNode) node).isHidden()
					&& node.getGrammarElement() instanceof AbstractRule)
				return isComment((AbstractRule) node.getGrammarElement());
			return false;
		}

		public boolean isComment(AbstractRule rule) {
			return rule != null
					&& ("ML_COMMENT".equals(rule.getName()) || "SL_COMMENT"
							.equals(rule.getName()));
		}

		// Extract Property for MDCF_Data_Props::IEEE11073_MDC_ATTR_SYS_TYPE
		private void assignSystemType(ModalPropertyValue modalPropertyValue) {

			if (modalPropertyValue.getOwnedValue() instanceof StringLiteral) {
				StringLiteral sl = (StringLiteral) modalPropertyValue
						.getOwnedValue();
				deviceComponentModel.setDeviceType(sl.getValue());
			} else {
				Exception e = new NotImplementedException(
						"Illegal format for IEEE11073_MDC_ATTR_SYS_TYPE:"
								+ "Expecting StringLiteralImpl but "
								+ modalPropertyValue.getOwnedValue());
				log.log(Level.SEVERE, e.toString(), e);
				handleException(modalPropertyValue, e);
			}
		}

		// Extract Property for MDCF_Data_Props::manufacturerModel
		private void assignManufacturerModel(
				EList<ModalPropertyValue> ownedValues) {
			if (ownedValues.size() != 1
					|| !(ownedValues.get(0).getOwnedValue() instanceof RecordValue)) {
				handleException(ownedValues.get(0).getOwnedValue(),
						new Exception(
								"Invalid Structure Manfacturer Model Property:"
										+ ownedValues.get(0).getOwnedValue()));
				return;
			} else {
				RecordValue rvi = (RecordValue) ownedValues.get(0)
						.getOwnedValue();
				RecordValue pe_model = (RecordValue) PropertyUtils
						.getRecordFieldValue(rvi, "MDC_ATTR_ID_MODEL");
				StringLiteral model_manufacturer = (StringLiteral) PropertyUtils
						.getRecordFieldValue(pe_model, "manufacturer");
				StringLiteral model_number = (StringLiteral) PropertyUtils
						.getRecordFieldValue(pe_model, "model_number");

				if (model_manufacturer == null) {
					handleException(pe_model, new Exception(
							"Missing Manufacturer Name in Manufacturer Model:"
									+ pe_model));
					return;
				} else if (model_number == null) {
					handleException(pe_model, new Exception(
							"Missing Model Number in Manufacturer Model:"
									+ pe_model));
					return;
				}

				deviceComponentModel.setManufacturerName(model_manufacturer
						.getValue());
				deviceComponentModel.setModelNumber(model_number.getValue());

				//PropertyExpression pe_credential = PropertyUtils
				//		.getRecordFieldValue(rvi, "credentials");
			}
		}

	}

	private void transformToExchangeModel(Port object,
			ExchangeMethods exMethods, DML_Port_Properties dpp) {
		String exchangeName = extractExchangeName(object, dpp);

		ExchangeModel em;
		if (exchangeModelExists(exchangeName)) {
			em = getExchangeModel(exchangeName);
		} else {
			em = exMethods.createExchangeModel(exchangeName, object);
		}

		exMethods.populatePortProperty(em, object, dpp);
		updateExchangeModel(exchangeName, em);
		
	}
	
	private boolean exchangeModelExists(String exchangeName) {
		return getExchangeModel(exchangeName) == null ? false : true;
	}

	private ExchangeModel getExchangeModel(String exchangeName) {
		return deviceComponentModel.exchangeModels.get(exchangeName);
	}

	private void updateExchangeModel(String exchangeName, ExchangeModel em) {
		deviceComponentModel.exchangeModels.put(exchangeName, em);
	}
	
	private String extractExchangeName(Port object, DML_Port_Properties dpp) {
		String fullPortName = object.getFullName();
		String[] split_pieces = fullPortName.split("_");
		String suffix = "_" + split_pieces[split_pieces.length - 1];

		return object.getNamespace().getFullName() + "_"
				+ fullPortName.substring(0,
						fullPortName.length() - suffix.length())
				+ "_" + dpp.role;
	}
	
	private String getPortName(Port object) {
		return object.getNamespace().getFullName() + "_" + object.getFullName();
	}
	
	class DML_Port_Properties {
		String role = "";
		int min = 0;
		int max = 0;

		public DML_Port_Properties() {

		}

		public DML_Port_Properties(String role, int min, int max) {
			this.role = role;
			this.min = min;
			this.max = max;
		}

		public String toString() {
			return "Role:" + role + "\n" + "Min:" + min + ", Max:" + max;
		}
	}

	private DML_Port_Properties getPortProperties(Port object) {

		Property pr = GetProperties.lookupPropertyDefinition(object,
				"MDCF_Comm_Props", "DML_Port");
		if (pr == null)
			return new DML_Port_Properties();

		PropertyAcc pa = object.getPropertyValue(pr);

		if (pa.first() == null)
			return new DML_Port_Properties();

		ModalPropertyValue mpv = pa.first().getOwnedValues().get(0); // DML_PORT
		RecordValue rvi = (RecordValue) mpv.getOwnedValue();

		NamedValue comm_role = (NamedValue) PropertyUtils
				.getRecordFieldValue(rvi, "Comm_Role");
		RangeValue output_rate = (RangeValue) PropertyUtils
				.getRecordFieldValue(rvi, "Output_Rate");

		return new DML_Port_Properties(
				convertNamedValueToEnumerationLiteralString(comm_role),
				(int) ((IntegerLiteral) output_rate.getMinimumValue())
						.getValue(),
				(int) ((IntegerLiteral) output_rate.getMaximumValue())
						.getValue());
	}

	private String convertNamedValueToEnumerationLiteralString(NamedValue nv) {
		if (nv == null)
			return "";
		AbstractNamedValue anv = nv.getNamedValue();
		if (anv instanceof EnumerationLiteral)
			return ((EnumerationLiteral) anv).getName();
		else
			return "";
	}

	
/*	// Extract Property of MDCF_Comm_Props::DML_Port
	private void extractDMLPortProperties(EList<ModalPropertyValue> ownedValues) {
		if (ownedValues.size() != 1
				|| !(ownedValues.get(0).getOwnedValue() instanceof RecordValue)) {
			handleException(ownedValues.get(0).getOwnedValue(), new Exception(
					"Invalid Structure DML Port Property:"
							+ ownedValues.get(0).getOwnedValue()));
			return;
		} else {
			RecordValue rvi = (RecordValue) ownedValues.get(0).getOwnedValue();
			RecordValue pe_model = (RecordValue) PropertyUtils
					.getRecordFieldValue(rvi, "DML_Port");
			StringLiteral model_manufacturer = (StringLiteral) PropertyUtils
					.getRecordFieldValue(pe_model, "Comm_Role");
			StringLiteral model_number = (StringLiteral) PropertyUtils
					.getRecordFieldValue(pe_model, "Output_Rate");

			if (model_manufacturer == null) {
				handleException(pe_model, new Exception(
						"Missing Manufacturer Name in Manufacturer Model:"
								+ pe_model));
				return;
			} else if (model_number == null) {
				handleException(pe_model, new Exception(
						"Missing Model Number in Manufacturer Model:"
								+ pe_model));
				return;
			}
		}

	}*/
	
	//TODO:
	private String getCurrentProcessingVMDName() {
		return systemName; //don't consider for multiple VMDs for now
	}
	
	//TODO:
	public String extractParameterName(Port object) {
		return object.getNamespace().getFullName();
	}
	
	//TODO:
	private String extractActionName(String fullPortName) {
		String[] split_pieces = fullPortName.split("_");
		String suffix = "_" + split_pieces[split_pieces.length - 1];

		return fullPortName.substring(0,
				fullPortName.length() - suffix.length());
	}
	
	private int getMaxSeperationIntervalFromRange(EventDataPort object) {
		RangeValue rv = getSeperationIntervalRange(object);
		if (rv != null) {
			IntegerLiteral max = (IntegerLiteral) rv.getMaximumValue();

			log.log(Level.FINE, "Seperation Interval Max " + max.getValue());

			return (int) max.getValue();
		} else {
			// TODO: if it is mandatory, process error
			return 0;
		}

	}

	private int getMinSeperationIntervalFromRange(EventDataPort object) {
		RangeValue rv = getSeperationIntervalRange(object);
		if (rv != null) {
			IntegerLiteral min = (IntegerLiteral) rv.getMinimumValue();
			log.log(Level.FINE, "Seperation Interval Min " + min.getValue());
			return (int) min.getValue();
		} else {
			// TODO: if it is mandatory, process error
			return 0;
		}
	}
	
	private RangeValue getSeperationIntervalRange(EventDataPort object) {
		Property pr = GetProperties.lookupPropertyDefinition(object,
				"MDCF_Comm_Props", "separation_interval_range");
		if (pr == null)
			return null;

		PropertyAcc pa = object.getPropertyValue(pr);
		ModalPropertyValue mpv = pa.first().getOwnedValues().get(0);
		if (mpv.getOwnedValue() instanceof RangeValue) {
			return (RangeValue) mpv.getOwnedValue();
		} else
			return null;
	}
	
	class GetRequestExchangeMethods implements ExchangeMethods{

		@Override
		public ExchangeModel createExchangeModel(String exchangeName,
				Port object) {
			return new GetExchangeModel(extractParameterName(object),
					systemName, getCurrentProcessingVMDName(), exchangeName);
		}

		@Override
		public void populatePortProperty(ExchangeModel em, Port object, DML_Port_Properties dpp) {
			PortInfoModel portInfo = em.getInPortInfo();
			if (portInfo == null) {
				portInfo = new PortInfoModel(PortDirection.In,
						getPortName(object));
				portInfo.setMaxSeparationInterval(dpp.max);
				portInfo.setMinSeparationInterval(dpp.min);
				
				em.setInPortInfo(portInfo);
			} else {
				handleException(object,
						new Exception(
								"Can't have multiple in ports for one Exchange:"
										+ portInfo.getPortName() + " and "
										+ object.getFullName()));
			}
		}

	}
	
	class GetResponseExchangeMethods implements ExchangeMethods{

		@Override
		public ExchangeModel createExchangeModel(String exchangeName,
				Port object) {
			return new GetExchangeModel(extractParameterName(object),
					systemName, getCurrentProcessingVMDName(), exchangeName);
		}

		@Override
		public void populatePortProperty(ExchangeModel em, Port object, DML_Port_Properties dpp) {
			PortInfoModel portInfo = em.getOutPortInfo();
			if (portInfo == null) {
				portInfo = new PortInfoModel(PortDirection.Out,
						getPortName(object));

				// process data type
				if(object instanceof EventDataPort){
					EventDataPort edp = (EventDataPort) object; //TODO
					String dataType = AadlUtil.getSubcomponentTypeName(
						edp.getDataFeatureClassifier(), object);
					if (!dataType.equals("")) {
						String javaType = PortDataTypeMap
								.getJavaTypeString(dataType);
						if (javaType == null) {
							handleException(object, new Exception(
									"Unknown Port Data Type:No Java Type Known:"
											+ object.getFullName()));
						} else {
							portInfo.setMessageType(javaType);
						}
					} else {
						handleException(object,
								new Exception("Missing Data Type for the Port:"
										+ object.getFullName()));
					}
				}
				em.setOutPortInfo(portInfo);
			} else {
				handleException(object,
						new Exception(
								"Can't have multiple out ports for one Exchange:"
										+ portInfo.getPortName() + " and "
										+ object.getFullName()));
			}
		}
		
	}
	
	class SetReceiveExchangeMethods implements ExchangeMethods {

		@Override
		public ExchangeModel createExchangeModel(String exchangeName,
				Port object) {
			return new SetExchangeModel(extractParameterName(object),
					systemName, getCurrentProcessingVMDName(), exchangeName);
		}

		@Override
		public void populatePortProperty(ExchangeModel em, Port object, DML_Port_Properties dpp) {
			PortInfoModel portInfo = em.getOutPortInfo();
			if (portInfo == null) {
				portInfo = new PortInfoModel(PortDirection.Out,
						getPortName(object));
				em.setOutPortInfo(portInfo);
			} else {
				handleException(object,
						new Exception(
								"Can't have multiple in ports for one Exchange:"
										+ portInfo.getPortName() + " and "
										+ object.getFullName()));
			}
		}
	}
	
	class SetSendExchangeMethods implements ExchangeMethods {

		@Override
		public ExchangeModel createExchangeModel(String exchangeName,
				Port object) {
			return new SetExchangeModel(extractParameterName(object),
					systemName, getCurrentProcessingVMDName(), exchangeName);
		}

		@Override
		public void populatePortProperty(ExchangeModel em, Port object, DML_Port_Properties dpp) {
			PortInfoModel portInfo = em.getInPortInfo();

			if (portInfo == null) {
				portInfo = new PortInfoModel(PortDirection.In,
						getPortName(object));

				portInfo.setMaxSeparationInterval(dpp.max);
				portInfo.setMinSeparationInterval(dpp.min);

				// process data type
				if(object instanceof EventDataPort){
					EventDataPort edp = (EventDataPort) object; //TODO
					String dataType = AadlUtil.getSubcomponentTypeName(
							edp.getDataFeatureClassifier(), object);
					if (!dataType.equals("")) {
						String javaType = PortDataTypeMap
								.getJavaTypeString(dataType);
						if (javaType == null) {
							handleException(object, new Exception(
									"Unknown Port Data Type:No Java Type Known:"
											+ object.getFullName()));
						} else {
							portInfo.setMessageType(javaType);
						}
					} else {
						handleException(object,
								new Exception("Missing Data Type for the Port:"
										+ object.getFullName()));
					}
				}

				em.setInPortInfo(portInfo);
			} else {
				handleException(object,
						new Exception(
								"Can't have multiple out ports for one Exchange:"
										+ portInfo.getPortName() + " and "
										+ object.getFullName()));
			}
		}
	}
	
	class PublishSporadicExchangeMethods implements ExchangeMethods{

		@Override
		public ExchangeModel createExchangeModel(String exchangeName,
				Port object) {
				return new SporadicExchangeModel(extractParameterName(object),
						systemName, getCurrentProcessingVMDName(),
						exchangeName);
		}

		@Override
		public void populatePortProperty(ExchangeModel em, Port object, DML_Port_Properties dpp) {
			PortInfoModel portInfo = em.getOutPortInfo();
			if (portInfo == null) {
				portInfo = new PortInfoModel(PortDirection.Out,
						getPortName(object));
				em.setOutPortInfo(portInfo);
			} else {
				handleException(object,
						new Exception(
								"Can't have multiple out ports for one Exchange:"
										+ portInfo.getPortName() + " and "
										+ object.getFullName()));
			}

			// process data type
			if(object instanceof EventDataPort){
				EventDataPort edp = (EventDataPort) object; //TODO
				String dataType = AadlUtil.getSubcomponentTypeName(
						edp.getDataFeatureClassifier(), object);
				if (!dataType.equals("")) {
					String javaType = PortDataTypeMap.getJavaTypeString(dataType);
					if (javaType == null) {
						handleException(object, new Exception(
								"Unknown Port Data Type:No Java Type Known:"
										+ object.getFullName()));
					} else {
						portInfo.setMessageType(javaType);
					}
				} else {
					handleException(object,
							new Exception("Missing Data Type for the Port:"
									+ object.getFullName()));
				}
			}
			
			portInfo.setSeparationInterval(dpp.min);//TODO
		}
		
	}
	
	class PublishPeriodicExchangeMethods implements ExchangeMethods{

		@Override
		public ExchangeModel createExchangeModel(String exchangeName,
				Port object) {
			return new PeriodicExchangeModel(
					extractParameterName(object),
					systemName, getCurrentProcessingVMDName(),
					exchangeName);
		}

		@Override
		public void populatePortProperty(ExchangeModel em, Port object, DML_Port_Properties dpp) {
			PortInfoModel portInfo = em.getOutPortInfo();
			if (portInfo == null) {
				portInfo = new PortInfoModel(PortDirection.Out,
						getPortName(object));
				em.setOutPortInfo(portInfo);
			} else {
				handleException(object,
						new Exception(
								"Can't have multiple out ports for one Exchange:"
										+ portInfo.getPortName() + " and "
										+ object.getFullName()));
			}

			// process data type
			if(object instanceof EventDataPort){
				EventDataPort edp = (EventDataPort) object;//TODO
				String dataType = AadlUtil.getSubcomponentTypeName(
						edp.getDataFeatureClassifier(), object);
				if (!dataType.equals("")) {
					String javaType = PortDataTypeMap.getJavaTypeString(dataType);
					if (javaType == null) {
						handleException(object, new Exception(
								"Unknown Port Data Type:No Java Type Known:"
										+ object.getFullName()));
					} else {
						portInfo.setMessageType(javaType);
					}
				} else {
					handleException(object,
							new Exception("Missing Data Type for the Port:"
									+ object.getFullName()));
				}
			}

			portInfo.setMaxSeparationInterval(dpp.max);
			portInfo.setMinSeparationInterval(dpp.min);
		}
		
	}
	
	class ActionInitiatorExchangeMethods implements ExchangeMethods {

		@Override
		public ExchangeModel createExchangeModel(String exchangeName,
				Port object) {
			return new ActionExchangeModel(
					extractActionName(object.getFullName()), systemName,
					getCurrentProcessingVMDName(), exchangeName);
		}

		@Override
		public void populatePortProperty(ExchangeModel em, Port object, DML_Port_Properties dpp) {
			PortInfoModel portInfo = em.getInPortInfo();
			if (portInfo == null) {
				portInfo = new PortInfoModel(PortDirection.In,
						getPortName(object));

				portInfo.setMaxSeparationInterval(dpp.max);
				portInfo.setMinSeparationInterval(dpp.min);

				em.setInPortInfo(portInfo);
			} else {
				handleException(object,
						new Exception(
								"Can't have multiple out ports for one Exchange:"
										+ portInfo.getPortName() + " and "
										+ object.getFullName()));
			}
		}
		
	}
	
	class ActionExecutorExchangeMethods implements ExchangeMethods {

		@Override
		public ExchangeModel createExchangeModel(String exchangeName,
				Port object) {
			return new ActionExchangeModel(
					extractActionName(object.getFullName()), systemName,
					getCurrentProcessingVMDName(), exchangeName);
		}

		@Override
		public void populatePortProperty(ExchangeModel em, Port object, DML_Port_Properties dpp) {
			PortInfoModel portInfo = em.getOutPortInfo();
			if (portInfo == null) {
				portInfo = new PortInfoModel(PortDirection.Out,
						getPortName(object));
				em.setOutPortInfo(portInfo);
			} else {
				handleException(object,
						new Exception(
								"Can't have multiple in ports for one Exchange:"
										+ portInfo.getPortName() + " and "
										+ object.getFullName()));
			}
		}
	}
	
	@Override
	protected void initSwitches() {
		aadl2Switch = new TranslatorSwitch();
	}

	public void addPropertySetName(String propSetName) {
		propertySetNames.add(propSetName);
	}

	public void setErrorManager(ParseErrorReporterManager parseErrManager) {
		errorManager = parseErrManager;
	}

	public DeviceComponentModel getDeviceComponentModel() {
		return this.deviceComponentModel;
	}

	public NewDeviceTranslator(final IProgressMonitor monitor) {
		super(monitor, PROCESS_PRE_ORDER_ALL);
		deviceComponentModel = new DeviceComponentModel();
		log.setUseParentHandlers(false);
	}

	private void handleException(Element obj, Exception e) {
		INode node = NodeModelUtils.findActualNodeFor(obj);
		IResource file = OsateResourceUtil.convertToIResource(obj.eResource());
		ParseErrorReporter errReporter = errorManager.getReporter(file);
		if (errReporter instanceof MarkerParseErrorReporter)
			((MarkerParseErrorReporter) errReporter).setContextResource(obj
					.eResource());
		errReporter.error(obj.eResource().getURI().lastSegment(),
				node.getStartLine(), e.getMessage());
		cancelTraversal();
	}
	
	private interface ExchangeMethods {
		ExchangeModel createExchangeModel(String exchangeName, Port object);
		void populatePortProperty(ExchangeModel em, Port object, DML_Port_Properties dpp);
	}
}
