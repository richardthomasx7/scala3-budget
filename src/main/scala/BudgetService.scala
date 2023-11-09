import cats.effect._
import cats.syntax.all._
import org.typelevel.cats.time._
import cats.syntax.all._
import java.time._
import java.time.format.DateTimeFormatter
trait BudgetService[F[_]] {
  def generateSpend(): F[List[(String, String)]]
}

class BudgetServiceImpl[F[_]: Sync](httpClient: HttpClient[F])
    extends BudgetService[F] {

  override def generateSpend(): F[List[(String, String)]] = {
    val categories = List("steam", "checkers", "woolworths", "dischem")
    val targetSpend = Map(
      "steam" -> 1000.0,
      "checkers" -> 2000.0,
      "woolworths" -> 2000.0,
      "dischem" -> 1500.0
    )
    for {
      res <- httpClient.getAuthToken()
      accounts <- httpClient.getAccountList(res.accessToken)
      today = LocalDate.now
      thirtyDaysAgo = today.minusDays(30)
      trans <- httpClient.getTransactionHistory(
        accounts.data.accounts
          .find(_.accountName == "Mr RW Thomas")
          .get
          .accountId,
        res.accessToken,
        thirtyDaysAgo.show,
        today.show
      )
      amounts <- categories
        .map { item =>
          val ac = trans.data.transactions.filter(
            _.description.toLowerCase().contains(item)
          )
          val totalAmounts = ac.foldLeft(0.0)((acc, curr) => acc + curr.amount)
          (item -> totalAmounts)
        }
        .pure[F]
      output <- amounts
        .map { amount =>
          val cat = amount._1
          val currentSpend = amount._2
          val isOverBudget = targetSpend.get(cat).get < currentSpend
          if (isOverBudget) {
            val overAmount = currentSpend - targetSpend.get(cat).get
            val resultText =
              s"Your target was R${targetSpend.get(cat).get}, and you spent R${currentSpend} which is R${overAmount} over the target"
            (cat -> resultText)
          } else {
            val underAmount = targetSpend.get(cat).get - currentSpend
            val resultText =
              s"Your target was R${targetSpend.get(cat).get}, and you spent R${currentSpend} which is R${underAmount} under the target"
            (cat -> resultText)
          }
        }
        .pure[F]

    } yield output
  }

}
