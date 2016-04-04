package models

case class Office(streetAddress: String, postalCode: String, city: String, province: String,
                  mainOfficeNumber: String, var oid: String, var branches : List[String] ){


}
