package controllers

import java.text.SimpleDateFormat

import models.{ServiceForm, Timeentry, Service}
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.DB
import play.api.mvc.{Action, Controller}
import play.api.Play.current
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._

import scala.text


/**
  * Created by jorgeluis on 31/03/16.
  */
class Services extends Controller{

  def info(sid: String) = Action {
    var timeEntryList : List[Timeentry] = List()
    var employee : Any = null
    var client : Any = null
    var data : Any = null
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement

      val pokeQuery = stmt.executeQuery("select * from timeentry where sid = '" +   sid + "'")
      var hasTimeEntries : Boolean = false
      while(pokeQuery.next){
        hasTimeEntries = true
      }
      if(hasTimeEntries){
        val fullQuery = stmt.executeQuery("select service.sid,servicetypename, basefee, hourlyrate, amountpaid,startdatetime, enddatetime, provides_service_to.cid, client.name, provides_service_to.sin, " +
          "employee.name from service, timeentry, provides_service_to, client, employee " +
          "WHERE service.sid = timeentry.sid and service.sid = provides_service_to.sid and client.cid = provides_service_to.cid and employee.sin = provides_service_to.sin and provides_service_to.sid = '" + sid + "'")
        while (fullQuery.next) {
          if (data  == null){
            data = Service(fullQuery.getString(1), fullQuery.getString(2), fullQuery.getString(3).toInt, fullQuery.getString(4).toInt, fullQuery.getString(5).toInt,Nil,"","")
            client = Tuple2(fullQuery.getString(8), fullQuery.getString(9))
            employee = Tuple2(fullQuery.getString(10), fullQuery.getString(11))
          }
          val df = new SimpleDateFormat("yyyy-MM-dd")
          val timeentry : Timeentry = Timeentry(df.parse(fullQuery.getString(6)), df.parse(fullQuery.getString(7)))
          timeEntryList = timeentry :: timeEntryList
        }
      }else{
        val fullQuery = stmt.executeQuery("select service.sid,servicetypename, basefee, hourlyrate, amountpaid, provides_service_to.cid, client.name, provides_service_to.sin, " +
          "employee.name from service, provides_service_to, client, employee " +
          "WHERE service.sid = provides_service_to.sid and client.cid = provides_service_to.cid and employee.sin = provides_service_to.sin and provides_service_to.sid = '" + sid + "'")
        while (fullQuery.next) {
          if (data  == null){
            data = Service(fullQuery.getString(1), fullQuery.getString(2), fullQuery.getString(3).toInt, fullQuery.getString(4).toInt, fullQuery.getString(5).toInt,Nil,"","")
            client = Tuple2(fullQuery.getString(6), fullQuery.getString(7))
            employee = Tuple2(fullQuery.getString(8), fullQuery.getString(9))
          }
        }
      }
    } finally {
      conn.close()
    }
    val service: Service = data.asInstanceOf[Service]
    val advisor : Tuple2[String, String] = employee.asInstanceOf[Tuple2[String, String]]
    val user : Tuple2[String, String] = client.asInstanceOf[Tuple2[String, String]]
    if (timeEntryList.nonEmpty){
      service.TimeEntries = timeEntryList
    }
    Ok(views.html.services.info(service, user, advisor))

  }

  def newService = Action {
    val emptyServiceForm = ServiceForm(NextServiceNumber(),"",100,50,0,"","")
    Ok(views.html.services.serviceCreate(serviceForm.fill(emptyServiceForm), "Fill in all required fields please"))
  }

  def newServiceByClient(cid : String) = Action {
    val clientServiceForm = ServiceForm(NextServiceNumber(),"",100,50,0,"",cid)
    Ok(views.html.services.serviceCreate(serviceForm.fill(clientServiceForm), "Fill in all required fields please"))
  }

  def newServiceByAdvisor(sin : String) = Action{
    val advisorServiceForm = ServiceForm(NextServiceNumber(),"",100,50,0,sin,"")
    Ok(views.html.services.serviceCreate(serviceForm.fill(advisorServiceForm), "Fill in all required fields please"))
  }

  def saveService = Action { implicit request =>
    serviceForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.debug("inside error")
        BadRequest(views.html.services.serviceCreate(formWithErrors, "Error: see below"))
      },
      service => {
        if(this.save(service)){
          Redirect(routes.Services.info(service.sid))
        }else{
          BadRequest(views.html.services.serviceCreate(serviceForm.fill(service), "Error: Invalid cid/sin/sid"))
        }
      }
    )
  }

  def save(form : ServiceForm): Boolean ={
    if(validateForm(form)){
      val params = "'" + form.sid + "','" + form.serviceTypeName + "','" + form.baseFee.toString + "','" + form.hourlyRate.toString +
        "','" + form.amountPaid.toString + "'"
      val exec =  "INSERT INTO service VALUES ( " + params + ");" +
        "INSERT INTO provides_service_to VALUES ('" + form.advisorSin + "','" + form.clientCid + "','" + form.sid + "')"
      val conn = DB.getConnection()
      try {
        val stmt = conn.createStatement
        stmt.execute(exec)
      } finally {
        conn.close()
      }
      true
    }else{
      //validation failed
      false
    }
  }

  def validateForm( form : ServiceForm): Boolean = {
    var cidExists : Boolean = false
    var sinExists : Boolean = false
    var sidDoesntExist : Boolean = false
    val conn = DB.getConnection()
    val cidCheck = "select * from client where cid = '" + form.clientCid + "'"
    val sinCheck = "select * from employee where sin = '" + form.advisorSin + "'"
    val sidCheck = "select * from service where sid = '" + form.sid + "'"
    try {
      val stmt = conn.createStatement
      var rs = stmt.executeQuery(cidCheck)
      while (rs.next) {
        //cid found
        cidExists = true
      }
      rs = stmt.executeQuery(sinCheck)
      while (rs.next) {
        //sin found
        sinExists = true
      }
      rs = stmt.executeQuery(sidCheck)
      while (rs.next) {
        //sid found, should be empty
        sidDoesntExist = true
      }
    } finally {
      conn.close()
    }
    //we want a cid and sin that exists, anda new sid
    cidExists && sinExists && !sidDoesntExist
  }

  private val serviceForm: Form[ServiceForm] = Form(
    mapping(
      "sid" -> nonEmptyText(10),
      "serviceTypeName" -> nonEmptyText(0,20),
      "baseFee" -> number,
      "hourlyRate" -> number,
      "amountPaid" ->   number,
      "advisorSin" -> nonEmptyText(9),
      "clientCid" -> nonEmptyText(9)
    )(ServiceForm.apply)(ServiceForm.unapply)
  )


  def NextServiceNumber() : String = {
    val conn = DB.getConnection()
    var data = ""
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("select max(sid) from service")
      while (rs.next) {
        data = rs.getString(1)
        val next : Long = data.toLong + 1
        data = next.toString
      }
    } finally {
      conn.close()
    }
    data
  }

}
