package mdcf.app.PulseOx_Forwarding_System;

import java.util.HashMap;
import java.util.Map;

import mdcf.channelservice.common.MdcfDecodeMessageException;
import mdcf.channelservice.common.MdcfMessage;
import mdcf.channelservice.common.MdcfReceiverPort;
import mdcf.channelservice.common.MdcfSenderPort;
import mdcf.core.ctypes.Task;
import mdcf.core.ctypes.logic.LogicComponent;
import mdcf.core.messagetypes.devicemgmt.PubChannelAssignmentMsg;
import mdcf.core.messagetypes.devicemgmt.SubChannelAssignmentMsg;

public abstract class PulseOx_Logic_ProcessSuperType extends LogicComponent {
	private HashMap<String, Task> taskInstanceMap;
	private HashMap<String, MdcfReceiverPort<?>> receiverPortMap;
	private Integer SpO2Data;

	private MdcfReceiverPort<Integer> SpO2ReceiverPort = new MdcfReceiverPort<>(
			"SpO2In", Integer.class);
	private MdcfSenderPort<Object> DerivedAlarmSenderPort = new MdcfSenderPort<>(
			"DerivedAlarmOut", Object.class);

	public PulseOx_Logic_ProcessSuperType(String GUID) {
		super(GUID, "PulseOx_Logic_Process");
		taskInstanceMap = new HashMap<>();
		receiverPortMap = new HashMap<>();
		taskInstanceMap.put(CheckSpO2ThreadTask.class.getSimpleName(),
				new CheckSpO2ThreadTask());
		receiverPortMap.put(SpO2ReceiverPort.getName(), SpO2ReceiverPort);
	}

	@Override
	public void init() {
		initComponent();
	}

	@Override
	protected Map<String, Task> getTaskInstanceMap() {
		return this.taskInstanceMap;
	}

	@Override
	protected Map<String, MdcfReceiverPort<?>> getReceiverPortMap() {
		return this.receiverPortMap;
	}

	protected Integer getSpO2Data() {
		return SpO2Data;
	}

	@Override
	public void processSubscriberChannelAssignment(
			SubChannelAssignmentMsg subAssign) {
		subscriberChannelAssignmentHelper(subAssign, this.SpO2ReceiverPort);
	}

	@Override
	public void processPublisherChannelAssignment(
			PubChannelAssignmentMsg pubAssign) {
		publisherChannelAssignmentHelper(pubAssign, this.DerivedAlarmSenderPort);
	}

	protected abstract void initComponent();

	protected abstract void CheckSpO2ThreadMethod();

	private void SpO2ListenerOnMessage(MdcfMessage msg, Integer SpO2Data) {
		this.SpO2Data = SpO2Data;
	}

	public class CheckSpO2ThreadTask implements Task {
		@Override
		public void run() {
			CheckSpO2ThreadMethod();
		}
	}
}