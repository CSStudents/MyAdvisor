package models

case class Advisor(sin: String, name: String, workPhoneNumber: String, homePhoneNumber: String,
                   streetAddress: String, city: String, province: String, reports_To: String) {


  object advisor{



  }


  def lookBySin(sin: String): Boolean ={
    return true
  }

  def add(advisor: Advisor) ={
    //add advisor to db...
  }

}
