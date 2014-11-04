package edu.ksu.cis.projects.mdcf.aadltranslator;

import java.util.ArrayList;
import java.util.logging.*;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.osate.aadl2.*;
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

import edu.ksu.cis.projects.mdcf.aadltranslator.exception.NotImplementedException;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.ActionExchangeModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.DeviceComponentModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.ExchangeModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.GetExchangeModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.PeriodicExchangeModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.PortDataTypeMap;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.PortInfoModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.PortInfoModel.PortDirection;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.SetExchangeModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.SporadicExchangeModel;

public final class DeviceTranslator extends AadlProcessingSwitchWithProgress {
	private static final Logger log = Logger.getLogger(DeviceTranslator.class
			.getName());

	private enum ElementType {
		NONE, SYSTEM_IMPL, DEVICE_ABSTRACT
	};

	private enum CommPatternType {
		REQUEST("_res_in", 1), RESPONSE("_res_out", 2), SEND("_rec_in", 3), RECEIVE(
				"_rec_out", 4), PUBLISH("_pub_out", 5), INITIATOR("_exec_in", 6), EXECUTOR(
				"_exec_out", 7);

		private final String suffix;
		private final int id;

		private CommPatternType(final String suffix, int id) {
			this.suffix = suffix;
			this.id = id;
		}

		public String suffix() {
			return this.suffix;
		}

		@SuppressWarnings("unused")
		public int ID() {
			return this.id;
		}
	}

	private ArrayList<String> propertySetNames = new ArrayList<>();
	private ParseErrorReporterManager errorManager;
	private DeviceComponentModel deviceComponentModel = null;
	private String systemName = null;
	private int systemCount = 0;
	private int systemImpCount = 0;
	private ArrayList<String> vmdTypeNames = new ArrayList<String>();
	private ArrayList<String> vmdTypeDefNames = new ArrayList<String>();
	private String packageName;

	public class TranslatorSwitch extends Aadl2Switch<String> {

		private ElementType lastElemProcessed = ElementType.NONE;

		@Override
		public String casePort(Port obj) {
			log.log(Level.FINE, "casePort:" + obj.getFullName());
			if (obj.getCategory() == PortCategory.EVENT_DATA) {
				process(obj);
			} else if (obj.getCategory() == PortCategory.DATA) {
				// Not processed in device component
				handleException(obj, new Exception(
						"Data Port is not expected in the Device AADL"));
				return DONE;
			} else if (obj.getCategory() == PortCategory.EVENT) {
				// Not processed in device component
				handleException(obj, new Exception(
						"Event Port is not expected in the Device AADL"));
				return DONE;
			}

			return DONE;
		}

