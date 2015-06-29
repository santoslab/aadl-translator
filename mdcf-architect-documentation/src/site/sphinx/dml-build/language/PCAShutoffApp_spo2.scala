trait PCAShutoffApp_spo2 extends ICE_APP_REQ 
{
  val spo2 : ICE_SpO2_Numeric 
  val spo2_per : ICE_Periodic_Exchange
}


object PCAShutoffApp_spo2
{  
  @Inv val req_spo2_get : Predicate[PCAShutoffApp_spo2] = pred 
  {
    req : PCAShutoffApp_spo2 => req.spo2 match 
    {              
      case spo2_ex : ICE_SpO2_Numeric => 
          spo2_ex.exchanges.values.exists(  
        _ match 
        {                                                      
          case exch : ICE_Periodic_Exchange => req.spo2_per == exch  
          case _ => false 
        }
      )
      case _ => false
    }
  }
}
