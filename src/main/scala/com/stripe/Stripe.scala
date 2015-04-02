package com.stripe

import org.json4s._
import org.json4s.native.Serialization.write
import org.json4s.native.JsonMethods._
import org.json4s.native.JsonMethods.{render => jrender}
import org.json4s.JsonDSL._

case class CardCollection(count: Int, data: List[Card])

case class Card(
  last4: String,
  `type`: String,
  expMonth: Int,
  expYear: Int,
  fingerprint: String,
  country: Option[String],
  name: Option[String] = None,
  addressLine1: Option[String] = None,
  addressLine2: Option[String] = None,
  addressZip: Option[String] = None,
  addressState: Option[String] = None,
  addressCountry: Option[String] = None,
  cvcCheck: Option[String] = None,
  addressLine1Check: Option[String] = None,
  addressZipCheck: Option[String] = None) extends APIResource

case class Charge(
  created: Long,
  id: String,
  livemode: Boolean,
  paid: Boolean,
  amount: Int,
  currency: String,
  refunded: Boolean,
  disputed: Boolean,
  fee: Int,
  card: Card,
  failureMessage: Option[String],
  amountRefunded: Option[Int],
  customer: Option[String],
  invoice: Option[String],
  description: Option[String]) extends APIResource {
  def refund(): Charge = request("POST", "%s/refund".format(instanceURL(this.id))).extract[Charge]
}

case class ChargeCollection(count: Int, data: List[Charge])

object Charge extends APIResource {
  def create(params: Map[String,_]): Charge = {
    request("POST", classURL, params).extract[Charge]
  }

  def retrieve(id: String): Charge = {
    request("GET", instanceURL(id)).extract[Charge]
  }

  def all(params: Map[String,_] = Map.empty): ChargeCollection = {
    request("GET", classURL, params).extract[ChargeCollection]
  }

}

case class Customer(
  created: Long,
  id: String,
  livemode: Boolean,
  description: Option[String],
  cards: CardCollection,
  defaultCard: Option[String],
  email: Option[String],
  delinquent: Option[Boolean],
  subscription: Option[Subscription],
  discount: Option[Discount],
  accountBalance: Option[Int]) extends APIResource {
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

case class CustomerCollection(count: Int, data: List[Customer])

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
  trialPeriodDays: Option[Int]) extends APIResource {
  def update(params: Map[String,_]): Plan = {
    request("POST", instanceURL(this.id), params).extract[Plan]
  }

  def delete(): DeletedPlan = {
    request("DELETE", instanceURL(this.id)).extract[DeletedPlan]
  }
}

case class PlanCollection(count: Int, data: List[Plan])

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

case class InvoiceItemCollection(count: Int, data: List[InvoiceItem])

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

case class InvoiceCollection(count: Int, data: List[Invoice])

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

case class CouponCollection(count: Int, data: List[Coupon])

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
