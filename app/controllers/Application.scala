package controllers

import play.api._
import play.api.mvc._


import play.api.cache.Cache //unsure if need, was in Heroku version

class Application extends Controller {

  def index = Action {
    Ok(views.html.main(""))
  }


}