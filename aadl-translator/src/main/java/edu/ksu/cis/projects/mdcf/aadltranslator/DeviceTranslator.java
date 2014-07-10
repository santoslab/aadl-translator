package edu.ksu.cis.projects.mdcf.aadltranslator;

import java.util.ArrayList;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.osate.aadl2.*;
import org.osate.aadl2.Process;
import org.osate.aadl2.Thread;
import org.osate.aadl2.ThreadGroup;
import org.osate.aadl2.impl.AbstractTypeImpl;
import org.osate.aadl2.impl.DeviceSubcomponentImpl;
import org.osate.aadl2.impl.RealizationImpl;
import org.osate.aadl2.impl.RecordValueImpl;
import org.osate.aadl2.impl.StringLiteralImpl;
import org.osate.aadl2.modelsupport.errorreporting.MarkerParseErrorReporter;
import org.osate.aadl2.modelsupport.errorreporting.ParseErrorReporter;
import org.osate.aadl2.modelsupport.errorreporting.ParseErrorReporterManager;
import org.osate.aadl2.modelsupport.modeltraversal.AadlProcessingSwitchWithProgress;
import org.osate.aadl2.modelsupport.resources.OsateResourceUtil;
import org.osate.aadl2.modelsupport.util.AadlUtil;
import org.osate.aadl2.properties.PropertyAcc;
import org.osate.aadl2.util.Aadl2Switch;
import org.osate.contribution.sei.names.DataModel;
import org.osate.xtext.aadl2.properties.util.GetProperties;
import org.osate.xtext.aadl2.properties.util.PropertyUtils;

import java.lang.System;

import edu.ksu.cis.projects.mdcf.aadltranslator.exception.NotImplementedException;
import edu.ksu.cis.projects.mdcf.aadltranslator.exception.PropertyOutOfRangeException;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.DeviceComponentModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.ExchangeModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.GetExchangeModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.ExchangeModel.ExchangeKind;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.PeriodicExchangeModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.PortInfoModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.PortInfoModel.PortDirection;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.SetExchangeModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.SporadicExchangeModel;

public final class DeviceTranslator extends AadlProcessingSwitchWithProgress {
	private enum ElementType {
		NONE, SYSTEM_IMPL, DEVICE_ABSTRACT
	};

	private enum CommPatternType {
		REQUEST("_req", 1), RESPONSE("_res", 2), SEND("_snd", 3), RECEIVE(
				"_recv", 4), PERIODIC("_period_pub", 5), SPORADIC(
				"_sporadic_pub", 6);

		private final String suffix;
		private final int id;

		private CommPatternType(final String suffix, int id) {
			this.suffix = suffix;
			this.id = id;
		}

		public String suffix() {
			return this.suffix;
		}

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

	public class TranslatorSwitch extends Aadl2Switch<String> {

		private ElementType lastElemProcessed = ElementType.NONE;

		private String DONE = "Done";
		private String NOT_DONE = null;

		@Override
		public String casePort(Port obj) {
			System.err.println("casePort:" + obj.getFullName());

			if (obj.getCategory() == PortCategory.EVENT_DATA) {
				process(obj);
			} else if (obj.getCategory() == PortCategory.DATA) {
				// Not processed in device component
				handleException(obj, new Exception(
						"Data Port is not expected in the Device AADL"));
			} else if (obj.getCategory() == PortCategory.EVENT) {
				// Not processed in device component
				handleException(obj, new Exception(
						"Event Port is not expected in the Device AADL"));
			}

			return DONE;
		}

