package controllers

import models.{ClientForm, Client}
import play.api.db.DB
import play.api.mvc.{Action, Controller}
import play.api.Play.current
import play.api.data._
import play.api.data.Forms._
import java.util.Date
import play.api.i18n.Messages.Implicits._

import play.api.Logger

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

  def saveClient = Action { implicit request =>
    clientForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.debug("inside error")
        BadRequest(views.html.clients.clientCreate(formWithErrors))
      },
      client => {
        this.save(client)
        Redirect(routes.Clients.info(client.cid))
      }
    )
  }

  def newClient = Action {
    val dummyClient = ClientForm("","",null,0,0,"","","","")
    Ok(views.html.clients.clientCreate(clientForm.fill(dummyClient)))
  }

  def save(form : ClientForm): Unit = {
      val params = "'" + form.cid + "','" + form.name + "','" + form.birthdate + "','" + form.homephone.toString + "','" + form.workphone.toString +
        "','" + form.streetAddress + "','" + form.city + "','" + form.province + "','" + form.postalCode + "'"
      val exec =  "INSERT INTO client VALUES ( " + params + ")"
      val conn = DB.getConnection()
      try {
        val stmt = conn.createStatement;
        stmt.execute(exec)
      } finally {
        conn.close()
      }
  }


  private val clientForm: Form[ClientForm] = Form(
    mapping(
      "cid" -> nonEmptyText(9),
      "name" -> nonEmptyText(0,20),
      "birthdate" -> date,
      "homephone" -> number(0,999999999),
      "workphone" -> number(0,999999999),
      "streetAddress" -> text(0,20),
      "city" -> text(0,20),
      "province" -> text(0,20),
      "postalCode" -> text(0,6)
    ) (ClientForm.apply)(ClientForm.unapply)
  )


}
