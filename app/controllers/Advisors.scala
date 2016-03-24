package controllers

import play.api.db.DB
import play.api.mvc.{Action, Controller}
import play.api.Play.current
import play.twirl.api.Html

class Advisors extends Controller {

  def list = Action {
    var out = "Advisor name:        work department: \n\n"
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement

      val rs = stmt.executeQuery("SELECT name, dptname FROM employee, works_in WHERE employee.sin = works_in.sin")

      while (rs.next) {
        out += rs.getString(1) + rs.getString(2) +"\n"

      }
    } finally {
      conn.close()
    }
    Ok(out)
  }

  def info(sin: String) = Action {
    var out = ""
    val exec = "SELECT name FROM employee WHERE employee.sin = '" + sin + "'"
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement

      val rs = stmt.executeQuery(exec)

      while (rs.next) {
        out += rs.getString(1)

      }
    } finally {
      conn.close()
    }
    val content = Html("<h4>Place holder for employee " + out + " </h4>")
    Ok(views.html.main(content))
  }

}