		@Override
		public String caseAadlPackage(AadlPackage obj) {

			for (Element e : obj.getOwnedPublicSection().getOwnedElements()) {

				if (e instanceof SystemType) { 
					packageName = obj.getName();
					process(e);
				} else if (e instanceof SystemImplementation) {
					process(e);
				} else if (e instanceof AbstractType) {
					// All the port information reside in the abstract type.
					// Process it.
					process(e);
				} else {
					log.log(Level.FINE, "getOwnedPublicSection:Ignored:" + e);
				}
			}

			if (systemImpCount == 0) {
				handleException(obj, new Exception(
						"Missing System Implementation:" + obj.getFullName()));
				return DONE;
			}

			// Checking the package contains the necessary VMD type definition.
			// This is actually redundant since AADL compiler will complain if
			// the definition is missing.
			for (String typeName : vmdTypeNames) {
				if (!vmdTypeDefNames.contains(typeName)) {
					handleException(obj, new Exception(
							"Missing VMD type definition:" + obj.getFullName()));
					return DONE;
				}
			}

			// Distribute exchanges
			deviceComponentModel.distributeExchanges();

			return DONE;
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

		// Extract Property for MDCF_Data_Props::ICE_ManufacturerModel
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

				PropertyExpression pe_credential = PropertyUtils
						.getRecordFieldValue(rvi, "credentials");

				if (pe_credential == null) {
					handleException(rvi, new Exception("Missing Credential:"
							+ rvi));
					return;
				}

				ListValue lv_credential = (ListValue) pe_credential;

				// TODO: This keeps returning empty even when there is
				// credentials
				// if(lv_credential.getOwnedElements().isEmpty()){
				// handleException(lv_credential,
				// new Exception("No Credential:"
				// + lv_credential));
				// return;
				// }

				for (PropertyExpression cred : lv_credential
						.getOwnedListElements()) {
					StringLiteral sl_cred = (StringLiteral) cred;
					deviceComponentModel.addCredential(sl_cred.getValue());
				}
			}
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
		public String caseSystemImplementation(SystemImplementation si){
			// find system implementation and see whether the name matches
			// with the system type,
			// if it doesn't trigger error.
			if (systemImpCount > 1) {
				handleException(
						si,
						new Exception(
								"Only one system implementation is allowed in device AADL."));
				return DONE;
			} else {
				log.log(Level.FINE, "System Implementation Reading:"
						+ si.getFullName());
				systemImpCount++;
			}

			// Get the properties for the system.
			boolean systemTypeExist = false;
			boolean manufacturerModelExist = false;
			for (PropertyAssociation pa : si
					.getOwnedPropertyAssociations()) {
				String propertyName = pa.getProperty().getFullName();
				if (propertyName.equals("IEEE11073_MDC_ATTR_SYS_TYPE")) {
					assignSystemType(pa.getOwnedValues().get(0));
					systemTypeExist = true;
				} else if (propertyName.equals("ICE_ManufacturerModel")) {
					assignManufacturerModel(pa.getOwnedValues());
					manufacturerModelExist = true;
				}
			}

			if (!systemTypeExist) {
				handleException(si,
						new Exception("Missing System Type Property:"
								+ si.getFullName()));
				return DONE;
			} else if (!manufacturerModelExist) {
				handleException(si,
						new Exception(
								"Missing ManufacturerModel Property:"
										+ si.getFullName()));
				return DONE;
			}

			// There could be more than one vmds in a device
			
			for (AbstractSubcomponent asc : si.getOwnedAbstractSubcomponents()) {
				vmdTypeNames.add(asc.getAbstractSubcomponentType()
						.getFullName());
			}
			return DONE;
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.osate.aadl2.util.Aadl2Switch#caseEventDataPort(org.osate.aadl2
		 * .EventDataPort)
		 */
		@Override
		public String caseEventDataPort(EventDataPort object) {
			if (this.lastElemProcessed == ElementType.DEVICE_ABSTRACT) {
				log.log(Level.FINE,
						"caseEventDataPort"
								+ "("
								+ vmdTypeDefNames.get(vmdTypeDefNames.size() - 1)
								+ ")" + ":" + object.getFullName() + ":"
								+ object.getDirection());
				CommPatternType commType = decideCommPattern(object
						.getFullName());
				if (commType == null) {
					handleException(object,
							new Exception("Unknown Communication Pattern:"
									+ object.getFullName()));
					return DONE;
				}

				switch (CommPatternType.valueOf(commType.name())) {
				case REQUEST:
					transformToExchangeModel(object, new GetRequestExchangeMethods());
					break;
				case RESPONSE:
					transformToExchangeModel(object, new GetResponseExchangeMethods());
					break;
				case SEND:
					transformToExchangeModel(object, new SetSendExchangeMethods());
					break;
				case RECEIVE:
					transformToExchangeModel(object, new SetReceiveExchangeMethods());
					break;

				case PUBLISH:
					boolean sporadic = isSporadic(object);			
					if(sporadic)
						transformToExchangeModel(object, new PublishSporadicExchangeMethods());
					else
						transformToExchangeModel(object, new PublishPeriodicExchangeMethods());
					break;

				case INITIATOR: 					
					transformToExchangeModel(object, new ActionInitiatorExchangeMethods());
					break;

				case EXECUTOR:
					transformToExchangeModel(object, new ActionExecutorExchangeMethods());
					break;
				default:
					log.log(Level.SEVERE, "Unhandled Communication Pattern:"
							+ CommPatternType.valueOf(commType.name()));
					break;
				}

			} else {
				handleException(object, new Exception(
						"Only Abstract Device type contains port information:"
								+ lastElemProcessed));
				return DONE;
			}
			return DONE;
		}

