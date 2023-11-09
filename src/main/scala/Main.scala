import sttp.client4._
import cats.effect._
import cats.syntax.all._
import sttp.client4.httpclient.cats.HttpClientCatsBackend
import cats.effect.IOApp
import cats.effect.ExitCode
import sttp.client4.WebSocketBackend

object Main extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

    for {
      resp <- HttpClientCatsBackend.resource[IO]().use {
        case given WebSocketBackend[IO] =>
          val httpClient = new HttpClientImpl[IO]()
          val budget = new BudgetServiceImpl[IO](httpClient = httpClient)
          budget.generateSpend()
      }
      _ <- resp.foreach(item => println(item._1 + ": " + item._2)).pure[IO]
    } yield ExitCode.Success
  }
}
