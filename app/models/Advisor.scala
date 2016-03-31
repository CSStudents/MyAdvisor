package models


case class Advisor(sin: String, name: String, workPhoneNumber: String, homePhoneNumber: String,
                   streetAddress: String, city: String, province: String, reports_To: String, var branches : List[String]) {

  object Advisor {

    //how do you get access to this outside scope of Advisor?
    def save(form : AdvisorForm): Unit ={
      // call db and save
    }
  }



  def lookBySin(sin: String): Boolean ={
    true
  }


//  def addBranches(newBranches: List[Serializable]) ={
//    branches = newBranches :: branches
//  }
//
//  def getBranches : List[String] ={
//    branches
//  }


}
