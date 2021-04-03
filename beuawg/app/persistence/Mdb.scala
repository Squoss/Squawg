/*
 * The MIT License
 *
 * Copyright (c) 2021 Squeng AG
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package persistence

import javax.inject.Inject
import javax.inject.Singleton

import scala.concurrent._

import play.api._
import play.api.inject.ApplicationLifecycle

// In general, you should only need the org.mongodb.scala and org.bson namespaces in your code.
import org.mongodb.scala.{Document, MongoClient, MongoCollection}

// Replaces the default UuidCodec with one that uses the new standard UUID representation
// cf. https://mongodb.github.io/mongo-java-driver/4.2/driver-scala/tutorials/databases-collections/
import org.bson.UuidRepresentation
import org.bson.codecs.UuidCodec
import org.bson.codecs.configuration.CodecRegistries

@Singleton
class Mdb @Inject() (
    applicationLifecycle: ApplicationLifecycle,
    config: Configuration
) extends Logging {

  val codecRegistry = CodecRegistries.fromRegistries(
    CodecRegistries.fromCodecs(new UuidCodec(UuidRepresentation.STANDARD)),
    MongoClient.DEFAULT_CODEC_REGISTRY
  )

  logger.info("opening connection to MongoDB")
  private val client = MongoClient(config.get[String]("mongodb.uri"))
  private val database = client
    .getDatabase(config.get[String]("mongodb.db"))
    .withCodecRegistry(codecRegistry)
  logger.info("opened connection to MongoDB")

  def apply(collection: String): MongoCollection[Document] =
    database.getCollection(collection)

  applicationLifecycle.addStopHook(() => {
    logger.info("closing connection to MongoDB")
    Future.successful(client.close())
  })

}
