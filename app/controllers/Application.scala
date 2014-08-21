package controllers

import play.api.mvc._
import play.api.libs.json.{JsError, Json}
import models._


object Application extends Controller {

  //JSON read/write macro
  implicit val phonesBookEntryFormat = Json.format[PhonesBookEntry]
  implicit val queryFormat = Json.format[QueryD]
  implicit val responseFormat = Json.format[Response]


  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }


  def getData = Action(BodyParsers.parse.json) {request =>
    val q = request.body.validate[QueryD]
        q.fold(
        errors=>{BadRequest(Json.obj("status" ->"KO", "message" -> JsError.toFlatJson(errors)))},
        result=>{
          Ok(Json.toJson(PhonesBook.find2(result)))
        }
        )
  }


  def insertEntry = Action(BodyParsers.parse.json) { request =>

    val entryResult = request.body.validate[PhonesBookEntry]
    entryResult.fold(
      errors => {BadRequest(Json.obj("status" ->"KO", "message" -> JsError.toFlatJson(errors)))},
      entry => {

        if(PhonesBook.isPersisted(entry)){
          entry.insert
          // !!!! Pagination hardcoded
          // Ok(Json.toJson(PhonesBook.paged(1,10)))
          Ok(Json.toJson(PhonesBook.find2(QueryD("",1,10))))
        }
        else
          BadRequest(Json.obj("status" ->"KO", "message" -> "duplicated data"))
      }

    )
  }

  def deleteEntry = Action(BodyParsers.parse.json) { request =>

    val entryResult = request.body.validate[PhonesBookEntry]
    entryResult.fold(
      errors => {BadRequest(Json.obj("status" ->"KO", "message" -> JsError.toFlatJson(errors)))},
      entry => { entry.delete

        Ok(Json.toJson(PhonesBook.find2(QueryD("",1,10))))
      }

    )
  }

  def updateEntry = Action(BodyParsers.parse.json) { request =>

    val entryResult = request.body.validate[PhonesBookEntry]
    entryResult.fold(
      errors => {BadRequest(Json.obj("status" ->"KO", "message" -> JsError.toFlatJson(errors)))},
      entry => {
        entry.update
        Ok(Json.toJson(PhonesBook.find2(QueryD("",1,10))))
      }
    )
  }

}