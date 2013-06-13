package org.mkleine

import com.coremedia.cap.Cap
import com.coremedia.cap.content.Content
import com.coremedia.cap.content.publication.PublicationService
import util.Random
import collection.JavaConversions._

object PublicationGenerator {

  var segmentCount = 0

  def main(args: Array[String]) {
    val url = if (args.length > 0) args(0) else "http://localhost:41080/coremedia/ior"
    val pwd = if (args.length > 1) args(1) else ""
    val userName = if (args.length > 2) args(2) else "admin"
    val contentType = if (args.length > 3) args(3) else "Document_"
    val numberOfPublications = if (args.length > 4) args(4).toInt else 10

    // obtain required services
    val connection = Cap.connect(url, userName, pwd)
    val repo = connection.getContentRepository
    val queryService = repo.getQueryService
    val publicationService = repo.getPublicationService
    
    // fetch contents
    val contents = queryService.poseContentQuery("TYPE " + contentType).toSeq
    val randIndexes = Seq.fill(numberOfPublications)(Random.nextInt(contents.size))

    randIndexes.foreach(i => publish(contents(i),publicationService))

    exit(0)
  }

  def publish(content:Content, publicationService:PublicationService) {
    try {
      if (content.isCheckedIn) content.checkOut
      content.checkIn
      publicationService.approve(content.getCheckedInVersion)
      publicationService.publish(content)
      println("published content " + content)
    } catch {
      case e: Exception => println("ignoring exceptiont: " + e)
    }
  }

}
