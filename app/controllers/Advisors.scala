package controllers

import models.{Service, AdvisorForm, Office, Advisor}
import play.api.db.DB
import play.api.mvc.{Action, Controller}
import play.api.Play.current
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.Logger


class Advisors extends Controller {

  def list = Action {
    var advisorsList: List[Advisor] = List()
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("SELECT employee.sin, name, workPhoneNumber, city FROM employee")

      while (rs.next) {
        val advisor: Advisor = Advisor(rs.getString(1), rs.getString(2), rs.getString(3), "", "", rs.getString(4), "", "", Nil)
        advisorsList = advisor :: advisorsList

      }
    } finally {
      conn.close()
    }
    Ok(views.html.advisors.list(advisorsList))
  }

  def parseQuery = Action{ implicit request =>
    queryForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.debug("form with errors")
        BadRequest(views.html.advisors.filteredList(Nil, "", formWithErrors))
      },
      query => {
        Redirect(routes.Advisors.filterList(query._1))
      }
    )
  }

  def filterList(serviceTypeName : String) = Action{
    var advisorsList : List[Advisor] = List()
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      //division query
      val rs = stmt.executeQuery("select employee.sin, name, servicetypename from employee, offers_service where employee.sin = offers_service.sin " +
        "EXCEPT (select employee.sin, name, servicetypename from offers_service, employee where employee.sin = offers_service.sin " +
        "EXCEPT (select employee.sin, name, servicetypename from employee, offers_service where employee.sin = offers_service.sin and servicetypename LIKE '%" + serviceTypeName + "%')) ORDER BY name;")

      while (rs.next) {
        //pass the serviceTypeName on the streetAddress Value... Im not making a separate object just for the join table
        val advisor : Advisor = Advisor(rs.getString(1), rs.getString(2), "", "", rs.getString(3), "", "", "", Nil)
        advisorsList = advisor :: advisorsList
      }
    } finally {
      conn.close()
    }
    val query : Tuple1[String] = Tuple1(serviceTypeName)
    Ok(views.html.advisors.filteredList(advisorsList, serviceTypeName, queryForm.fill(query)))
  }

  def info(sin: String) = Action {
    var branchList: List[String] = List()
    var data: Any = null
    val exec = "SELECT employee.sin, name, workPhoneNumber, streetAddress, city, reports_To, dptName " +
      "FROM employee, works_in " +
      "WHERE employee.sin = works_in.sin and employee.sin = '" + sin + "'"
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery(exec)
      while (rs.next) {
        if (data == null) {
          data = Advisor(rs.getString(1), rs.getString(2), rs.getString(3), "", rs.getString(4), rs.getString(5), "", rs.getString(6), Nil)

        }

        branchList = rs.getString(7) :: branchList

      }
    } finally {
      conn.close()
    }
    val advisor: Advisor = data.asInstanceOf[Advisor]
    advisor.branches = branchList
    val servicesProvided: List[Service] = getServiceByAdvisor(sin)
    Ok(views.html.advisors.info(advisor, servicesProvided)(null))

  }

  def newAdvisor = Action {
    val dummyAdvisor = AdvisorForm("000000001", "", 778, 778, "", "", "", "000000001")
    Ok(views.html.advisors.advisorCreate(advisorForm.fill(dummyAdvisor)))
  }

  def saveAdvisor = Action { implicit request =>
    advisorForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.debug("inside error")
        BadRequest(views.html.advisors.advisorCreate(formWithErrors))
      },
      advisor => {
        this.save(advisor)
        Redirect(routes.Advisors.info(advisor.sin))
      }
    )
  }

  def save(form: AdvisorForm): Unit = {
    val params = "'" + form.sin + "','" + form.name + "','" + form.workPhoneNumber.toString + "','" + form.homePhoneNumber.toString +
      "','" + form.streetAddress + "','" + form.city + "','" + form.postalcode + "','" + form.reportsTo + "'"
    val exec = "INSERT INTO employee VALUES ( " + params + ")"
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      stmt.execute(exec)
    } finally {
      conn.close()
    }
  }

  def delete(sin: String): Unit = {
    val exec = "DELETE FROM employee WHERE sin ='" + sin + "'"
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
      "name" -> nonEmptyText(0, 20),
      "workPhoneNumber" -> number,
      "homePhoneNumber" -> number,
      "streetAddress" -> text(0, 20),
      "city" -> nonEmptyText(0, 20),
      "postalcode" -> nonEmptyText(7),
      "ReportsTo" -> text(9)
    )(AdvisorForm.apply)(AdvisorForm.unapply)
  )

  private val queryForm: Form[Tuple1[String]] = Form(
    mapping(
      "query" -> text
    )(Tuple1.apply)(Tuple1.unapply)
  )


  def lookBySin(sin: Int): Boolean = {
    true
  }

  def getServiceByAdvisor(sin: String): List[Service] = {
    var serviceList: List[Service] = List()
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("SELECT service.sid, service.servicetypename, service.basefee, service.hourlyrate, service.amountpaid, provides_service_to.cid, client.name " +
        "FROM service, provides_service_to, client " +
        "WHERE service.sid = provides_service_to.sid and provides_service_to.cid = client.cid  and provides_service_to.sin = '" + sin + "'")

      while (rs.next) {
        val service: Service = Service(rs.getString(1), rs.getString(2), rs.getString(3).toInt, rs.getString(4).toInt, rs.getString(5).toInt, Nil, rs.getString(6), rs.getString(7))
        serviceList = service :: serviceList
      }
    } finally {
      conn.close()
    }

    serviceList
  }

  def maxMinCalc(sin: String, choice: String) = Action {
    val calcValue = findValue(sin, choice)
    Ok(views.html.advisors.requestedValue(choice, calcValue))
  }


  def findValue(sin: String, choice: String): String = {

    if (choice == "min") {

      val exec = "SELECT MIN(AveragesByCustomer.avgAmountpaid)" +
        " FROM (select avg(service.amountpaid) as avgAmountpaid from service, provides_service_to where " +
        "service.sid = provides_service_to.sid AND" +
        " provides_service_to.sin = " + "'" + sin + "'" + " group by provides_service_to.cid) as AveragesByCustomer"

      val conn = DB.getConnection()

      var minValue: String = ""

      try {

        val stmt = conn.createStatement
        val rs = stmt.executeQuery(exec)

        while (rs.next) {
          minValue = rs.getString(1)
        }

      } finally {
        conn.close()
      }
      if (minValue == null) {
        return "No Services Provided Yet"
      }
      return minValue
    }

    if (choice == "max") {
      val exec = "SELECT MAX(AveragesByCustomer.avgAmountpaid)" +
        " FROM (select avg(service.amountpaid) as avgAmountpaid from service, provides_service_to where " +
        "service.sid = provides_service_to.sid AND" +
        " provides_service_to.sin = " + "'" + sin + "'" + " group by provides_service_to.cid) as AveragesByCustomer"

      val conn = DB.getConnection()

      var maxValue: String = ""

      try {

        val stmt = conn.createStatement
        val rs = stmt.executeQuery(exec)

        while (rs.next) {
          maxValue = rs.getString(1)
        }

      } finally {
        conn.close()
      }
      if (maxValue == null) {
        return "No Services Provided Yet"
      }
      return maxValue
    }

    return "No Services Provided Yet"
  }

}
