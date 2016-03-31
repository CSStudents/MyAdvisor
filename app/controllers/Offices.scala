package controllers

import models.Office
import play.api.db.DB
import play.api.mvc.{Action, Controller}
import play.api.Play.current

class Offices extends Controller{

  def list = Action {
    var officeList : List[Office] = List()
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("SELECT streetaddress, oid, mainofficenumber FROM officelocation")
      while (rs.next) {
        val office : Office = Office(rs.getString(1), "postalCodePlaceHolder", "CityPlaceHolder", "ProvincePlaceHolder",rs.getString(3), rs.getString(2), Nil)
        officeList = office :: officeList
      }
    } finally {
      conn.close()
    }
    Ok(views.html.offices.list(officeList))
  }

  def info(oid: String) = Action {
    var branchList : List[String] = List()
    var data : Any = null
    val exec = "SELECT officelocation.streetaddress, city, mainofficenumber, dptname  " +
                "FROM officelocation, located_at " +
                "WHERE officelocation.streetaddress = located_at.streetaddress and officelocation.oid = '" + oid + "'"
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(exec)
      while (rs.next) {
        if (data == null){
          data = Office(rs.getString(1), "PostalCodePlaceHolder", rs.getString(2), "provPlaceHolder" ,rs.getString(3), oid, Nil )
        }
        branchList = rs.getString(4) :: branchList
      }
    } finally {
      conn.close()
    }
    val office: Office = data.asInstanceOf[Office]
    if(office != null){
      office.branches = branchList
    }
    Ok(views.html.offices.info(office))
  }

}