		private boolean isSporadic(EventDataPort object) {
			PropertyAcc separation_interval = object
					.getPropertyValue(GetProperties.lookupPropertyDefinition(
							object, "MDCF_Comm_Props", "separation_interval"));
			PropertyAcc separation_interval_range = object
					.getPropertyValue(GetProperties.lookupPropertyDefinition(
							object, "MDCF_Comm_Props",
							"separation_interval_range"));

			if (separation_interval.first() == null
					&& separation_interval_range.first() == null) {
				handleException(object, new Exception(
						"Both separation_interval and separation_interval_range are absent:"
								+ object.getFullName()));
			} else if (separation_interval.first() != null
					&& separation_interval_range.first() != null) {
				handleException(object, new Exception(
						"Only separation_interval or separation_interval_range should be present:"
								+ object.getFullName()));
			}

			if (separation_interval.first() != null) {
				return true;
			} else {
				return false;
			}
		}

		private void transformToExchangeModel(EventDataPort object,
				ExchangeMethods exMethods) {
			String exchangeName = extractExchangeName(object);

			ExchangeModel em;
			if (exchangeModelExists(exchangeName)) {
				em = getExchangeModel(exchangeName);
			} else {
				em = exMethods.createExchangeModel(exchangeName, object);
			}

			exMethods.populatePortProperty(em, object);
			updateExchangeModel(exchangeName, em);
			
		}

		private void updateExchangeModel(String exchangeName, ExchangeModel em) {
			deviceComponentModel.exchangeModels.put(exchangeName, em);
		}

		private boolean exchangeModelExists(String exchangeName) {
			return getExchangeModel(exchangeName) == null ? false : true;
		}

