package controllers

import play.api.db.DB
import play.api.mvc.{Action, Controller}
import play.api.Play.current
class Client extends Controller{

  def list = Action {
    var clientList : List[Array[String]] = List()
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement

      val rs = stmt.executeQuery("SELECT cid, name FROM client")

      while (rs.next) {
        val client : Array[String] = Array(rs.getString(1), rs.getString(2))
        clientList = client :: clientList

      }
    } finally {
      conn.close()
    }
    Ok(views.html.clients(clientList, true))
  }

  def info(cid: String) = Action {
    var clientList : List[Array[String]] = List()
    val exec = "SELECT name, birthdate, homephonenumber, streetaddr, city " +
                "FROM client " +
                "WHERE client.cid = '" + cid + "'"
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(exec)

      while (rs.next) {
        val client : Array[String] = new Array(5)
        client(0) = rs.getString(1)
        client(1) = rs.getString(2)
        client(2) = rs.getString(3)
        client(3) = rs.getString(4)
        client(4) = rs.getString(5)
        clientList = client :: clientList

      }
    } finally {
      conn.close()
    }
    Ok(views.html.clients(clientList, false))
  }

}
