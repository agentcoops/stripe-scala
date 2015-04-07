package com.stripe

import scala.concurrent._
import ExecutionContext.Implicits.global

import org.json4s._
import org.json4s.native.Serialization.write
import org.json4s.native.JsonMethods._
import org.json4s.native.JsonMethods.{render => jrender}
import org.json4s.JsonDSL._

case class CardCollection(total_count: Option[Int] = None, data: List[Card])

case class Card(
  id: String,
  last4: String,
  exp_month: Int,
  exp_year: Int,
  funding: String,
  fingerprint: String,
  brand: String,
  recipient: Option[String] = None,
  customer: Option[String] = None,
  country: Option[String],
  name: Option[String] = None,
  address_line1: Option[String] = None,
  address_line2: Option[String] = None,
  address_zip: Option[String] = None,
  address_state: Option[String] = None,
  address_country: Option[String] = None,
  cvc_check: Option[String] = None,
  address_line1_check: Option[String] = None,
  address_zip_check: Option[String] = None,
  dynamic_last4: Option[String] = None
) extends APIResource


case class Charge(
  created: Long,
  id: String,
  livemode: Boolean,
  status: String,
  paid: Boolean,
  amount: Int,
  currency: String,
  captured: Boolean,
  refunded: Boolean,
  source: Card,
  balance_transaction: Option[String] = None,
  // TODO: dispute
  failure_code: Option[String] = None,
  failure_message: Option[String] = None,
  customer: Option[String] = None,
  invoice: Option[String] = None,
  description: Option[String] = None,
  // TODO: metadata
  // TODO: fraud details
  receipt_number: Option[String] = None,
  receipt_email: Option[String] = None,
  shipping: Option[String] = None,
  refunds: Option[Refunds] = None,
  application_fee: Option[String] = None
) extends APIResource {
  def refund(): Future[Charge] = request[Charge]("POST", "%s/refund".format(instanceURL(this.id)))
}

case class ChargeCollection(total_count: Option[Int] = None, data: List[Charge])

object Charge extends APIResource {
  def create(params: Map[String,_]): Future[Charge] =
    request[Charge]("POST", classURL, params)

  def retrieve(id: String): Future[Charge] =
    request[Charge]("GET", instanceURL(id))

  def all(params: Map[String,_] = Map.empty): Future[ChargeCollection] =
    request[ChargeCollection]("GET", classURL, params)
}

case class Refunds(
  total_count: Int,
  has_more: Boolean,
  url: String,
  data: List[Refund]
) extends APIResource

case class Refund(
  id: String,
  amount: Int,
  currency: String,
  created: Long,
  balance_transaction: String,
  charge: String,
  receipt_number: Option[String] = None,
  reason: Option[String] = None
) extends APIResource

case class Customer(
  created: Long,
  id: String,
  currency: String,
  livemode: Boolean,
  account_balance: Int,
  sources: CardCollection,
  description: Option[String] = None,
  email: Option[String] = None,
  delinquent: Option[Boolean] = None,
  subscription: Option[Subscription] = None,
  discount: Option[Discount] = None,
  default_source: Option[String] = None
) extends APIResource {
  def update(params: Map[String,_]): Future[Customer] = {
    request[Customer]("POST", instanceURL(this.id), params)
  }

  def delete(): Future[DeletedCustomer] = {
    request[DeletedCustomer]("DELETE", instanceURL(this.id))
  }

  def updateSubscription(params: Map[String,_]): Future[Subscription] = {
    request[Subscription]("POST", "%s/subscription".format(instanceURL(id)), params)
  }

  def cancelSubscription(params: Map[String,_] = Map.empty): Future[Subscription] = {
    request[Subscription]("DELETE", "%s/subscription".format(instanceURL(id)), params)
  }
}

case class DeletedCustomer(id: String, deleted: Boolean)

case class CustomerCollection(total_count: Option[Int] = None, data: List[Customer])

object Customer extends APIResource {
  def create(params: Map[String,_]): Future[Customer] = {
    request[Customer]("POST", classURL, params)
  }

  def retrieve(id: String): Future[Customer] = {
    request[Customer]("GET", instanceURL(id))
  }

  def all(params: Map[String,_] = Map.empty): Future[CustomerCollection] = {
    request[CustomerCollection]("GET", classURL, params)
  }
}

case class Plan(
  id: String,
  name: String,
  interval: String,
  amount: Int,
  currency: String,
  livemode: Boolean,
  trial_period_days: Option[Int] = None,
  interval_count: Option[Int] = None,
  subscriptions: Option[SubscriptionCollection] = None
) extends APIResource {
  def update(params: Map[String,_]): Future[Plan] = {
    request[Plan]("POST", instanceURL(this.id), params)
  }

  def delete(): Future[DeletedPlan] = {
    request[DeletedPlan]("DELETE", instanceURL(this.id))
  }
}

case class PlanCollection(total_count: Option[Int] = None, data: List[Plan])

case class DeletedPlan(id: String, deleted: Boolean)

object Plan extends APIResource {
  def create(params: Map[String,_]): Future[Plan] = {
    request[Plan]("POST", classURL, params)
  }

  def retrieve(id: String): Future[Plan] = {
    request[Plan]("GET", instanceURL(id))
  }