		private ExchangeModel getExchangeModel(String exchangeName) {
			return deviceComponentModel.exchangeModels.get(exchangeName);
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

		private IntegerLiteral getSeperationInterval(EventDataPort object) {
			Property pr = GetProperties.lookupPropertyDefinition(object,
					"MDCF_Comm_Props", "separation_interval");
			if (pr == null)
				return null;

			PropertyAcc pa = object.getPropertyValue(pr);
			ModalPropertyValue mpv = pa.first().getOwnedValues().get(0);
			if (mpv.getOwnedValue() instanceof IntegerLiteral) {
				return (IntegerLiteral) mpv.getOwnedValue();
			} else
				return null;
		}

		public String extractParameterName(EventDataPort object) {
			Property pr = GetProperties.lookupPropertyDefinition(object,
					"MDCF_ICE_Props", "data_type");
			if (pr == null)
				return null;

			PropertyAcc pa = object.getPropertyValue(pr);
			ModalPropertyValue mpv = pa.first().getOwnedValues().get(0);
			if (mpv.getOwnedValue() instanceof StringLiteral) {
				return ((StringLiteral) mpv.getOwnedValue()).getValue();
			} else
				return null;

		}

		private String extractActionName(String fullPortName) {
			String[] split_pieces = fullPortName.split("_");
			String suffix = "_" + split_pieces[split_pieces.length - 1];

			return fullPortName.substring(0,
					fullPortName.length() - suffix.length());
		}

		private String extractExchangeName(EventDataPort object) {
			String fullPortName = object.getFullName();
			String[] split_pieces = fullPortName.split("_");
			String suffix = "_" + split_pieces[split_pieces.length - 2] + "_" + split_pieces[split_pieces.length - 1]; //Take last two terms for matching

			String exchange_suffix = "";
			if (suffix.equals(CommPatternType.RECEIVE.suffix)
					|| suffix.equals(CommPatternType.SEND.suffix)) {
				exchange_suffix = "_set";
			} else if (suffix.equals(CommPatternType.REQUEST.suffix)
					|| suffix.equals(CommPatternType.RESPONSE.suffix)) {
				exchange_suffix = "_get";
			} else if (suffix.equals(CommPatternType.INITIATOR.suffix)
					|| suffix.equals(CommPatternType.EXECUTOR.suffix)) {
				exchange_suffix = "_action";
			} else {
				exchange_suffix = "_pub_out";
			}

			return fullPortName.substring(0,
					fullPortName.length() - suffix.length())
					+ exchange_suffix;
		}

		private String getCurrentProcessingVMDName() {
			return vmdTypeNames.get(vmdTypeNames.size() - 1);
		}

		private CommPatternType decideCommPattern(String portName) {
			if (portName.endsWith(CommPatternType.REQUEST.suffix())) {
				return CommPatternType.REQUEST;
			} else if (portName.endsWith(CommPatternType.RESPONSE.suffix())) {
				return CommPatternType.RESPONSE;
			} else if (portName.endsWith(CommPatternType.SEND.suffix())) {
				return CommPatternType.SEND;
			} else if (portName.endsWith(CommPatternType.RECEIVE.suffix())) {
				return CommPatternType.RECEIVE;
			} else if (portName.endsWith(CommPatternType.PUBLISH.suffix())) {
				return CommPatternType.PUBLISH;
			} else if (portName.endsWith(CommPatternType.INITIATOR.suffix())) {
				return CommPatternType.INITIATOR;
			} else if (portName.endsWith(CommPatternType.EXECUTOR.suffix())) {
				return CommPatternType.EXECUTOR;
			}
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.osate.aadl2.util.Aadl2Switch#caseAbstractType(org.osate.aadl2
		 * .AbstractType)
		 */
		@Override
		public String caseAbstractType(AbstractType object) {
			log.log(Level.FINE, "AbstractTypeImpl:" + object.getFullName());
			vmdTypeDefNames.add(object.getFullName());

			lastElemProcessed = ElementType.DEVICE_ABSTRACT;

			processEList(object.getAllFeatures());

			// Check whether the exchanges are valid (e.g. matching pairs for
			// req/res, send/recv, init/exec)
			String checkReport = deviceComponentModel
					.sanityCheckExchanges(object.getFullName());
			if (!checkReport.equals("")) {
				handleException(object, new Exception(checkReport));
				return DONE;
			}

			lastElemProcessed = ElementType.NONE;
			return DONE;
		}
		
		
		class DML_Port_Properties {
			String channel = "";
			String channelGroup = "";
			String payloadType = "";
			String role = "";
			
			public DML_Port_Properties() {
				
			}
			
			public DML_Port_Properties(String channel, String channelGroup, String payloadType, String role) {
				this.channel = channel;
				this.channelGroup = channelGroup;
				this.payloadType = payloadType;
				this.role = role;
			}

			public String toString(){
				return "Channel:" + channel + " in " + this.channelGroup + "\n"
						+ "Payload:" + payloadType + "\n"
						+ "Role:" + role;
			}
		}
		
		private DML_Port_Properties getCommunicationProperties(
				EventDataPort object) {
			
			Property pr = GetProperties.lookupPropertyDefinition(object,
					"MDCF_Comm_Props", "DML_Port");
			if (pr == null)
				return new DML_Port_Properties();
			
			PropertyAcc pa = object.getPropertyValue(pr);
			
			if(pa.first() == null)
				return new DML_Port_Properties();
			
			ModalPropertyValue mpv = pa.first().getOwnedValues().get(0); //DML_PORT
			RecordValue rvi = (RecordValue) mpv
					.getOwnedValue();
			StringLiteral channel = (StringLiteral) PropertyUtils
					.getRecordFieldValue(rvi, "Channel");
			NamedValue channel_group = (NamedValue) PropertyUtils
					.getRecordFieldValue(rvi, "Channel_Group");
			NamedValue payload_type = (NamedValue) PropertyUtils
					.getRecordFieldValue(rvi, "Payload_Type");
			NamedValue comm_role = (NamedValue) PropertyUtils
					.getRecordFieldValue(rvi, "Comm_Role");

			return new DML_Port_Properties(channel.getValue(), 
					convertNamedValueToEnumerationLiteralString(channel_group), 
					convertNamedValueToEnumerationLiteralString(payload_type), 
					convertNamedValueToEnumerationLiteralString(comm_role));
		}
		
		private String convertNamedValueToEnumerationLiteralString(NamedValue nv){
			if(nv == null) return "";
			AbstractNamedValue anv = nv.getNamedValue();
			if(anv instanceof EnumerationLiteral) return ((EnumerationLiteral) anv).getName();
			else return "";
		}
		
		class GetRequestExchangeMethods implements ExchangeMethods{

			@Override
			public ExchangeModel createExchangeModel(String exchangeName,
					EventDataPort object) {
				return new GetExchangeModel(extractParameterName(object),
						systemName, getCurrentProcessingVMDName(), exchangeName);
			}

			@Override
			public void populatePortProperty(ExchangeModel em, EventDataPort object) {
				PortInfoModel portInfo = em.getInPortInfo();
				if (portInfo == null) {
					portInfo = new PortInfoModel(PortDirection.In,
							object.getFullName());
					portInfo.setMaxSeparationInterval(getMaxSeperationIntervalFromRange(object));
					portInfo.setMinSeparationInterval(getMinSeperationIntervalFromRange(object));
					
					DML_Port_Properties dpp = getCommunicationProperties(object);
					
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
					EventDataPort object) {
				return new GetExchangeModel(extractParameterName(object),
						systemName, getCurrentProcessingVMDName(), exchangeName);
			}

			@Override
			public void populatePortProperty(ExchangeModel em, EventDataPort object) {
				PortInfoModel portInfo = em.getOutPortInfo();
				if (portInfo == null) {
					portInfo = new PortInfoModel(PortDirection.Out,
							object.getFullName());

					// process data type
					String dataType = AadlUtil.getSubcomponentTypeName(
							object.getDataFeatureClassifier(), object);
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
					EventDataPort object) {
				return new SetExchangeModel(extractParameterName(object),
						systemName, getCurrentProcessingVMDName(), exchangeName);
			}

			@Override
			public void populatePortProperty(ExchangeModel em, EventDataPort object) {
				PortInfoModel portInfo = em.getOutPortInfo();
				if (portInfo == null) {
					portInfo = new PortInfoModel(PortDirection.Out,
							object.getFullName());
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
					EventDataPort object) {
				return new SetExchangeModel(extractParameterName(object),
						systemName, getCurrentProcessingVMDName(), exchangeName);
			}

			@Override
			public void populatePortProperty(ExchangeModel em, EventDataPort object) {
				PortInfoModel portInfo = em.getInPortInfo();

				if (portInfo == null) {
					portInfo = new PortInfoModel(PortDirection.In,
							object.getFullName());

					RangeValue rv = getSeperationIntervalRange(object);
					if (rv != null) {
						IntegerLiteral max = (IntegerLiteral) rv.getMaximumValue();
						IntegerLiteral min = (IntegerLiteral) rv.getMinimumValue();

						log.log(Level.FINE,
								"Seperation Interval Max " + max.getValue());
						log.log(Level.FINE,
								"Seperation Interval Min " + min.getValue());

						portInfo.setMaxSeparationInterval((int) max.getValue());
						portInfo.setMinSeparationInterval((int) min.getValue());
					} else {
						// TODO: if it is mandatory, process error
					}

					// process data type
					String dataType = AadlUtil.getSubcomponentTypeName(
							object.getDataFeatureClassifier(), object);
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
					EventDataPort object) {
					return new SporadicExchangeModel(extractParameterName(object),
							systemName, getCurrentProcessingVMDName(),
							extractExchangeName(object));
			}

			@Override
			public void populatePortProperty(ExchangeModel em, EventDataPort object) {
				PortInfoModel portInfo = em.getOutPortInfo();
				if (portInfo == null) {
					portInfo = new PortInfoModel(PortDirection.Out,
							object.getFullName());
					em.setOutPortInfo(portInfo);
				} else {
					handleException(object,
							new Exception(
									"Can't have multiple out ports for one Exchange:"
											+ portInfo.getPortName() + " and "
											+ object.getFullName()));
				}

				// process data type
				String dataType = AadlUtil.getSubcomponentTypeName(
						object.getDataFeatureClassifier(), object);
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

				IntegerLiteral il = getSeperationInterval(object);
				if (il != null) {
					log.log(Level.FINE, "Seperation Interval:" + il.getValue());

					portInfo.setSeparationInterval((int) il.getValue());
				}

			}
			
		}
		
		class PublishPeriodicExchangeMethods implements ExchangeMethods{

			@Override
			public ExchangeModel createExchangeModel(String exchangeName,
					EventDataPort object) {
				return new PeriodicExchangeModel(extractParameterName(object),
						systemName, getCurrentProcessingVMDName(),
						extractExchangeName(object));
			}

			@Override
			public void populatePortProperty(ExchangeModel em, EventDataPort object) {
				PortInfoModel portInfo = em.getOutPortInfo();
				if (portInfo == null) {
					portInfo = new PortInfoModel(PortDirection.Out,
							object.getFullName());
					em.setOutPortInfo(portInfo);
				} else {
					handleException(object,
							new Exception(
									"Can't have multiple out ports for one Exchange:"
											+ portInfo.getPortName() + " and "
											+ object.getFullName()));
				}

				// process data type
				String dataType = AadlUtil.getSubcomponentTypeName(
						object.getDataFeatureClassifier(), object);
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

				RangeValue rv = getSeperationIntervalRange(object);
				if (rv != null) {
					IntegerLiteral max = (IntegerLiteral) rv.getMaximumValue();
					IntegerLiteral min = (IntegerLiteral) rv.getMinimumValue();

					log.log(Level.FINE, "Seperation Interval Max:" + max.getValue());
					log.log(Level.FINE, "Seperation Interval Max:" + min.getValue());

					portInfo.setMaxSeparationInterval((int) max.getValue());
					portInfo.setMinSeparationInterval((int) min.getValue());
				} else {
					// TODO: if it is mandatory, process error
				}
			}
			
		}
		
		class ActionInitiatorExchangeMethods implements ExchangeMethods {

			@Override
			public ExchangeModel createExchangeModel(String exchangeName,
					EventDataPort object) {
				return new ActionExchangeModel(
						extractActionName(object.getFullName()), systemName,
						getCurrentProcessingVMDName(), exchangeName);
			}

			@Override
			public void populatePortProperty(ExchangeModel em, EventDataPort object) {
				PortInfoModel portInfo = em.getInPortInfo();
				if (portInfo == null) {
					portInfo = new PortInfoModel(PortDirection.In,
							object.getFullName());

					RangeValue rv = getSeperationIntervalRange(object);
					if (rv != null) {
						IntegerLiteral max = (IntegerLiteral) rv.getMaximumValue();
						IntegerLiteral min = (IntegerLiteral) rv.getMinimumValue();

						log.log(Level.FINE,
								"Seperation Interval Max:" + max.getValue());
						log.log(Level.FINE,
								"Seperation Interval Max:" + min.getValue());

						portInfo.setMaxSeparationInterval((int) max.getValue());
						portInfo.setMinSeparationInterval((int) min.getValue());
					} else {
						// TODO: if it is mandatory, process error
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
		
		class ActionExecutorExchangeMethods implements ExchangeMethods {

			@Override
			public ExchangeModel createExchangeModel(String exchangeName,
					EventDataPort object) {
				return new ActionExchangeModel(
						extractActionName(object.getFullName()), systemName,
						getCurrentProcessingVMDName(), exchangeName);
			}

			@Override
			public void populatePortProperty(ExchangeModel em, EventDataPort object) {
				PortInfoModel portInfo = em.getOutPortInfo();
				if (portInfo == null) {
					portInfo = new PortInfoModel(PortDirection.Out,
							object.getFullName());
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
	}

	public DeviceTranslator(final IProgressMonitor monitor) {
		super(monitor, PROCESS_PRE_ORDER_ALL);
		deviceComponentModel = new DeviceComponentModel();
		log.setUseParentHandlers(false);
	}

	@Override
	protected final void initSwitches() {
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
		ExchangeModel createExchangeModel(String exchangeName, EventDataPort object);
		void populatePortProperty(ExchangeModel em, EventDataPort object);
	}
}
