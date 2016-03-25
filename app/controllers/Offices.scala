package controllers

import play.api.db.DB
import play.api.mvc.{Action, Controller}
import play.api.Play.current
import play.twirl.api.Html

class Offices extends Controller{

  def list = Action {
    var out = "Office location:        main office number: \n\n"
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement

      val rs = stmt.executeQuery("SELECT streetaddress, mainofficenumber FROM officelocation")

      while (rs.next) {
        out += rs.getString(1) + rs.getString(2) +"\n"

      }
    } finally {
      conn.close()
    }
    Ok(out)
  }

  def info(oid: String) = Action {
    var out = ""
    val exec = "SELECT streetaddress FROM officelocation WHERE officelocation.oid = '" + oid + "'"
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
    val content = Html("<h4>Place holder for office " + out + " </h4>")
    Ok(views.html.main(content))
  }

}
