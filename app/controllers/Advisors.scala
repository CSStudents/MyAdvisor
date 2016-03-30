package controllers

import models.Advisor
import play.api.db.DB
import play.api.mvc.{Action, Controller}
import play.api.Play.current
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.i18n.Messages.Implicits._
import play.api.mvc.Flash


class Advisors extends Controller {

  def list = Action {
    var advisorsList : List[Array[String]] = List()
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("SELECT employee.sin, name, workPhoneNumber, city FROM employee")

      while (rs.next) {
        val advisor : Array[String] = Array(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4))
        advisorsList = advisor :: advisorsList

      }
    } finally {
      conn.close()
    }
    Ok(views.html.advisors(advisorsList, true))
  }

  def info(sin: String) = Action {
    var list : List[Array[String]] = List()
    val exec =  "SELECT name, workPhoneNumber, streetAddress, city, dptName " +
      "FROM employee, works_in " +
      "WHERE employee.sin = works_in.sin and employee.sin = '" + sin + "'"
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement

      val rs = stmt.executeQuery(exec)

      while (rs.next) {
        val advisor : Array[String] = new Array(4)
        advisor(0) = rs.getString(1)
        advisor(1) = rs.getString(2)
        advisor(2) = rs.getString(3) + ", " + rs.getString(4)
        advisor(3) = rs.getString(5)
        list = advisor :: list

      }
    } finally {
      conn.close()
    }
    Ok(views.html.advisors(list, false))
  }

  def admin = Action { implicit request =>
    val sample : String = "Somestring"
    Ok(views.html.adminControls(sample))

  }

  def newAdvisor = Action { implicit request =>
    val form = advisorForm
    Ok(views.html.editAdvisor(form))
  }

  private val advisorForm: Form[Advisor] = Form(
    mapping(
      "sin" -> nonEmptyText(9,9),
      "name" -> nonEmptyText,
      "workPhoneNumber" -> nonEmptyText(0,9),
      "homePhoneNumber" -> nonEmptyText(0,9),
      "streetAddress" ->   nonEmptyText(0, 20),
      "city" -> nonEmptyText(0, 20),
      "province" -> nonEmptyText(0, 2),
      "ReportsTo" -> Forms.text.verifying("validation.reportsTo.incorrect", this.lookBySin(_))
    )(Advisor.apply)(Advisor.unapply)
  )

  def lookBySin(sin: String): Boolean ={
    return true
  }

  def save = Action{implicit request =>
    val newAdvisorForm = advisorForm.bindFromRequest()

    newAdvisorForm.fold(

      hasErrors = { form => Redirect(routes.Advisors.newAdvisor()).
        flashing(Flash(form.data) + ("error" -> Messages("validation.errors")))},

      success = {newAdvisor => this.add(newAdvisor)
                  Redirect(routes.Advisors.list())}
    )

  }

  def add(advisor: Advisor) ={
    //add advisor to db...
  }




}