  def all(params: Map[String,_] = Map.empty): Future[PlanCollection] = {
    request[PlanCollection]("GET", classURL, params)
  }
}

case class Subscription(
  plan: Plan,
  start: Long,
  status: String,
  customer: String,
  cancelAtPeriodEnd: Option[Boolean],
  currentPeriodStart: Option[Long],
  currentPeriodEnd: Option[Long],
  endedAt: Option[Long],
  trialStart: Option[Long],
  trialEnd: Option[Long],
  canceledAt: Option[Long]) extends APIResource {
}

case class SubscriptionCollection(total_count: Option[Int] = None, data: List[Subscription])

case class NextRecurringCharge(amount: Int, date: String)

case class Discount(
  id: String,
  coupon: String,
  start: Long,
  customer: String,
  end: Option[Long]) extends APIResource {
}

case class InvoiceItem(
  id: String,
  amount: Int,
  currency: String,
  date: Long,
  livemode: Boolean,
  description: Option[String],
  invoice: Option[Invoice]) extends APIResource {
  def update(params: Map[String,_]): Future[InvoiceItem] = {
    request[InvoiceItem]("POST", instanceURL(this.id), params)
  }

  def delete(): Future[DeletedInvoiceItem] = {
    request[DeletedInvoiceItem]("DELETE", instanceURL(this.id))
  }
}

case class DeletedInvoiceItem(id: String, deleted: Boolean)

case class InvoiceItemCollection(total_count: Option[Int] = None, data: List[InvoiceItem])

object InvoiceItem extends APIResource {
  def create(params: Map[String,_]): Future[InvoiceItem] = {
    request[InvoiceItem]("POST", classURL, params)
  }

  def retrieve(id: String): Future[InvoiceItem] = {
    request[InvoiceItem]("GET", instanceURL(id))
  }

  def all(params: Map[String,_] = Map.empty): Future[InvoiceItemCollection] = {
    request[InvoiceItemCollection]("GET", classURL, params)
  }
}

case class InvoiceLineSubscriptionPeriod(start: Long, end: Long)
case class InvoiceLineSubscription(plan: Plan, amount: Int, period: InvoiceLineSubscriptionPeriod)
case class InvoiceLines(
  subscriptions: List[InvoiceLineSubscription],
  invoiceItems: List[InvoiceItem],
  prorations: List[InvoiceItem]) extends APIResource {
}

case class Invoice(
  date: Long,
  // id is optional since UpcomingInvoices don't have an ID.
  id: Option[String],
  periodStart: Long,
  periodEnd: Long,
  lines: InvoiceLines,
  subtotal: Int,
  total: Int,
  customer: String,
  attempted: Boolean,
  closed: Boolean,
  paid: Boolean,
  livemode: Boolean,
  attemptCount: Int,
  amountDue: Int,
  startingBalance: Int,
  endingBalance: Option[Int],
  nextPaymentAttempt: Option[Long],
  charge: Option[String],
  discount: Option[Discount]) {
}

case class InvoiceCollection(total_count: Option[Int] = None, data: List[Invoice])

object Invoice extends APIResource {
  def retrieve(id: String): Future[Invoice] = {
    request[Invoice]("GET", instanceURL(id))
  }

  def all(params: Map[String,_] = Map.empty): Future[InvoiceCollection] = {
    request[InvoiceCollection]("GET", classURL, params)
  }

  def upcoming(params: Map[String, _]): Future[Invoice] = {
    request[Invoice]("GET", "%s/upcoming".format(classURL), params)
  }
}

case class Token(
  id: String,
  created: Int,
  livemode: Boolean,
  used: Boolean,
  card: Card) extends APIResource {
}

object Token extends APIResource {
  def create(params: Map[String,_]): Future[Token] = {
    request[Token]("POST", classURL, params)
  }

  def retrieve(id: String): Future[Token] = {
    request[Token]("GET", instanceURL(id))
  }
}

case class Coupon(
  id: String,
  percentOff: Int,
  livemode: Boolean,
  duration: String,
  redeemBy: Option[Long],
  maxRedemptions: Option[Int],
  timesRedeemed: Option[Int],
  durationInMonths: Option[Int]) extends APIResource {
  def delete(): Future[DeletedCoupon] = {
    request[DeletedCoupon]("DELETE", instanceURL(this.id))
  }
}

case class CouponCollection(total_count: Option[Int] = None, data: List[Coupon])

case class DeletedCoupon(id: String, deleted: Boolean)

object Coupon extends APIResource {
  def create(params: Map[String,_]): Future[Coupon] = {
    request[Coupon]("POST", classURL, params)
  }

  def retrieve(id: String): Future[Coupon] = {
    request[Coupon]("GET", instanceURL(id))
  }

  def all(params: Map[String,_] = Map.empty): Future[CouponCollection] = {
    request[CouponCollection]("GET", classURL, params)
  }
}

case class Account(
  id: String,
  email: Option[String],
  statementDescriptor: Option[String],
  detailsSubmitted: Boolean,
  chargeEnabled: Boolean,
  currenciesSupported: Array[String]
) extends APIResource

object Account extends APIResource {
  def retrieve: Future[Account] = {
    request[Account]("GET", singleInstanceURL)
  }
}
