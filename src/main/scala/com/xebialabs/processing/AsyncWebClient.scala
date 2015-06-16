package com.xebialabs.processing

import java.util.concurrent.Executor

import com.ning.http.client.{Response, AsyncHttpClient}

import scala.concurrent.{Future, Promise}
import scala.util.{Success, Failure, Try}

trait WebClient {

  def put(url: String, body: String)(implicit exec: Executor): Future[Response]

  def post(url: String, body: String)(implicit exec: Executor): Future[Response]
}

object AsyncWebClient extends WebClient {
  private val client = new AsyncHttpClient

  private def execute(rb: AsyncHttpClient#BoundRequestBuilder)(implicit exec: Executor): Future[Response] = {
    val p = Promise[Response]()
    val f = rb.execute()

    f.addListener(new Runnable {
      def run() = {
        Try(f.get) match {
          case Success(r) => p.success(r)
          case Failure(e) => p.failure(e)
        }
      }
    }, exec)
    p.future
  }

  def put(url: String, body: String)(implicit exec: Executor): Future[Response] = {
    execute(client.preparePut(url).setBody(body))
  }

  def post(url: String, body: String)(implicit exec: Executor): Future[Response] = {
    execute(client.preparePost(url).setBody(body))
  }

}

object AsyncWebClientError
