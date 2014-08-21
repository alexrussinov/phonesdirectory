import models.{PhonesBook, Imports, PhonesBookEntry, PhonesBookTable}
import play.api.db.slick._
import play.api.{Application, GlobalSettings}
import play.api.db.slick.Config.driver.simple._
import scala.slick.lifted.TableQuery
import play.api.Play.current

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 17/08/14
 * Time: 16:36
 * To change this template use File | Settings | File Templates.
 */
object Global extends GlobalSettings  {
    override def onStart(app: Application){

      //create an instance of the table
      val Phones = TableQuery[PhonesBookTable]

      // get test data
      val test_data = Imports.importFromCsvWithHeaders("app/us-500.csv",",")

      // generate phones entries for insert data
      val phones500 = PhonesBook.toPhoneEntry(test_data)

      DB.withSession { implicit s: Session =>
        Phones.insertAll(phones500: _*)
      }

    }
}
