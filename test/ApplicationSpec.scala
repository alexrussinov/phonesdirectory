import models.{Response, QueryD, PhonesBookEntry}
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.libs.json.Json
import play.api.test._
import play.api.test.Helpers._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {
  implicit val queryFormat = Json.format[QueryD]

  "Application" should {

    "send 404 on a bad request" in new WithApplication{
      route(FakeRequest(GET, "/boum")) must beNone
    }

    "render the index page" in new WithApplication{
      val home = route(FakeRequest(GET, "/")).get

      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
      contentAsString(home) must contain ("Искать")
    }

    "insert as json" in new WithApplication() {
      implicit val phonesBookEntryFormat = Json.format[PhonesBookEntry]
      val json = Json.toJson(PhonesBookEntry(None,"Вася", "223356"))

      val result = route(FakeRequest(POST, "/insert").withJsonBody(json)).get
      status(result) must equalTo(OK)
      contentAsString(result) must contain ("Вася")
    }

    "get paged data as json" in new WithApplication() {

      implicit val phonesBookEntryFormat = Json.format[PhonesBookEntry]
      implicit val queryFormat = Json.format[QueryD]
      implicit val responseFormat = Json.format[Response]

      val json = Json.toJson(PhonesBookEntry(None,"Вася", "223356"))
      route(FakeRequest(POST, "/insert").withJsonBody(json)).get

      val result = route(FakeRequest(POST, "/entries").withJsonBody(Json.toJson(QueryD("",1,10)))).get
      status(result) must equalTo(OK)
      contentAsString(result) must contain ("Вася")
    }

    "delete as json" in new WithApplication() {
      implicit val phonesBookEntryFormat = Json.format[PhonesBookEntry]
      val json = Json.toJson(PhonesBookEntry(None,"Вася", "223356"))
      val jsonForDelete = Json.toJson(PhonesBookEntry(Some(1),"Вася", "223356"))
        route(FakeRequest(POST, "/insert").withJsonBody(json)).get

      val result = route(FakeRequest(POST, "/delete").withJsonBody(jsonForDelete)).get
      status(result) must equalTo(OK)
      contentAsString(result) must not contain ("Вася")
    }

    "update as json" in new WithApplication() {
      implicit val phonesBookEntryFormat = Json.format[PhonesBookEntry]
      val json = Json.toJson(PhonesBookEntry(None,"Вася", "223356"))
      val jsonForUpdate = Json.toJson(PhonesBookEntry(Some(2),"Ваня", "223356"))
      route(FakeRequest(POST, "/insert").withJsonBody(json)).get

      val result = route(FakeRequest(POST, "/update").withJsonBody(jsonForUpdate)).get
      status(result) must equalTo(OK)
      contentAsString(result) must contain ("Ваня")
    }

    "search as json" in new WithApplication() {
      implicit val phonesBookEntryFormat = Json.format[PhonesBookEntry]
      val json = Json.toJson(PhonesBookEntry(None,"Вася", "223356"))
      route(FakeRequest(POST, "/insert").withJsonBody(json)).get

      val result = route(FakeRequest(POST, "/entries").withJsonBody(Json.toJson(QueryD("Вас",1,10)))).get
      status(result) must equalTo(OK)
      contentAsString(result) must contain ("Вася")
    }
  }
}
