package models

import java.util.Date

case class ClientForm(cid: String, name: String, birthdate: Date, homephone: Int, workphone: Int,
                      streetAddress: String, city: String, province: String, postalCode: String) {

}