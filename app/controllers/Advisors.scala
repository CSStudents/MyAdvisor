package controllers

import play.api.db.DB
import play.api.mvc.{Action, Controller}
import play.api.Play.current

class Advisors extends Controller {

  def list = Action {
    var advisorsList : List[Array[String]] = List()
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("SELECT employee.sin, name, workPhoneNumber, city FROM employee")

      while (rs.next) {
        val advisor : Array[String] = Array(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4))
        advisorsList = advisor :: advisorsList

      }
    } finally {
      conn.close()
    }
    Ok(views.html.advisors(advisorsList, true))
  }

  def info(sin: String) = Action {
    var list : List[Array[String]] = List()
    val exec =  "SELECT name, workPhoneNumber, streetAddress, city, dptName " +
                "FROM employee, works_in " +
                "WHERE employee.sin = works_in.sin and employee.sin = '" + sin + "'"
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement

      val rs = stmt.executeQuery(exec)

      while (rs.next) {
        val advisor : Array[String] = new Array(4)
        advisor(0) = rs.getString(1)
        advisor(1) = rs.getString(2)
        advisor(2) = rs.getString(3).trim() + ", " + rs.getString(4)
        advisor(3) = rs.getString(5)
        list = advisor :: list
      }
    } finally {
      conn.close()
    }
    Ok(views.html.advisors(list, false))
  }

}
