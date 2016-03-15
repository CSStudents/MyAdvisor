package controllers

import play.api._
import play.api.mvc._
import play.api.db._
import play.api.Play.current //brings current application into scope

import play.api.cache.Cache //unsure if need, was in Heroku version

class Application extends Controller {
  
  def index = Action {
    Ok(views.html.index("Welcome to MyAdvisor."))
  }
  
  def db = Action {
    var out = ""
    val conn = DB.getConnection()
    try {
      val stmt = conn.createStatement

      val rs = stmt.executeQuery("SELECT * FROM employee")

      while (rs.next) {
        out += "Read from DB: " + rs.getString(2) + "\n"
      }
    } finally {
      conn.close()
    }
    Ok(out)
  }

}
