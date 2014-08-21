package models
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.TableQuery
import play.api.db.slick._
import play.api.Play.current
import play.api.libs.json.{JsValue, Json}

case class QueryD(query : String, page : Int, size : Int)

case class Response(numberOfPages : Int, data: Seq[PhonesBookEntry])


object PhonesBook {

  implicit val phonesBookEntryFormat = Json.format[PhonesBookEntry]

  val Query =  TableQuery[PhonesBookTable]


  def getNumberOfRows : Int = {
    DB.withSession { implicit s: Session =>

      Query.length.run

    }
  }

  def find2(query: QueryD) : Response = {
    val offset = (query.page - 1) * query.size
    DB.withSession { implicit s: Session =>
      if(query.query.isEmpty)
        Response(Query.length.run, Query.drop(offset).take(query.size).list)
      else{
      val q =  Query.filter(e=> ((e.name like "%"+query.query+"%") || (e.phone_number like "%"+query.query+"%")) )
      val number : Int = q.length.run
      val data = q.drop(offset).take(query.size).list
     // Map("data"->Json.toJson(data),"number"-> Json.toJson(number) )
      Response(number,data)
      }

    }
  }

  def isPersisted(entry : PhonesBookEntry): Boolean = {
    DB.withSession { implicit s: Session =>
      Query.filter(e=> (e.name === entry.name) && (e.phone_number === entry.phone_number)).list.isEmpty
    }
  }

  // generates seq. of products from src, we use this method while importing products from csv
  def toPhoneEntry(src : Seq[Map[String,String]]) : Seq[PhonesBookEntry] = {
    src map (s=>PhonesBookEntry(None,s("first_name")+" "+s("last_name"),s("phone1")) )
  }

  // generates seq. of products from src, we use this method while importing products from csv
  def toPhoneEntry2(src : Seq[Map[String,String]]) : Seq[PhonesBookEntry] = {
    src map (s=>PhonesBookEntry(None,s("first_name")+" "+s("last_name"),s("phone1")) )
  }



}

case class PhonesBookEntry(id: Option[Int] = None, name: String, phone_number: String){

  val Query =  TableQuery[PhonesBookTable]

  // insert phone entry in to the database
  def insert = {
    DB.withSession { implicit s: Session =>

      Query.insert(this)
    }
  }

  // update phone entry in the database
  def update = {
    DB.withSession { implicit s: Session =>

      Query.filter(_.id === this.id).update(this)
    }
  }

  // delete phone entry from database
  def delete = {
    DB.withSession { implicit s: Session =>

      Query.filter(_.id === this.id).delete
    }
  }
}


/*
Table mapping
 */

class PhonesBookTable(tag : Tag) extends Table[PhonesBookEntry](tag, "phones_table"){
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name",O.NotNull)
  def phone_number = column[String]("phone_number", O.NotNull)

  def * = (id.?, name, phone_number) <> (PhonesBookEntry.tupled , PhonesBookEntry.unapply _)

}
