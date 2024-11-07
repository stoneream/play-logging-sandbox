package controllers

import action.TraceableLoggingAction

import javax.inject.Inject
import models.Widget
import net.logstash.logback.argument.StructuredArguments.kv
import org.slf4j.LoggerFactory
import play.api.data._
import play.api.i18n._
import play.api.mvc._

import scala.collection._

/**
 * The classic WidgetController using MessagesAbstractController.
 *
 * Instead of MessagesAbstractController, you can use the I18nSupport trait,
 * which provides implicits that create a Messages instance from a request
 * using implicit conversion.
 *
 * See https://www.playframework.com/documentation/latest/ScalaForms#Passing-MessagesProvider-to-Form-Helpers
 * for details.
 */
class WidgetController @Inject() (
    traceableLoggingAction: TraceableLoggingAction,
    cc: MessagesControllerComponents
) extends MessagesAbstractController(cc) {
  private val logger = LoggerFactory.getLogger(this.getClass)

  import WidgetForm._

  private val widgets = mutable.ArrayBuffer(
    Widget("Widget 1", 123),
    Widget("Widget 2", 456),
    Widget("Widget 3", 789)
  )

  // The URL to the widget.  You can call this directly from the template, but it
  // can be more convenient to leave the template completely stateless i.e. all
  // of the "WidgetController" references are inside the .scala file.
  private val postUrl = routes.WidgetController.createWidget

  def index = traceableLoggingAction {
    Ok(views.html.index())
  }

  def listWidgets = traceableLoggingAction { implicit request =>
    // Pass an unpopulated form to the template
    Ok(views.html.listWidgets(widgets.toSeq, form, postUrl))
  }

  // This will be the action that handles our form post
  def createWidget = traceableLoggingAction { implicit request =>
    val errorFunction = { (formWithErrors: Form[Data]) =>
      // This is the bad case, where the form had validation errors.
      // Let's show the user the form again, with the errors highlighted.
      // Note how we pass the form with errors to the template.
      BadRequest(views.html.listWidgets(widgets.toSeq, formWithErrors, postUrl))
    }

    val successFunction = { (data: Data) =>
      // This is the good case, where the form was successfully parsed as a Data object.
      val widget = Widget(name = data.name, price = data.price)
      widgets += widget

      logger.info("Widget added {}, {}", kv("name", widget.name), kv("price", widget.price))

      Redirect(routes.WidgetController.listWidgets).flashing("info" -> "Widget added!")
    }

    val formValidationResult = form.bindFromRequest()
    formValidationResult.fold(errorFunction, successFunction)
  }
}
