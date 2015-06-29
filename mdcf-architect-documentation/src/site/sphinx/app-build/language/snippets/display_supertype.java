package mdcf.app.PulseOx_Forwarding_System;

import java.util.HashMap;
import java.util.Map;

import mdcf.channelservice.common.MdcfDecodeMessageException;
import mdcf.channelservice.common.MdcfMessage;
import mdcf.channelservice.common.MdcfReceiverPort;
import mdcf.channelservice.common.MdcfSenderPort;
import mdcf.core.ctypes.Task;
import mdcf.core.ctypes.apppanel.AppPanelComponent;
import mdcf.core.messagetypes.devicemgmt.PubChannelAssignmentMsg;
import mdcf.core.messagetypes.devicemgmt.SubChannelAssignmentMsg;

public abstract class PulseOx_Display_ProcessSuperType extends
		AppPanelComponent {
	private HashMap<String, Task> taskInstanceMap;
	private HashMap<String, MdcfReceiverPort<?>> receiverPortMap;

	private MdcfReceiverPort<Integer> SpO2ReceiverPort = new MdcfReceiverPort<>(
			"SpO2In", Integer.class);
	private MdcfReceiverPort<Object> DerivedAlarmReceiverPort = new MdcfReceiverPort<>(
			"DerivedAlarmIn", Object.class);

	public PulseOx_Display_ProcessSuperType(String GUID) {
		super(GUID, "PulseOx_Display_Process");
		taskInstanceMap = new HashMap<>();
		receiverPortMap = new HashMap<>();
		taskInstanceMap.put(UpdateSpO2ThreadTask.class.getSimpleName(),
				new UpdateSpO2ThreadTask());
		taskInstanceMap.put(HandleAlarmThreadTask.class.getSimpleName(),
				new HandleAlarmThreadTask());
		receiverPortMap.put(SpO2ReceiverPort.getName(), SpO2ReceiverPort);
		receiverPortMap.put(DerivedAlarmReceiverPort.getName(),
				DerivedAlarmReceiverPort);
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
		subscriberChannelAssignmentHelper(subAssign, this.SpO2ReceiverPort);
		subscriberChannelAssignmentHelper(subAssign,
				this.DerivedAlarmReceiverPort);
	}

	@Override
	public void processPublisherChannelAssignment(
			PubChannelAssignmentMsg pubAssign) {
	}

	protected abstract void initComponent();

	protected abstract void SpO2ListenerOnMessage(MdcfMessage msg,
			Integer SpO2Data);

	protected abstract void DerivedAlarmListenerOnMessage(MdcfMessage msg);

	public class UpdateSpO2ThreadTask implements Task {
		@Override
		public void run() {
			MdcfMessage message = SpO2ReceiverPort.getReceiver().getLastMsg();
			try {
				Integer SpO2Data = SpO2ReceiverPort.getLastMsgContent();
				SpO2ListenerOnMessage(message, SpO2Data);
			} catch (MdcfDecodeMessageException e) {
				System.err.println(getComponentTypeName()
						+ ".UpdateSpO2ThreadTask task: invalid message:"
						+ message.getTextMsg());
				e.printStackTrace();
			}
		}
	}

	public class HandleAlarmThreadTask implements Task {
		@Override
		public void run() {
			MdcfMessage message = DerivedAlarmReceiverPort.getReceiver()
					.getLastMsg();
			DerivedAlarmListenerOnMessage(message);
		}
	}

}