		@Override
		public String caseAadlPackage(AadlPackage obj) {

			for (Element e : obj.getOwnedPublicSection().getOwnedElements()) {

				if (e instanceof SystemType) { // find system here..if there is
												// more than one system then
												// trigger error.
					SystemType st = (SystemType) e;
					if (systemCount > 1) {
						handleException(e, new Exception(
								"Only one system is allowed in device AADL."));
					} else {
						System.out.println("System Type Reading:"
								+ st.getName());
						systemName = st.getName();
						systemCount++;
					}
					deviceComponentModel.setName(st.getName());

				} else if (e instanceof SystemImplementation) { // find system
																// implementation
																// and see
																// whether the
																// name matches
																// with the
																// system type,
																// if it doesn't
																// trigger
																// error.
					SystemImplementation si = (SystemImplementation) e;
					if (systemImpCount > 1) {
						handleException(
								e,
								new Exception(
										"Only one system implementation is allowed in device AADL."));
					} else {
						System.out.println("System Implementation Reading:"
								+ si.getFullName());
						systemImpCount++;
					}

					// Get the properties for the system.
					for (PropertyAssociation pa : si
							.getOwnedPropertyAssociations()) {
						String propertyName = pa.getProperty().getFullName();
						if (propertyName.equals("IEEE11073_MDC_ATTR_SYS_TYPE")) {
							assignSystemType(pa.getOwnedValues().get(0));
						} else if (propertyName.equals("ICE_ManufacturerModel")) {
							assignManufacturerModel(pa.getOwnedValues());
						}
					}

					// There could be more than one vmds in a device
					for (DeviceSubcomponent dsc : si
							.getOwnedDeviceSubcomponents()) {
						vmdTypeNames.add(dsc.getDeviceSubcomponentType()
								.getFullName());
					}

				} else if (e instanceof AbstractType) {
					// All the port information reside in the abstract type.
					// Process it.
					process(e);
				} else {
					System.err.println("getOwnedPublicSection:Ignored:" + e);
				}

			}

			// Checking the package contains the necessary VMD type definition.
			// This is actually redundant since AADL compiler will complain if
			// the definition is missing.
			for (String typeName : vmdTypeNames) {
				if (!vmdTypeDefNames.contains(typeName))
					handleException(obj, new Exception("VMD:" + typeName
							+ " is not provided in this package."));
			}

			return DONE;
		}

		// Extract Property for MDCF_Data_Props::IEEE11073_MDC_ATTR_SYS_TYPE
		private void assignSystemType(ModalPropertyValue modalPropertyValue) {

			if (modalPropertyValue.getOwnedValue() instanceof StringLiteral) {
				StringLiteral sl = (StringLiteral) modalPropertyValue
						.getOwnedValue();
				deviceComponentModel.setDeviceType(sl.getValue());
			} else {
				System.err
						.println("Illegal format for IEEE11073_MDC_ATTR_SYS_TYPE");
				handleException(modalPropertyValue,
						new NotImplementedException(
								"Illegal format for IEEE11073_MDC_ATTR_SYS_TYPE:"
										+ "Expecting StringLiteralImpl but "
										+ modalPropertyValue.getOwnedValue()));
			}
		}

