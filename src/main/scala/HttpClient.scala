import Models.AuthTokenResponse
import sttp.client4.httpclient.cats.HttpClientCatsBackend
import sttp.client4._
import sttp.client4.circe._
import cats.effect._
import cats.syntax.all._
import io.circe._
import BudgetConfig.apiKey
import Models.AccountListResponse
import Models.TransactionResponse

trait HttpClient[F[_]] {

  def getAuthToken(): F[AuthTokenResponse]
  def getAccountList(accessToken: String): F[AccountListResponse]
  def getTransactionHistory(
      accountId: String,
      accessToken: String,
      from: String,
      to: String
  ): F[TransactionResponse]

}

class HttpClientImpl[F[_]: Sync]()(using sttpBackend: WebSocketBackend[F])
    extends HttpClient[F] {

  override def getAuthToken(): F[AuthTokenResponse] = {
    val request = basicRequest
      .post(
        uri"https://openapi.investec.com/identity/v2/oauth2/token"
      )
      .headers(
        Map(
          "x-api-key" -> apiKey,
          "Content-Type" -> "application/x-www-form-urlencoded"
        )
      )
      .auth
      .basic()
      .body()
      .response(asJson[AuthTokenResponse])

    request.send(sttpBackend).map(_.body).rethrow
  }

  override def getAccountList(accessToken: String): F[AccountListResponse] = {
    val request = basicRequest
      .get(
        uri"https://openapi.investec.com/za/pb/v1/accounts"
      )
      .auth
      .bearer(accessToken)
      .response(asJson[AccountListResponse])

    request.send(sttpBackend).map(_.body).rethrow
  }

  override def getTransactionHistory(
      accountId: String,
      accessToken: String,
      from: String,
      to: String
  ): F[TransactionResponse] = {
    val request = basicRequest
      .get(
        uri"https://openapi.investec.com/za/pb/v1/accounts/${accountId}/transactions?fromDate=${from}&toDate=${to}"
      )
      .auth
      .bearer(accessToken)
      .response(asJson[TransactionResponse])

    request.send(sttpBackend).map(_.body).rethrow
  }

}
