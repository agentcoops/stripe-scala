package com.stripe

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
  def refund(): Charge = request("POST", "%s/refund".format(instanceURL(this.id))).extract[Charge]
}

case class ChargeCollection(total_count: Option[Int] = None, data: List[Charge])

object Charge extends APIResource {
  def create(params: Map[String,_]): Charge =
    request("POST", classURL, params).extract[Charge]

  def retrieve(id: String): Charge =
    request("GET", instanceURL(id)).extract[Charge]

  def all(params: Map[String,_] = Map.empty): ChargeCollection =
    request("GET", classURL, params).extract[ChargeCollection]
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
  def update(params: Map[String,_]): Customer = {
    request("POST", instanceURL(this.id), params).extract[Customer]
  }

  def delete(): DeletedCustomer = {
    request("DELETE", instanceURL(this.id)).extract[DeletedCustomer]
  }

  def updateSubscription(params: Map[String,_]): Subscription = {
    request("POST", "%s/subscription".format(instanceURL(id)), params).extract[Subscription]
  }

  def cancelSubscription(params: Map[String,_] = Map.empty): Subscription = {
    request("DELETE", "%s/subscription".format(instanceURL(id)), params).extract[Subscription]
  }
}

case class DeletedCustomer(id: String, deleted: Boolean)

case class CustomerCollection(total_count: Option[Int] = None, data: List[Customer])

object Customer extends APIResource {
  def create(params: Map[String,_]): Customer = {
    request("POST", classURL, params).extract[Customer]
  }

  def retrieve(id: String): Customer = {
    request("GET", instanceURL(id)).extract[Customer]
  }

  def all(params: Map[String,_] = Map.empty): CustomerCollection = {
    request("GET", classURL, params).extract[CustomerCollection]
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
  def update(params: Map[String,_]): Plan = {
    request("POST", instanceURL(this.id), params).extract[Plan]
  }

  def delete(): DeletedPlan = {
    request("DELETE", instanceURL(this.id)).extract[DeletedPlan]
  }
}

case class PlanCollection(total_count: Option[Int] = None, data: List[Plan])

case class DeletedPlan(id: String, deleted: Boolean)

object Plan extends APIResource {
  def create(params: Map[String,_]): Plan = {
    request("POST", classURL, params).extract[Plan]
  }

  def retrieve(id: String): Plan = {
    request("GET", instanceURL(id)).extract[Plan]
  }

  def all(params: Map[String,_] = Map.empty): PlanCollection = {
    request("GET", classURL, params).extract[PlanCollection]
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
  def update(params: Map[String,_]): InvoiceItem = {
    request("POST", instanceURL(this.id), params).extract[InvoiceItem]
  }

  def delete(): DeletedInvoiceItem = {
    request("DELETE", instanceURL(this.id)).extract[DeletedInvoiceItem]
  }
}

case class DeletedInvoiceItem(id: String, deleted: Boolean)

case class InvoiceItemCollection(total_count: Option[Int] = None, data: List[InvoiceItem])

object InvoiceItem extends APIResource {
  def create(params: Map[String,_]): InvoiceItem = {
    request("POST", classURL, params).extract[InvoiceItem]
  }

  def retrieve(id: String): InvoiceItem = {
    request("GET", instanceURL(id)).extract[InvoiceItem]
  }

  def all(params: Map[String,_] = Map.empty): InvoiceItemCollection = {
    request("GET", classURL, params).extract[InvoiceItemCollection]
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
  def retrieve(id: String): Invoice = {
    request("GET", instanceURL(id)).extract[Invoice]
  }

  def all(params: Map[String,_] = Map.empty): InvoiceCollection = {
    request("GET", classURL, params).extract[InvoiceCollection]
  }

  def upcoming(params: Map[String, _]): Invoice = {
    request("GET", "%s/upcoming".format(classURL), params).extract[Invoice]
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
  def create(params: Map[String,_]): Token = {
    request("POST", classURL, params).extract[Token]
  }

  def retrieve(id: String): Token = {
    request("GET", instanceURL(id)).extract[Token]
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
  def delete(): DeletedCoupon = {
    request("DELETE", instanceURL(this.id)).extract[DeletedCoupon]
  }
}

case class CouponCollection(total_count: Option[Int] = None, data: List[Coupon])

case class DeletedCoupon(id: String, deleted: Boolean)

object Coupon extends APIResource {
  def create(params: Map[String,_]): Coupon = {
    request("POST", classURL, params).extract[Coupon]
  }

  def retrieve(id: String): Coupon = {
    request("GET", instanceURL(id)).extract[Coupon]
  }

  def all(params: Map[String,_] = Map.empty): CouponCollection = {
    request("GET", classURL, params).extract[CouponCollection]
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
  def retrieve: Account = {
    request("GET", singleInstanceURL).extract[Account]
  }
}