		// Extract Property for MDCF_Data_Props::ICE_ManufacturerModel
		private void assignManufacturerModel(
				EList<ModalPropertyValue> ownedValues) {
			if (ownedValues.size() != 1
					|| !(ownedValues.get(0).getOwnedValue() instanceof RecordValue)) {
				// exception
			} else {
				RecordValue rvi = (RecordValue) ownedValues.get(0)
						.getOwnedValue();
				RecordValue pe_model = (RecordValue) PropertyUtils
						.getRecordFieldValue(rvi, "MDC_ATTR_ID_MODEL");
				BasicPropertyAssociation bpa = (BasicPropertyAssociation) pe_model
						.getOwnedFieldValues().get(0);
				StringLiteral model_manufacturer = (StringLiteral) PropertyUtils
						.getRecordFieldValue(pe_model, "manufacturer");
				StringLiteral model_number = (StringLiteral) PropertyUtils
						.getRecordFieldValue(pe_model, "model_number");

				deviceComponentModel.setManufacturerName(model_manufacturer
						.getValue());
				deviceComponentModel.setModelNumber(model_number.getValue());

				PropertyExpression pe_credential = PropertyUtils
						.getRecordFieldValue(rvi, "credentials");
				ListValue lv_credential = (ListValue) pe_credential;
				for (PropertyExpression cred : lv_credential
						.getOwnedListElements()) {
					StringLiteral sl_cred = (StringLiteral) cred;
					deviceComponentModel.addCredential(sl_cred.getValue());
				}
			}

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
				System.err.println("caseEventDataPort" + "("
						+ vmdTypeDefNames.get(vmdTypeDefNames.size() - 1) + ")"
						+ ":" + object.getFullName() + ":"
						+ object.getDirection());
				CommPatternType commType = decideCommPattern(object
						.getFullName());
				if (commType == null) {
					handleException(object,
							new Exception("Unknown Communication Pattern:"
									+ object.getFullName()));
					return DONE;
				}

				switch (commType.valueOf(commType.name())) {
				case REQUEST: 
				{
					String exchangeName = extractExchangeName(
							object.getFullName(),
							CommPatternType.REQUEST.suffix);

					// See if the exchange has been registered in the model
					ExchangeModel em = deviceComponentModel.exchangeModels
							.get(exchangeName);
					if (em == null) {// if not create a new one and register
						em = new GetExchangeModel(extractParmeterName(
								object.getFullName(),
								CommPatternType.REQUEST.suffix), // Name of the parameter
								systemName, // System Name
								vmdTypeNames.get(vmdTypeNames.size() - 1), // VMD Name of the exchange
								extractExchangeName(object.getFullName(),
										CommPatternType.REQUEST.suffix) // ExchangeName
						);

						PortInfoModel portInfo = new PortInfoModel(
								PortDirection.In, object.getFullName());
						
						
						
						RangeValue rv = getSeperationIntervalRange(object);
						if(rv != null){
							IntegerLiteral max = (IntegerLiteral) rv.getMaximumValue();
							IntegerLiteral min = (IntegerLiteral) rv.getMinimumValue();

							System.err.println("Seperation Interval Max "  + max.getValue());
							System.err.println("Seperation Interval Min "  + min.getValue());
							
							portInfo.setPortProperty(GetExchangeModel.InPortProperty.MAX_SEPARATION_INTERVAL.name(),
									Long.toString(max.getValue()));
							portInfo.setPortProperty(GetExchangeModel.InPortProperty.MIN_SEPARATION_INTERVAL.name(), 
									Long.toString(min.getValue()));
						} else {
							//TODO: if it is mandatory, process error
						}

						em.setInPortInfo(portInfo);

						deviceComponentModel.exchangeModels.put(exchangeName,
								em);
					} else {
						// if it is retrieve the existing exchange and add the
						// information
						// if there is already something in there, it is an
						// error
						PortInfoModel portInfo = em.getInPortInfo();
						if (portInfo == null) {
							portInfo = new PortInfoModel(PortDirection.In,
									object.getFullName());
							em.setInPortInfo(portInfo);
						} else {
							handleException(object, new Exception(
									"Can't have multiple in ports for one Exchange:"
											+ portInfo.getPortName() + " and "
											+ object.getFullName()));
						}
					}
				}
					break;
				case RESPONSE:
				{
					String exchangeName = extractExchangeName(
							object.getFullName(),
							CommPatternType.RESPONSE.suffix);

					// See if the exchange has been registered in the model
					ExchangeModel em = deviceComponentModel.exchangeModels
							.get(exchangeName);
					if (em == null) {// if not create a new one and register
						em = new GetExchangeModel(extractParmeterName(
								object.getFullName(),
								CommPatternType.RESPONSE.suffix), // Name of the parameter
								systemName, // System Name
								vmdTypeNames.get(vmdTypeNames.size() - 1), // VMD Name of the exchange
								extractExchangeName(object.getFullName(),
										CommPatternType.RESPONSE.suffix) // ExchangeName
						);

						PortInfoModel portInfo = new PortInfoModel(
								PortDirection.Out, object.getFullName());
						em.setOutPortInfo(portInfo);

						deviceComponentModel.exchangeModels.put(exchangeName,
								em);
					} else {
						// if it is retrieve the existing exchange and add the
						// information
						// if there is already something in there, it is an
						// error
						PortInfoModel portInfo = em.getOutPortInfo();
						if (portInfo == null) {
							portInfo = new PortInfoModel(PortDirection.Out,
									object.getFullName());
							
							//process data type
							String dataType = AadlUtil.getSubcomponentTypeName(object.getDataFeatureClassifier(), object);
							if(dataType != null){
								portInfo.setPortProperty(GetExchangeModel.OutPortProperty.MSG_TYPE.name(),
										dataType);
							} else {
								handleException(object, new Exception("Missing Data Type for the Port:" + object.getFullName()));
								return DONE;
							}
							
							em.setOutPortInfo(portInfo);
						} else {
							handleException(object, new Exception(
									"Can't have multiple out ports for one Exchange:"
											+ portInfo.getPortName() + " and "
											+ object.getFullName()));
						}
					}
				}
					break;
				case RECEIVE:
				{
					String exchangeName = extractExchangeName(
							object.getFullName(),
							CommPatternType.RECEIVE.suffix);

					// See if the exchange has been registered in the model
					ExchangeModel em = deviceComponentModel.exchangeModels
							.get(exchangeName);
					if (em == null) {// if not create a new one and register
						em = new SetExchangeModel(extractParmeterName(
								object.getFullName(),
								CommPatternType.RECEIVE.suffix), // Name of the parameter
								systemName, // System Name
								vmdTypeNames.get(vmdTypeNames.size() - 1), // VMD Name of the exchange
								extractExchangeName(object.getFullName(),
										CommPatternType.RECEIVE.suffix) // ExchangeName
						);

						PortInfoModel portInfo = new PortInfoModel(
								PortDirection.In, object.getFullName());
						em.setInPortInfo(portInfo);

						deviceComponentModel.exchangeModels.put(exchangeName,
								em);
					} else {
						// if it is retrieve the existing exchange and add the
						// information
						// if there is already something in there, it is an
						// error
						PortInfoModel portInfo = em.getInPortInfo();
						if (portInfo == null) {
							portInfo = new PortInfoModel(PortDirection.In,
									object.getFullName());
							em.setInPortInfo(portInfo);
						} else {
							handleException(object, new Exception(
									"Can't have multiple in ports for one Exchange:"
											+ portInfo.getPortName() + " and "
											+ object.getFullName()));
						}
					}
				}
					break;
				case SEND:
				{
					String exchangeName = extractExchangeName(
							object.getFullName(),
							CommPatternType.SEND.suffix);

					// See if the exchange has been registered in the model
					ExchangeModel em = deviceComponentModel.exchangeModels
							.get(exchangeName);
					if (em == null) {// if not create a new one and register
						em = new SetExchangeModel(extractParmeterName(
								object.getFullName(),
								CommPatternType.SEND.suffix), // Name of the parameter
								systemName, // System Name
								vmdTypeNames.get(vmdTypeNames.size() - 1), // VMD Name of the exchange
								extractExchangeName(object.getFullName(),
										CommPatternType.SEND.suffix) // ExchangeName
						);

						PortInfoModel portInfo = new PortInfoModel(
								PortDirection.Out, object.getFullName());
						em.setOutPortInfo(portInfo);

						deviceComponentModel.exchangeModels.put(exchangeName,
								em);
					} else {
						// if it is retrieve the existing exchange and add the
						// information
						// if there is already something in there, it is an
						// error
						PortInfoModel portInfo = em.getOutPortInfo();
						if (portInfo == null) {
							portInfo = new PortInfoModel(PortDirection.Out,
									object.getFullName());
							em.setOutPortInfo(portInfo);
						} else {
							handleException(object, new Exception(
									"Can't have multiple out ports for one Exchange:"
											+ portInfo.getPortName() + " and "
											+ object.getFullName()));
						}
					}
					//TODO: process properties and data type

				}
					break;
				case SPORADIC:
				{
					String exchangeName = extractExchangeName(
							object.getFullName(),
							CommPatternType.SPORADIC.suffix);

					// See if the exchange has been registered in the model
					ExchangeModel em = deviceComponentModel.exchangeModels
							.get(exchangeName);
					if (em == null) {// if not create a new one and register
						em = new SporadicExchangeModel(extractParmeterName(
								object.getFullName(),
								CommPatternType.SPORADIC.suffix), // Name of the parameter
								systemName, // System Name
								vmdTypeNames.get(vmdTypeNames.size() - 1), // VMD Name of the exchange
								extractExchangeName(object.getFullName(),
										CommPatternType.SPORADIC.suffix) // ExchangeName
						);

						PortInfoModel portInfo = new PortInfoModel(
								PortDirection.Out, object.getFullName());
						em.setOutPortInfo(portInfo);

						deviceComponentModel.exchangeModels.put(exchangeName,
								em);
					} else {
						// if it is retrieve the existing exchange and add the
						// information
						// if there is already something in there, it is an
						// error
						PortInfoModel portInfo = em.getOutPortInfo();
						if (portInfo == null) {
							portInfo = new PortInfoModel(PortDirection.Out,
									object.getFullName());
							em.setOutPortInfo(portInfo);
						} else {
							handleException(object, new Exception(
									"Can't have multiple out ports for one Exchange:"
											+ portInfo.getPortName() + " and "
											+ object.getFullName()));
						}
					}
					//TODO: process properties and data type

				}
					break;
				case PERIODIC:
				{
					String exchangeName = extractExchangeName(
							object.getFullName(),
							CommPatternType.PERIODIC.suffix);

					// See if the exchange has been registered in the model
					ExchangeModel em = deviceComponentModel.exchangeModels
							.get(exchangeName);
					if (em == null) {// if not create a new one and register
						em = new PeriodicExchangeModel(extractParmeterName(
								object.getFullName(),
								CommPatternType.PERIODIC.suffix), // Name of the parameter
								systemName, // System Name
								vmdTypeNames.get(vmdTypeNames.size() - 1), // VMD Name of the exchange
								extractExchangeName(object.getFullName(),
										CommPatternType.PERIODIC.suffix) // ExchangeName
						);

						PortInfoModel portInfo = new PortInfoModel(
								PortDirection.Out, object.getFullName());
						em.setOutPortInfo(portInfo);

						deviceComponentModel.exchangeModels.put(exchangeName,
								em);
					} else {
						// if it is retrieve the existing exchange and add the
						// information
						// if there is already something in there, it is an
						// error
						PortInfoModel portInfo = em.getOutPortInfo();
						if (portInfo == null) {
							portInfo = new PortInfoModel(PortDirection.Out,
									object.getFullName());
							em.setOutPortInfo(portInfo);
						} else {
							handleException(object, new Exception(
									"Can't have multiple out ports for one Exchange:"
											+ portInfo.getPortName() + " and "
											+ object.getFullName()));
						}
					}
					//TODO: process properties and data type
				}
					break;
				}

			} else {
				handleException(object, new Exception(
						"Only Abstract Device type contains port information:"
								+ lastElemProcessed));
			}
			return DONE;
		}

