package controllers

import models.{Service, AdvisorForm, Office, Advisor}
import play.api.db.DB
import play.api.mvc.{Action, Controller}
import play.api.Play.current
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.mvc.Flash
import play.api.Logger


class Advisors extends Controller {

  def list = Action {
    var advisorsList : List[Advisor] = List()
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("SELECT employee.sin, name, workPhoneNumber, city FROM employee")

      while (rs.next) {
        val advisor : Advisor = Advisor(rs.getString(1), rs.getString(2), rs.getString(3), "", "", rs.getString(4), "", "", Nil)
        advisorsList = advisor :: advisorsList

      }
    } finally {
      conn.close()
    }
    Ok(views.html.advisors.list(advisorsList))
  }

  def info(sin: String) = Action {
    var branchList : List[String] = List()
    var data : Any = null
    val exec =  "SELECT employee.sin, name, workPhoneNumber, streetAddress, city, reports_To, dptName " +
      "FROM employee, works_in " +
      "WHERE employee.sin = works_in.sin and employee.sin = '" + sin + "'"
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(exec)
      while (rs.next) {
        if (data  == null){
          data = Advisor(rs.getString(1), rs.getString(2), rs.getString(3), "", rs.getString(4), rs.getString(5), "", rs.getString(6), Nil)

        }

        branchList = rs.getString(7) :: branchList

      }
    } finally {
      conn.close()
    }
    val advisor: Advisor = data.asInstanceOf[Advisor]
    advisor.branches = branchList
    val servicesProvided : List[Service] = getServiceByAdvisor(sin)
    Ok(views.html.advisors.info(advisor, servicesProvided))

  }

  def saveAdvisor = Action { implicit request =>
    advisorForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.debug("inside error")
        BadRequest(views.html.advisors.form(formWithErrors))
      },
      advisor => {
        this.save(advisor)
        Redirect(routes.Advisors.info(advisor.sin))
      }
    )
  }

  def newAdvisor = Action {
    val dummyAdvisor = AdvisorForm("000000001","",778,778,"","","","000000001")
    Ok(views.html.advisors.form(advisorForm.fill(dummyAdvisor)))
  }

  def save(form : AdvisorForm): Unit ={
    Logger.debug(form.name)
    val params = "'" + form.sin + "','" + form.name + "','" + form.workPhoneNumber.toString + "','" + form.homePhoneNumber.toString +
                "','" + form.streetAddress + "','" + form.city + "','" + form.postalcode + "','" + form.reportsTo + "'"
    val exec =  "INSERT INTO employee VALUES ( " + params + ")"
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      stmt.execute(exec)
    } finally {
      conn.close()
    }
  }

  def delete(sin : String): Unit ={
    val exec =  "DELETE FROM employee WHERE sin ='" + sin + "'"
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      stmt.execute(exec)
    } finally {
      conn.close()
    }
    Redirect(routes.Advisors.list())
  }

  private val advisorForm: Form[AdvisorForm] = Form(
    mapping(
      "sin" -> nonEmptyText(9),
      "name" -> nonEmptyText(0,20),
      "workPhoneNumber" -> number,
      "homePhoneNumber" -> number,
      "streetAddress" ->   text(0,20),
      "city" -> nonEmptyText(0,20),
      "postalcode" -> nonEmptyText(7),
      "ReportsTo" -> text(9)
    )(AdvisorForm.apply)(AdvisorForm.unapply)
  )

  def lookBySin(sin : Int) : Boolean = {
    true
  }

  def getServiceByAdvisor(sin: String): List[Service] = {
    var serviceList : List[Service] = List()
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("SELECT service.sid, service.servicetypename, service.basefee, service.hourlyrate, service.amountpaid, provides_service_to.cid, client.name " +
        "FROM service, provides_service_to, client " +
        "WHERE service.sid = provides_service_to.sid and provides_service_to.cid = client.cid  and provides_service_to.sin = '" + sin + "'")

      while (rs.next) {
        val service : Service = Service(rs.getString(1), rs.getString(2), rs.getString(3).toInt, rs.getString(4).toInt,rs.getString(5).toInt, Nil, rs.getString(6), rs.getString(7))
        serviceList = service :: serviceList
      }
    } finally {
      conn.close()
    }

    serviceList
  }

}
