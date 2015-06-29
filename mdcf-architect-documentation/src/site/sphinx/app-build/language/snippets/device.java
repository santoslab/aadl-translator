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

public class ICEpoInterfacePseudoDevice extends LogicComponent {
	private HashMap<String, Task> taskInstanceMap;
	private HashMap<String, MdcfReceiverPort<?>> receiverPortMap;

	private MdcfReceiverPort<Integer> RawSpO2ReceiverPort = new MdcfReceiverPort<>(
			"RawSpO2In", Integer.class);
	private MdcfSenderPort<Integer> SpO2SenderPort = new MdcfSenderPort<>(
			"SpO2Out", Integer.class);

	public ICEpoInterfacePseudoDevice(String GUID) {
		super(GUID, "ICEpoInterface");
		taskInstanceMap = new HashMap<>();
		receiverPortMap = new HashMap<>();
		taskInstanceMap.put(SpO2TaskTask.class.getSimpleName(),
				new SpO2TaskTask());
		receiverPortMap.put(RawSpO2ReceiverPort.getName(), RawSpO2ReceiverPort);
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

	@Override
	public void processSubscriberChannelAssignment(
			SubChannelAssignmentMsg subAssign) {
		subscriberChannelAssignmentHelper(subAssign, this.RawSpO2ReceiverPort);
	}

	@Override
	public void processPublisherChannelAssignment(
			PubChannelAssignmentMsg pubAssign) {
		publisherChannelAssignmentHelper(pubAssign, this.SpO2SenderPort);
	}

	private void initComponent() {
		// Do nothing, pseudodevices require no initialization
	}

	private void RawSpO2ListenerOnMessage(MdcfMessage msg, Integer RawSpO2Data) {
		SpO2SenderPort.send(RawSpO2Data);
	}

	public class SpO2TaskTask implements Task {
		@Override
		public void run() {
			MdcfMessage message = RawSpO2ReceiverPort.getReceiver()
					.getLastMsg();
			try {
				Integer SpO2Data = RawSpO2ReceiverPort.getLastMsgContent();
				RawSpO2ListenerOnMessage(message, SpO2Data);
			} catch (MdcfDecodeMessageException e) {
				System.err.println(getComponentTypeName()
						+ ".SpO2TaskTask task: invalid message:"
						+ message.getTextMsg());
				e.printStackTrace();
			}
		}
	}

}