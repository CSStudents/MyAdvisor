package controllers

import play.api.db.DB
import play.api.mvc.{Action, Controller}
import play.api.Play.current

class Offices extends Controller{

  def list = Action {
    var officeList : List[Array[String]] = List()
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement

      val rs = stmt.executeQuery("SELECT oid, streetaddress, mainofficenumber FROM officelocation")

      while (rs.next) {
        val office : Array[String] = Array(rs.getString(1), rs.getString(2), rs.getString(3))
        officeList = office :: officeList

      }
    } finally {
      conn.close()
    }
    Ok(views.html.offices(officeList, true))
  }

  def info(oid: String) = Action {
    var list : List[Array[String]] = List()
    val exec = "SELECT officelocation.streetaddress, city, mainofficenumber, dptname  " +
                "FROM officelocation, located_at " +
                "WHERE officelocation.streetaddress = located_at.streetaddress and officelocation.oid = '" + oid + "'"
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement

      val rs = stmt.executeQuery(exec)

      while (rs.next) { //should be an inner loop...
      val office : Array[String] = new Array(4)
        office(0) = rs.getString(1)
        office(1) = rs.getString(2)
        office(2) = rs.getString(3)
        office(3) = rs.getString(4)
        list = office :: list
      }
    } finally {
      conn.close()
    }
    Ok(views.html.offices(list, false))
  }

}
