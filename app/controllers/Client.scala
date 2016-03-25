package controllers

import play.api.db.DB
import play.api.mvc.{Action, Controller}
import play.api.Play.current
import play.twirl.api.Html

class Client extends Controller{

  def list = Action {
    var out = "user id :          user name: \n\n"
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement

      val rs = stmt.executeQuery("SELECT cid, name FROM client")

      while (rs.next) {
        out += rs.getString(1) + "\t" + rs.getString(2) +"\n"

      }
    } finally {
      conn.close()
    }
    Ok(out)
  }

  def info(cid: String) = Action {
    var out = ""
    val exec = "SELECT name FROM client WHERE client.cid = '" + cid + "'"
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
    val content = Html("<h4>Place holder for user " + out + " </h4>")
    Ok(views.html.main(content))
  }

}
