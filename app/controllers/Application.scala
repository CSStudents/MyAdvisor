package controllers

import play.api._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages
import play.api.Play.current
import play.api.mvc.Session
import play.api.i18n.Messages.Implicits._
import play.api.mvc._
import play.api.Logger


class Application extends Controller {

  private var username : String = ""
  private var id : String = ""

  def index = Action { implicit request =>
    val username = request.session.get("username")
    val id = request.session.get("id")
    Logger.debug("username in session : " + username.get )
    Logger.debug("id of user : " + id.get)

    if(id.get.isEmpty){
      Logger.debug("is empty")
    }else{
      Ok(views.html.nav()).withSession("username" -> this.username, "id" -> this.id)
      Logger.debug("not empty")
    }
    Ok(views.html.nav())
  }

  def login = Action {
    val data : Tuple2[String, String] = Tuple2("", "")
    Ok(views.html.login(credentials.fill(data)))
  }

  def authenticate(credentials :Tuple2[String, String]) = Action { implicit request =>
    Ok(views.html.nav()).withSession( "username" -> credentials._1, "id" -> credentials._2)
  }

  def parseCredentials = Action { implicit request =>
    credentials.bindFromRequest.fold(
      formWithErrors => {
        Logger.debug("inside error")
        BadRequest(views.html.login(formWithErrors))
      },
      advisor => {
        this.username = advisor._1
        this.id = advisor._2
        Redirect(routes.Application.index())
      }
    )
  }

  private val credentials : Form[Tuple2[String, String]] = Form(
    mapping(
      "username" -> text,
      "id" -> text
    )(Tuple2.apply)(Tuple2.unapply)
  )


}