package controllers

import controllers.Application
import models.{Service, ClientForm, ClientDeleteForm, Client}
import play.api.db.DB
import play.api.mvc.{Action, Controller}
import play.api.Play.current
import play.api.data._
import play.api.data.Forms._
import java.util.Date
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.lang.Long
import play.api.i18n.Messages.Implicits._

import play.api.{Application, Logger}

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

  def info(cid: String) = Action { implicit request =>
    var branchList : List[String] = List()
    var data : Any = null
    val exec = "SELECT name, birthdate, homephonenumber, workphonenumber, streetaddr, city, province, postalcode " +
                "FROM client " +
                "WHERE client.cid = '" + cid + "'"
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(exec)

      while (rs.next) {
        if (data == null){
          data = Client(cid, rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
            rs.getString(6), rs.getString(7), rs.getString(8) )
        }
      }
    } finally {
      conn.close()
    }
    val client : Client = data.asInstanceOf[Client]
    if(client != null){
      val df = new SimpleDateFormat("yyyy-MM-dd")
      val startDate = df.parse(client.birthDate)

      var clientInfo: ClientForm = null;
      val longValue: Long = 0L;

      if (client.homePhoneNumber == null || client.workPhoneNumber == null) {
        clientInfo = ClientForm(client.cid.replaceAll("""(?m)\s+$""", ""),client.name.replaceAll("""(?m)\s+$""", ""),startDate,
          longValue,
          longValue,
        client.streetAddress,
        client.city.replaceAll("""(?m)\s+$""", ""),
        client.province.replaceAll("""(?m)\s+$""", ""),
        client.postalCode.replaceAll("""(?m)\s+$""", ""))

      }

      else {
        clientInfo = ClientForm(client.cid.replaceAll("""(?m)\s+$""", ""),client.name.replaceAll("""(?m)\s+$""", ""),startDate,
          Long.valueOf(client.homePhoneNumber.replaceAll("""(?m)\s+$""", "")).longValue(),
          Long.valueOf(client.workPhoneNumber.replaceAll("""(?m)\s+$""", "")).longValue(),
          client.streetAddress,
          client.city.replaceAll("""(?m)\s+$""", ""),
          client.province.replaceAll("""(?m)\s+$""", ""),
          client.postalCode.replaceAll("""(?m)\s+$""", ""))
      }


      val services : List[Service] = getServicesByClient(cid)
      Ok(views.html.clients.info(client, clientForm.fill(clientInfo), services))
    }
    else {
      val dummyClient = ClientForm("","",null,0,0,"","","","")
      Ok(views.html.clients.info(client, clientForm.fill(dummyClient), Nil))
    }

  }

  def getServicesByClient(cid : String) : List[Service] = {
    var serviceList : List[Service] = List()
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("SELECT service.sid, service.servicetypename, service.basefee, service.hourlyrate, service.amountpaid, provides_service_to.sin, employee.name " +
        "FROM service, provides_service_to, employee " +
        "WHERE service.sid = provides_service_to.sid and provides_service_to.sin = employee.sin  and provides_service_to.cid = '" + cid + "'")

      while (rs.next) {
        val service : Service = Service(rs.getString(1), rs.getString(2), rs.getString(3).toInt, rs.getString(4).toInt,rs.getString(5).toInt, Nil, rs.getString(6), rs.getString(7))
        serviceList = service :: serviceList
      }
    } finally {
      conn.close()
    }

    serviceList
  }

  def editClient(cid: String) = Action { implicit request =>
    clientForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.clients.clientCreate(formWithErrors))
      },
      client => {
        this.edit(client, cid)
        Redirect(routes.Clients.info(client.cid))
      }
    )
  }

  def edit(form : ClientForm, cid : String): Unit = {
    val params = "name= " + "'" + form.name + "'" + ", birthdate= " + "'" + form.birthdate + "'" + ", homephonenumber = " + form.homephone.toString +
    ", workphonenumber = " + form.workphone.toString + ", streetaddr = " + "'" + form.streetAddress + "'" + ", city = " + "'" + form.city + "'" +
      ", province = " + "'" + form.province + "'" + ", postalcode = " + "'" + form.postalCode + "'"
    val exec =  "UPDATE client SET " + params + " WHERE cid = " + "'" + form.cid + "'"
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement;
      stmt.execute(exec)
    } finally {
      conn.close()
    }
  }

  def saveClient = Action { implicit request =>
    clientForm.bindFromRequest.fold(
      formWithErrors => {
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

  def delete(cid: String) = Action {
      Ok(views.html.clients.deleteClient(cid, clientDeleteForm))
  }

  def deleteClient(cid: String) = Action {

    val exec =  "DELETE FROM client WHERE cid= " + "'" + cid + "'"
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement;
      stmt.execute(exec)
    } finally {
      conn.close()
    }

    Redirect(routes.Application.index)
  }

  private val clientDeleteForm: Form[ClientDeleteForm] = Form(
    mapping(
      "cid" -> text
    ) (ClientDeleteForm.apply)(ClientDeleteForm.unapply)
  )


  private val clientForm: Form[ClientForm] = Form(
    mapping(
      "cid" -> nonEmptyText(9),
      "name" -> nonEmptyText(0,20),
      "birthdate" -> date,
      "homephone" -> longNumber,
      "workphone" -> longNumber,
      "streetAddress" -> text(0,20),
      "city" -> text(0,20),
      "province" -> text(0,2),
      "postalCode" -> text(0,6)
    ) (ClientForm.apply)(ClientForm.unapply)
  )


}
