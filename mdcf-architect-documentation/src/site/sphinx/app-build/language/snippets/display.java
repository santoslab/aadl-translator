package mdcf.app.PulseOx_Forwarding_System;

import mdcf.channelservice.common.MdcfMessage;

public class PulseOx_Display_Process extends PulseOx_Display_ProcessSuperType {

	public PulseOx_Display_Process(String GUID) {
		super(GUID);
	}

	@Override
	protected void initComponent() {
		// TODO Fill in custom initialization code here
	}

	@Override
	protected void SpO2ListenerOnMessage(MdcfMessage msg, Integer SpO2Data) {
		// TODO Fill in custom listener code here
	}

	@Override
	protected void DerivedAlarmListenerOnMessage(MdcfMessage msg) {
		// TODO Fill in custom listener code here
	}

}