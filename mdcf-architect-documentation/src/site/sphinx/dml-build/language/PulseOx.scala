final class PulseOx extends ICE_MDS {
  // Metadata...
  
  override val vmds : Map[String, ICE_VMD] = Map(
    "pulseox" -> new ICE_PulseOx_VMD {
      override val status : ICE_VMD_Status = new ICE_VMD_Status {
        override val exchange : ICE_Get_Exchange = new GetExchange {}
      }

      override val channels : Map[String, ICE_Channel] = Map(
        "spo2" -> new ICE_SpO2_Channel {
          override val metrics : Map[String, ICE_Metric] = Map(
            "spo2_num" -> new ICE_SpO2_Numeric {
              override val exchanges : Map[String, ICE_Data_Exchange] = Map(
                "periodic" -> new PeriodicExchange {}
              )
            }
          )
          override val alerts : Map[String, ICE_Alert] = Map()
          override val settings : Map[String, ICE_Setting] = Map()
          override val statuses : Map[String, ICE_Status] = Map()
        }
      )
    }
  )
}
