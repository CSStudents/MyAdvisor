package controllers

import models.{AdvisorForm, Office, Advisor}
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
    Ok(views.html.advisors.info(advisor))

  }

  def saveAdvisor = Action { implicit request =>
    advisorForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.debug("inside error")
        BadRequest(views.html.advisors.form(formWithErrors))
      },
      advisor => {
//        this.save(advisor)
        Redirect(routes.Application.index())
              //.flashing("success" -> "Contact saved!")
      }
    )
  }

  def newAdvisor = Action {
    val dummyAdvisor = AdvisorForm("","","","","","","","")
    Ok(views.html.advisors.form(advisorForm.fill(dummyAdvisor)))
  }

  def save(form : AdvisorForm): Unit ={
    // call db and save
    Ok(views.html.nav())
  }

  private val advisorForm: Form[AdvisorForm] = Form(
    mapping(
      "sin" -> nonEmptyText,
      "name" -> text,
      "workPhoneNumber" -> text,
      "homePhoneNumber" -> text,
      "streetAddress" ->   text,
      "city" -> text,
      "province" -> text,
      "ReportsTo" -> text
    )(AdvisorForm.apply)(AdvisorForm.unapply)
  )

  def lookBySin(sin : Int) : Boolean = {
    true
  }

}