		private RangeValue getOutputRateRange(EventDataPort object) {
			Property pr = GetProperties.lookupPropertyDefinition(object, "MAP_Properties", "Output_Rate");
			if(pr == null) return null;
			
			PropertyAcc pa = object.getPropertyValue(pr);
			ModalPropertyValue mpv = pa.first().getOwnedValues().get(0);
			if(mpv.getOwnedValue() instanceof RangeValue){
				return (RangeValue) mpv.getOwnedValue();
			} else
				return null;
		}
		
		private RangeValue getSeperationIntervalRange(EventDataPort object) {
			Property pr = GetProperties.lookupPropertyDefinition(object, "MDCF_Comm_Props", "separation_interval_range");
			if(pr == null) return null;
			
			PropertyAcc pa = object.getPropertyValue(pr);
			ModalPropertyValue mpv = pa.first().getOwnedValues().get(0);
			if(mpv.getOwnedValue() instanceof RangeValue){
				return (RangeValue) mpv.getOwnedValue();
			} else
				return null;
		}

		private String extractParmeterName(String fullPortName, String suffix) {
			String[] splits = fullPortName.split("_");
			int count_of_underscore_in_suffix = suffix.length()
					- suffix.replace("_", "").length();
			return splits[splits.length - count_of_underscore_in_suffix - 1];
		}

		private String extractExchangeName(String fullPortName, String suffix) {
			return fullPortName.substring(0,
					fullPortName.length() - suffix.length());
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
			} else if (portName.endsWith(CommPatternType.SPORADIC.suffix())) {
				return CommPatternType.SPORADIC;
			} else if (portName.endsWith(CommPatternType.PERIODIC.suffix())) {
				return CommPatternType.PERIODIC;
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
			System.err.println("AbstractTypeImpl:" + object.getFullName());
			vmdTypeDefNames.add(object.getFullName());

			lastElemProcessed = ElementType.DEVICE_ABSTRACT;

			processEList(object.getAllFeatures());

			lastElemProcessed = ElementType.NONE;
			return DONE;
		}

	}
	

	public DeviceTranslator(final IProgressMonitor monitor) {
		super(monitor, PROCESS_PRE_ORDER_ALL);
		deviceComponentModel = new DeviceComponentModel();

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
}
