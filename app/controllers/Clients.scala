package controllers

import models.Client
import play.api.db.DB
import play.api.mvc.{Action, Controller}
import play.api.Play.current
class Clients extends Controller{

  def list = Action {
    var clientList : List[Client] = List()
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("SELECT cid, name FROM client")
      while (rs.next) {
        val client : Client = Client(rs.getString(1), rs.getString(2),
            "BirthDatePlaceHolder", "homePhonNumPlaceholder", "workPhonNumPlaceholder", "addressPlaceHolder", "cityPlaceHolder", "province", "postalCode")
        clientList = client :: clientList

      }
    } finally {
      conn.close()
    }
    Ok(views.html.clients.list(clientList))
  }

  def info(cid: String) = Action {
    var branchList : List[String] = List()
    var data : Any = null
    val exec = "SELECT name, birthdate, homephonenumber, streetaddr, city " +
                "FROM client " +
                "WHERE client.cid = '" + cid + "'"
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(exec)

      while (rs.next) {
        if (data == null){
          data = Client(cid, rs.getString(1), rs.getString(2), rs.getString(3), "workPhonNumPlaceholder", rs.getString(4), rs.getString(5), "province", "postalCode" )
        }
      }
    } finally {
      conn.close()
    }
    val client : Client = data.asInstanceOf[Client]
    if(client != null){
      //something to add?
    }
    Ok(views.html.clients.info(client))
  }

}
