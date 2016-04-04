package models

/**
  * Created by jorgeluis on 31/03/16.
  */
case class Service(sid : String, serviceTypeName : String, baseFee : Int, hourlyRate : Int, amountPaid : Int, var TimeEntries : List[Timeentry],
                   sinOrcid : String, name : String) {

  object Service{


  }


  def isService(sid: String) : Boolean = {
    true
  }

}
