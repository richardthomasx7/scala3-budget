import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._
import io.circe.{Codec, Encoder, Json}

import io.circe.Decoder
import io.circe.derivation.{Configuration, ConfiguredDecoder, ConfiguredEncoder}
import scala.CanEqual.derived

object Models {
  given Configuration = Configuration.default.withSnakeCaseMemberNames

  case class AuthTokenResponse(accessToken: String, tokenType: String)
      derives ConfiguredDecoder

  case class AccountListResponse(data: Accounts) derives Decoder

  case class Accounts(accounts: List[Account]) derives Decoder

  case class Account(
      accountId: String,
      accountNumber: String,
      accountName: String
  ) derives Decoder

  case class TransactionResponse(
    data: TransactionsList
  ) derives Decoder

  case class TransactionsList(
    transactions: List[Transaction]
  ) derives Decoder

  case class Transaction(
    `type`: String,
    transactionType: Option[String], // Make enum?
    description: String,
    amount: Double
  ) derives Decoder
}
