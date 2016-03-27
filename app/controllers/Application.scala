package controllers

import play.api._
import play.api.mvc._
import play.api.db._
import play.api.Play.current
import play.twirl.api.Html

import play.api.cache.Cache //unsure if need, was in Heroku version

class Application extends Controller {

  def index = Action {
    val content = Html("<div></div>")
    Ok(views.html.main(content))
  }

}