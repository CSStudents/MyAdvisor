package controllers

import models.{Service, ClientForm, Client}
import play.api.db.DB
import play.api.mvc.{Action, Controller}
import play.api.Play.current
import play.api.data._
import play.api.data.Forms._
import java.util.Date
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
      val df = new SimpleDateFormat("yyyy-MM-dd");
      val startDate = df.parse(client.birthDate);

      val clientInfo = ClientForm(client.cid.replaceAll("""(?m)\s+$""", ""),client.name.replaceAll("""(?m)\s+$""", ""),startDate,
        Integer.parseInt(client.homePhoneNumber.replaceAll("""(?m)\s+$""", "")),
        Integer.parseInt(client.workPhoneNumber.replaceAll("""(?m)\s+$""", "")),
        client.streetAddress,
        client.city.replaceAll("""(?m)\s+$""", ""),
        client.province.replaceAll("""(?m)\s+$""", ""),
        client.postalCode.replaceAll("""(?m)\s+$""", ""))

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
        Logger.debug("in the form with Errors!")
        BadRequest(views.html.clients.clientCreate(formWithErrors))
      },
      client => {
        Logger.debug("in the success method!")
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
    Logger.debug(s"Query=$exec")
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


  private val clientForm: Form[ClientForm] = Form(
    mapping(
      "cid" -> nonEmptyText(9),
      "name" -> nonEmptyText(0,20),
      "birthdate" -> date,
      "homephone" -> longNumber,
      "workphone" -> longNumber,
      "streetAddress" -> text(0,20),
      "city" -> text(0,20),
      "province" -> text(0,20),
      "postalCode" -> text(0,6)
    ) (ClientForm.apply)(ClientForm.unapply)
  )


}
