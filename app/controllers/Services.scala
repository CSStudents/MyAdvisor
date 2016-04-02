package controllers

import java.text.SimpleDateFormat

import models.{Timeentry, Service}
import play.api.db.DB
import play.api.mvc.{Action, Controller}
import play.api.Play.current


/**
  * Created by jorgeluis on 31/03/16.
  */
class Services extends Controller{

  def info(sid: String) = Action {
    var timeEntryList : List[Timeentry] = List()
    var employee : Any = null
    var client : Any = null
    var data : Any = null
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("select service.sid,servicetypename, basefee, hourlyrate, amountpaid,startdatetime, enddatetime, provides_service_to.cid, client.name, provides_service_to.sin, " +
        "employee.name from service, timeentry, provides_service_to, client, employee " +
        "WHERE service.sid = timeentry.sid and service.sid = provides_service_to.sid and client.cid = provides_service_to.cid and employee.sin = provides_service_to.sin and provides_service_to.sid = '" + sid + "'")
      while (rs.next) {
        if (data  == null){
          data = Service(rs.getString(1), rs.getString(2), rs.getString(3).toInt, rs.getString(4).toInt, rs.getString(5).toInt,Nil,"","")
          client = Tuple2(rs.getString(8), rs.getString(9))
          employee = Tuple2(rs.getString(10), rs.getString(11))
        }
        val df = new SimpleDateFormat("yyyy-MM-dd")
        val timeentry : Timeentry = Timeentry(df.parse(rs.getString(6)), df.parse(rs.getString(7)))
        timeEntryList = timeentry :: timeEntryList
      }
    } finally {
      conn.close()
    }
    val service: Service = data.asInstanceOf[Service]
    val advisor : Tuple2[String, String] = employee.asInstanceOf[Tuple2[String, String]]
    val user : Tuple2[String, String] = client.asInstanceOf[Tuple2[String, String]]
    service.TimeEntries = timeEntryList
    Ok(views.html.services.info(service, user, advisor))

  }

}
