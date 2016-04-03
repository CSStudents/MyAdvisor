package controllers

import play.api._
import play.api.data.Form
import play.api.data.Forms._
import play.api.db.DB
import play.api.i18n.Messages
import play.api.Play.current
import play.api.mvc.Session
import play.api.i18n.Messages.Implicits._
import play.api.mvc._
import play.api.Logger


class Application extends Controller {

  var username : String = ""
  var id : String = ""

  def index = Action { implicit request =>
    val username = request.session.get("username").getOrElse("is empty")
    val id = request.session.get("id").getOrElse("is empty")
    Logger.debug("username in session : " + username )
    Logger.debug("id of user : " + id)

    if(id.isEmpty){
      Logger.debug("is empty")
    }else{
      Logger.debug("not empty")
    }
    Ok(views.html.nav()).withSession("username" -> username, "id" -> id)
  }

  def login(admin : Boolean) = Action {
    val data : Tuple2[String, String] = Tuple2("", "")
    Ok(views.html.login.input(credentials.fill(data), "Please input credentials", admin))
  }

  def authenticate(credentials :Tuple2[String, String]) = Action { implicit request =>
    Ok(views.html.nav()).withSession( "username" -> credentials._1, "id" -> credentials._2)
  }

  def parseCredentials(admin : Boolean) = Action { implicit request =>
    credentials.bindFromRequest.fold(
      formWithErrors => {
        Logger.debug("inside error")
        BadRequest(views.html.login.input(formWithErrors, "Error: please review below", admin))
      },
      data => {
        if(admin){
          if(isAdvisorAuth(data)){
            this.username = data._1
            this.id = data._2
            Redirect(routes.Advisors.info(data._2)).withSession("username" -> username, "id" -> id)
          }else{
            Ok(views.html.login.input(credentials.fill(data), "Error: wrong username/id. Not found in Database", admin))
          }
        }else {
          if (isClientAuth(data)) {
            this.username = data._1
            this.id = data._2
            Redirect(routes.Clients.info(data._2)).withSession("username" -> username, "id" -> id)
          } else {
            Ok(views.html.login.input(credentials.fill(data), "Error: wrong username/id. Not found in Database", admin))
          }
        }
      }
    )
  }

  def isClientAuth(credentials : Tuple2[String, String]) : Boolean = {
    var result : Boolean = false
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("SELECT cid FROM client where name LIKE '%" + credentials._1 + "%' and cid = '" + credentials._2 + "'")

      while (rs.next) {
        result = true
      }
    } finally {
      conn.close()
    }
    result
  }

  def isAdvisorAuth(credentials : Tuple2[String, String]) : Boolean = {
    var result : Boolean = false
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("SELECT sin FROM employee where name LIKE '%" + credentials._1 + "%' and sin = '" + credentials._2 + "'")

      while (rs.next) {
        result = true
      }
    } finally {
      conn.close()
    }
    result
  }

  private val credentials : Form[Tuple2[String, String]] = Form(
    mapping(
      "username" -> text,
      "id" -> text
    )(Tuple2.apply)(Tuple2.unapply)
  )


}