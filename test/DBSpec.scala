package test
import org.specs2.mutable._

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import play.api.test._
import play.api.test.Helpers._
import models._
import scala.slick.lifted.TableQuery

/**
 * Attention!!! To pass these tests, we need to commit export of the test data in the Global.scala
 */



class DBSpec extends Specification {
  "DB" should {
    "work as expected" in new WithApplication {

      // get test data
      val test_data = Imports.importFromCsvWithHeaders("app/us-500.csv",",")

      // generate phone entries to insert
      val phones500 = PhonesBook.toPhoneEntry(test_data)

      //create an instance of the table
      val Phones = TableQuery[PhonesBookTable]
      //see a way to architect your app in the computers-database play-slick sample
      //http://github.com/playframework/play-slick/tree/master/samples/play-slick-sample

      DB.withSession { implicit s: Session =>
        val testPhones = Seq(
          PhonesBookEntry(None,"John", "223356"),
          PhonesBookEntry(None,"Simon", "223357"),
          PhonesBookEntry(None,"Bob", "223358"))
        val testPhonesResult = Seq(
          PhonesBookEntry(Some(1),"John", "223356"),
          PhonesBookEntry(Some(2),"Simon", "223357"),
          PhonesBookEntry(Some(3),"Bob", "223358"))

        // testing insertion
        Phones.insertAll(testPhones: _*)
        Phones.list must equalTo(testPhonesResult)


        //testing query
        PhonesBook.find2(QueryD("",1,10)) must equalTo(Response(3,Seq(PhonesBookEntry(Some(1),"John", "223356"),PhonesBookEntry(Some(2),"Simon", "223357"),PhonesBookEntry(Some(3),"Bob", "223358"))))
        Phones.list.length must equalTo(3)

        //testing insertion
        PhonesBookEntry(None,"Smith", "223359").insert
        Phones.list.length must equalTo(4)
        PhonesBook.find2(QueryD("359",1,10)) must equalTo(Response(1,Seq(PhonesBookEntry(Some(4),"Smith", "223359"))))

        PhonesBookEntry(Some(4),"Smith", "223360").update
        PhonesBook.find2(QueryD("360",1,10)) must equalTo(Response(1,Seq(PhonesBookEntry(Some(4),"Smith", "223360"))))
        PhonesBook.find2(QueryD("359",1,10)) must equalTo(Response(0,Seq()))

        PhonesBookEntry(Some(4),"Smith", "223360").delete
        PhonesBook.find2(QueryD("360",1,10)) must equalTo(Response(0,Seq()))
        Phones.list.length must equalTo(3)

        // testing insertion of imported data

        Phones.insertAll(phones500: _*)
        Phones.list.length must equalTo(503)

        //testing number of rows

        PhonesBook.getNumberOfRows must equalTo(503)




      }
    }


    "select the correct testing db settings by default" in new WithApplication(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      DB.withSession { implicit s: Session =>
        s.conn.getMetaData.getURL must startWith("jdbc:h2:mem:play-test")
      }
    }

    "use the default db settings when no other possible options are available" in new WithApplication {
      DB.withSession { implicit s: Session =>
        s.conn.getMetaData.getURL must equalTo("jdbc:h2:mem:play")
      }
    }
  }
}

