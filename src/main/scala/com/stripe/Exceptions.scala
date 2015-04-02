package com.stripe

sealed abstract class StripeException(msg: String, cause: Throwable = null) extends Exception(msg, cause)
case class APIException(msg: String, cause: Throwable = null) extends StripeException(msg, cause)
case class APIConnectionException(msg: String, cause: Throwable = null) extends StripeException(msg, cause)
case class CardException(msg: String, code: Option[String] = None, param: Option[String] = None) extends StripeException(msg)
case class InvalidRequestException(msg: String, param: Option[String] = None) extends StripeException(msg)
case class AuthenticationException(msg: String) extends StripeException(msg)

//represents Errors returned as JSON
case class ErrorContainer(error: Error)
case class Error(`type`: String, message: String, code: Option[String], param: Option[String])
