package controllers

import models.Service
import play.api.db.DB
import play.api.mvc.{Action, Controller}
import play.api.Play.current


/**
  * Created by jorgeluis on 31/03/16.
  */
class Services {

  def getServicesByUser(cid : String) : List[Service] = {
    var serviceList : List[Service] = List()
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("SELECT * FROM Service, provides_service_to where service.sid = provides_service_to.sid and provides_service_to.cid = '" + cid + "'")

      while (rs.next) {
        val service : Service = Service(rs.getString(1), rs.getString(2), rs.getString(3).toInt, rs.getString(4).toInt,rs.getString(5).toInt, Nil)
        serviceList = service :: serviceList
      }
    } finally {
      conn.close()
    }

    serviceList
  }

//  def info(sin: String) = Action {
//    var branchList : List[String] = List()
//    var data : Any = null
//    val exec =  "SELECT employee.sin, name, workPhoneNumber, streetAddress, city, reports_To, dptName " +
//      "FROM employee, works_in " +
//      "WHERE employee.sin = works_in.sin and employee.sin = '" + sin + "'"
//    val conn = DB.getConnection()
//    try {
//      val stmt = conn.createStatement
//      val rs = stmt.executeQuery(exec)
//      while (rs.next) {
//        if (data  == null){
//          data = Advisor(rs.getString(1), rs.getString(2), rs.getString(3), "", rs.getString(4), rs.getString(5), "", rs.getString(6), Nil)
//
//        }
//
//        branchList = rs.getString(7) :: branchList
//
//      }
//    } finally {
//      conn.close()
//    }
//    val advisor: Advisor = data.asInstanceOf[Advisor]
//    advisor.branches = branchList
//    Ok(views.html.advisors.info(advisor))
//
//  }

}
